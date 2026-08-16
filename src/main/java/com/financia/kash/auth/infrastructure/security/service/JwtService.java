package com.financia.kash.auth.infrastructure.security.service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.financia.kash.usuario.application.port.output.JwtForUsersPort;
import com.financia.kash.usuario.domain.model.User;
import com.financia.kash.usuario.infrastructure.adapter.database.entity.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService implements JwtForUsersPort {

    private final String SECRECT_KEY = "404E6352165564586E3272357538782F413F4428472848625064536756685970";
    private final long TOKEN_EXPIRATION = 1000 * 60 * 60 * 24;
    private final long REFRESH_WINDOW = 1000 * 60 * 60 * 24 * 7;

    private String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        UUID id = null;
        if (userDetails instanceof UserEntity) {
            UserEntity entity = (UserEntity) userDetails;
            id = entity.getId();
        }
        Map<String, Object> claims = Map.of(
                "authorities", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList(),
                "userId", id);
        return generateToken(claims, userDetails.getUsername());
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRECT_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new RuntimeException("Token JWT no válido o mal formado " + e);
        }
    }

    private <T> T getClaim(String token, Function<Claims, T> claimsMapper) {
        Claims claims = getAllClaims(token);
        return claimsMapper.apply(claims);
    }

    public String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public Date getExipationDate(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        return getExipationDate(token).before(new Date());
    }

    public boolean canByTokenRenewed(String token) {
        return getExipationDate(token).before(new Date(System.currentTimeMillis() + REFRESH_WINDOW));

    }

    public String renewToken(String token, UserDetails userDetails) {
        if (!canByTokenRenewed(token)) {
            throw new RuntimeException("El token no se puede renovar");
        }
        return generateToken(userDetails);
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        String username = getUsername(token);
        return username.equals(userDetails.getUsername());
    }

}
