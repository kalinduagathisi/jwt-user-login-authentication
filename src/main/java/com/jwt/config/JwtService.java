package com.jwt.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // provide secret key
    // min: 256 bytes
    private final String SECRET_KEY = "MRq7cWXff9QXV5SBzpCaygbwS1gEkxzAoZmKAiRWlGOrhzVJ91ts5BLQSmpnERHTxrTLxak5jMpNVCj4XcMEMeWeSeDzRW4OpE5Gd3yIiu0ly1XdHE2v2eZhIysTgqAZeUV9COysjIrxTl/V/fahWfaqDpIglKGbeeIWnB8AQNu3iLFucXf7J3Op173Mj4jjwvkASLHMq8VWyDlYuAi069XkVZerrLqXVrV4/iaYqZxRht+5nqL3M9yvUw610m10s8kRfEirHDj/sZ2NnWciPF+26hjWA9T0MbQgQ/5n7b8aqBA2LLa8kfoRWtjnQcNtqhJo1eZGpA9JWc4yJjo1QkDgy7m7LwxfNla0A58F0+8=\n";

    // generate token only with username
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }


    // generate token with extra claims
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ 1000*60*24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);  // username contains in the subject of the jwt
    }

    // extract one claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // extract all claims
    public Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // validate token
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
