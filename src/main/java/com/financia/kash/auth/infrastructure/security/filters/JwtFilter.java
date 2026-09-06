package com.financia.kash.auth.infrastructure.security.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.financia.kash.auth.infrastructure.security.service.JwtService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private final HandlerExceptionResolver resolver;

    public JwtFilter(JwtService jwtService, UserDetailsService userDetailsService,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        String path = request.getRequestURI();

        if (path.contains("/api/v1/auth/login") ||
                path.contains("/api/v1/auth/register") ||
                path.contains("/api/v1/auth/verify-2fa")) {

            filterChain.doFilter(request, response);
            return;
        }
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        try {
            boolean isTokenExpired = jwtService.isTokenExpired(token);
            boolean canByTokenRenewed = jwtService.canByTokenRenewed(token);
            if (isTokenExpired && !canByTokenRenewed) {
                throw new JwtException("Token no válido o usuario ya autenticado");
            }

            Boolean isPreAuth = jwtService.isPreAuthToken(token);
            if (isPreAuth != null && isPreAuth) {
                if (!path.contains("/api/v1/auth/verify-2fa")) {
                    throw new JwtException("Acceso denegado: Se requiere completar la verificación de dos pasos (2FA)");
                }
            }

            String username = jwtService.getUsername(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            boolean isValidToken = jwtService.isValidToken(token, userDetails);

            if (!isValidToken && SecurityContextHolder.getContext().getAuthentication() != null) {
                throw new JwtException("El token proproporcionado no es valido");
            }

            if (isTokenExpired && canByTokenRenewed) {
                String renewToken = jwtService.renewToken(token, userDetails);
                response.setHeader("Authorization", "Bearer " + renewToken);
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("error de autenticacion, {}", e.getMessage());
            SecurityContextHolder.clearContext();

            resolver.resolveException(request, response, null, e);
        }
    }
}
