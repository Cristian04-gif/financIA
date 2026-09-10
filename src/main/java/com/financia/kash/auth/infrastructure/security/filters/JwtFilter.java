package com.financia.kash.auth.infrastructure.security.filters;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.financia.kash.auth.infrastructure.security.service.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Component
@Log4j2
@RequiredArgsConstructor
public class JwtFilter implements WebFilter {

    private final JwtService jwtService;
    private final ReactiveUserDetailsService userDetailsService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        String path = exchange.getRequest().getPath().value();

        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authorization.substring(7);

        try {
            boolean isTokenExpired = jwtService.isTokenExpired(token);

            boolean canByTokenRenewed = jwtService.canByTokenRenewed(token);

            if (isTokenExpired && !canByTokenRenewed) {
                return unauthorized(exchange,
                        "Token no válido o usuario ya autenticado");
            }

            Boolean isPreAuth = jwtService.isPreAuthToken(token);

            if (Boolean.TRUE.equals(isPreAuth)
                    && !path.contains("/api/v1/auth/verify-2fa")) {

                return unauthorized(exchange,
                        "Se requiere completar la verificación de dos pasos (2FA)");
            }

            String username = jwtService.getUsername(token);

            return userDetailsService.findByUsername(username)
                    .flatMap(userDetails -> {

                        boolean isValidToken = jwtService.isValidToken(
                                token,
                                userDetails);

                        if (!isValidToken) {
                            return unauthorized(
                                    exchange,
                                    "El token proporcionado no es válido");
                        }

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                        return continueWithAuthentication(
                                exchange,
                                chain,
                                authentication);
                    })
                    .onErrorResume(Exception.class, e -> {
                        log.error(
                                "Error de autenticación: {}",
                                e.getMessage());

                        return unauthorized(
                                exchange,
                                e.getMessage());
                    });

        } catch (Exception e) {
            log.error(
                    "Error de autenticación: {}",
                    e.getMessage());

            return unauthorized(
                    exchange,
                    e.getMessage());
        }
    }

    private Mono<Void> continueWithAuthentication(
            ServerWebExchange exchange,
            WebFilterChain chain,
            Authentication authentication) {

        return chain.filter(exchange)
                .contextWrite(
                        ReactiveSecurityContextHolder
                                .withAuthentication(authentication));
    }

    private boolean isPublicEndpoint(String path) {
        return path.equals("/api/v1/auth/login")
                || path.equals("/api/v1/auth/register")
                || path.equals("/api/v1/auth/verify-2fa");
    }

    private Mono<Void> unauthorized(
            ServerWebExchange exchange,
            String message) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse()
                .setComplete();
    }

    // @Override
    // protected void doFilterInternal(HttpServletRequest request,
    // HttpServletResponse response, FilterChain filterChain)
    // throws ServletException, IOException {
    // String authorization = request.getHeader("Authorization");
    // String path = request.getRequestURI();

    // if (path.contains("/api/v1/auth/login") ||
    // path.contains("/api/v1/auth/register") ||
    // path.contains("/api/v1/auth/verify-2fa")) {

    // filterChain.doFilter(request, response);
    // return;
    // }
    // if (authorization == null || !authorization.startsWith("Bearer ")) {
    // filterChain.doFilter(request, response);
    // return;
    // }

    // String token = authorization.substring(7);

    // try {
    // boolean isTokenExpired = jwtService.isTokenExpired(token);
    // boolean canByTokenRenewed = jwtService.canByTokenRenewed(token);
    // if (isTokenExpired && !canByTokenRenewed) {
    // throw new JwtException("Token no válido o usuario ya autenticado");
    // }

    // Boolean isPreAuth = jwtService.isPreAuthToken(token);
    // if (isPreAuth != null && isPreAuth) {
    // if (!path.contains("/api/v1/auth/verify-2fa")) {
    // throw new JwtException("Acceso denegado: Se requiere completar la
    // verificación de dos pasos (2FA)");
    // }
    // }

    // String username = jwtService.getUsername(token);

    // UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    // boolean isValidToken = jwtService.isValidToken(token, userDetails);

    // if (!isValidToken && SecurityContextHolder.getContext().getAuthentication()
    // != null) {
    // throw new JwtException("El token proproporcionado no es valido");
    // }

    // if (isTokenExpired && canByTokenRenewed) {
    // String renewToken = jwtService.renewToken(token, userDetails);
    // response.setHeader("Authorization", "Bearer " + renewToken);
    // }

    // UsernamePasswordAuthenticationToken authenticationToken = new
    // UsernamePasswordAuthenticationToken(
    // userDetails, null, userDetails.getAuthorities());

    // authenticationToken.setDetails(new
    // WebAuthenticationDetailsSource().buildDetails(request));
    // SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    // filterChain.doFilter(request, response);
    // } catch (Exception e) {
    // log.error("error de autenticacion, {}", e.getMessage());
    // SecurityContextHolder.clearContext();

    // resolver.resolveException(request, response, null, e);
    // }
    // }
}
