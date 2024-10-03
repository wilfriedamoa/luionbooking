package com.lunionlab.booking.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lunionlab.booking.emum.JwtAudience;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

@Service
public class JwtService {
    @Value("${jwt.time.exp}")
    private Integer TOKEN_EXPIRATION;

    private String secret = "LUNIONbooking@2024";

    public String generateToken(String email) {
        int time = TOKEN_EXPIRATION * 60;
        Date now = new Date();
        Date DateExp = Date.from(now.toInstant().plus(time, ChronoUnit.MINUTES));
        return Jwts.builder().setAudience(JwtAudience.USER).setSubject(email).setExpiration(DateExp)
                .signWith(SignatureAlgorithm.HS256, this.secret)
                .compact();
    }

    public Map<String, String> getIdentifierFromToken(String token) {
        Map<String, String> ouput = new HashMap<>();
        ouput.put("identifier", "");
        ouput.put("audience", "");
        try {
            Claims claim = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
            ouput.put("identifier", claim.getSubject());
            ouput.put("audience", claim.getAudience());
            return ouput;
        } catch (Exception e) {
            return ouput;
        }
    }

}
