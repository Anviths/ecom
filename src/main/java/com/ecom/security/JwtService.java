package com.ecom.security;

import com.ecom.entity.Role;
import com.ecom.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value(value = "${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration-ms:3600000}")
    private Long expiryTime;

    private SecretKey siningKey() {
        return Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(jwtSecret)
        );
    }

    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream().map(Role::name).toList());
        claims.put("tv", user.getTokenVersion());
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiryTime);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(siningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(siningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(siningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(siningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(siningKey())
                .build()
                .parseClaimsJws(token).getBody();
        Object roles = claims.get("roles");

        if (roles instanceof List) {
            return ((List<?>) roles).stream().map(Object::toString).toList();
        }
        return Collections.emptyList();
    }

    public int extractTokenVersion(String token){

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(siningKey())
                .build()
                .parseClaimsJws(token).getBody();
        return claims.get("tv", Integer.class);


    }
}

