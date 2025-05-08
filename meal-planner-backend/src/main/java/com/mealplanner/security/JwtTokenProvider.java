package com.mealplanner.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {
	
	private static final String token = "cuAihCz53DZRjZwbsGcZJ2Ai6At+T142uphtJMsk7iQ=";
    private static final long validityInMs= 15*60*1000L;
	
	private Key secretKey;
	
	@PostConstruct
	public void init() {
		
		byte[] keyBytes = Base64.getDecoder().decode(token);
		this.secretKey = Keys.hmacShaKeyFor(keyBytes);
	}
	
	public String createToken(Long userId, Set<String> roles) {
        Claims claims = Jwts.claims().setSubject(userId.toString());
        claims.put("roles", roles);
        Date now = new Date();
        Date exp = new Date(now.getTime() + validityInMs);
        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(now)
                   .setExpiration(exp)
                   .signWith(secretKey, SignatureAlgorithm.HS256)
                   .compact();
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(secretKey)
                   .build()
                   .parseClaimsJws(token);
    }
}
