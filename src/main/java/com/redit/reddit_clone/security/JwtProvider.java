package com.redit.reddit_clone.security;

import com.redit.reddit_clone.exceptions.SpringRedditException;
import com.redit.reddit_clone.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;

import static io.jsonwebtoken.Jwts.parser;

@Service
public class JwtProvider {
    private KeyStore keyStore;


    @Value("${jwt.expiration.time}")
    @Getter
    private Long jwtExpirationTime;
    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(resourceAsStream, "secret".toCharArray());
        } catch (Exception e) {
            throw new SpringRedditException("Exception occured while loading keyStore");
        }
    }

    public String generateToken(Authentication authentication) {
        org.springframework.security.core.userdetails.User user = (User) authentication.getPrincipal();
        return Jwts.builder()
                .subject(user.getUsername())
                .signWith(getPrivateKey())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(jwtExpirationTime)))
                .compact();
    }
    public String generateTokenWithUsername(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(Instant.now()))
                .signWith(getPrivateKey())
                .expiration(Date.from(Instant.now().plusMillis(jwtExpirationTime)))
                .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey("springblog","secret".toCharArray());
        }
        catch (Exception e){
            throw new SpringRedditException("Exception occured while retrieving public key from keyStore");
        }
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .setSigningKey(getPublicKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            throw new SpringRedditException("Invalid JWT token "+e);
        }
    }
    private PublicKey getPublicKey(){
        try {
            return keyStore.getCertificate("springblog").getPublicKey();
        }
        catch (Exception e){
            throw new SpringRedditException("Exception occured while retrieving public key");
        }
    }

    public String getUsernameFromJwt(String jwt){
        Claims claims = parser()
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
        return claims.getSubject();
    }
}
