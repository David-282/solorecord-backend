package com.solorecord.solorecord_backend.identity.security;

import com.solorecord.solorecord_backend.identity.data.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;



    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes()) ;
    }


    public String generateToken(User user){
        return Jwts.builder().subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact()

                ;
    }

    public String extractEmail(String token){

        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public Date extractExpirationDate (String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    public boolean isTokenExpired (String token){
         return (extractExpirationDate(token).before(new Date()));

    }

    public boolean  validateToken (String token, UserDetails userDetails){
        final String email =  extractEmail(token);

        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
