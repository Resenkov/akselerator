package ru.dstu.work.akselerator.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long validityMs;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret,
                            @Value("${app.jwt.expiration-ms}") long validityMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.validityMs = validityMs;
    }

    public String createToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        Date now = new Date();
        Date exp = new Date(now.getTime() + validityMs);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Validates a token while also exposing whether it is expired. This is primarily
     * helpful for token introspection flows where we still want to read the claims of
     * an expired token.
     */
    public boolean isExpired(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return false;
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }

    public Claims parseClaimsAllowExpired(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException ex) {
            return ex.getClaims();
        }
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Object rolesObj = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("roles");
        if (rolesObj instanceof List) {
            return ((List<?>) rolesObj).stream().map(Object::toString).collect(Collectors.toList());
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(Claims claims) {
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof List) {
            return ((List<?>) rolesObj).stream().map(Object::toString).collect(Collectors.toList());
        }
        return List.of();
    }
}
