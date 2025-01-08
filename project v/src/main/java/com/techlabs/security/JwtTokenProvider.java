package com.techlabs.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.techlabs.exception.StudentApiException;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("0hfC9JEsNVaLjG2KvZJeo3I3ktBETbCguaSP5zQrb+Y=") // Replace with actual secret key
    private String jwtSecret;

    @Value("604800000") // One week in milliseconds
    private long jwtExpirationDate;

    // Generate JWT token with mobileNumber
    public String generateToken(Authentication authentication) {
        String mobileNumber = authentication.getName(); // Spring Security uses mobileNumber as the principal
 
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
 
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);
 
        return Jwts.builder()
                .setSubject(mobileNumber) // Use mobileNumber as the subject
                .claim("roles", roles)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }

    // Decode the secret key
    private Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    // Get mobileNumber from JWT token
    public String getMobileNumber(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); // Extract the subject (mobileNumber in this case)
    }

    // Validate JWT token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException ex) {
            throw new StudentApiException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new StudentApiException(HttpStatus.BAD_REQUEST, "Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new StudentApiException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new StudentApiException(HttpStatus.BAD_REQUEST, "JWT claims string is empty.");
        }
    }
}
