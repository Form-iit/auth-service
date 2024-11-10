package com.example.authservice.configs.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${auth.jwt.secret-key}")
  private String SECRET_KEY;

  // * Step4
  public String extractId(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  // * Step3
  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = validateTokenAndExtractAllClaims(token);
    return claimsResolver.apply(
        claims); // ? the resolver will be a function which extracts the claim by given name
  }

  // * Step1
  private Claims validateTokenAndExtractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSignatureKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  // * Step2
  private SecretKey getSignatureKey() {
    byte[] bytesSecretKey = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(bytesSecretKey);
  }

  // * Step5
  public boolean tokenIsValid(String jwt, UserDetails userDetails) {
    String UserId = extractId(jwt);
    return userDetails.getUsername().equals(UserId) && !isTokenExpired(jwt);
  }

  // * Step7
  private boolean isTokenExpired(String jwt) {
    return extractExpirationDate(jwt).before(new Date());
  }

  // * Step6
  private Date extractExpirationDate(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  // ?===================================================================================================================
  // * generate a token
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return generateToken(claims, userDetails);
  }

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts.builder()
        .claims()
        .add(extraClaims)
        .subject(userDetails.getUsername())
        .issuedAt(new Date(System.currentTimeMillis())) // * sets the creation date of the token
        .expiration(
            new Date(
                System.currentTimeMillis()
                    + 60 * 60
                        * 1000)) // * sets an expiration date for the token 1 hour from now.and()
        .and()
        .signWith(getSignatureKey()) // * signs the token
        .compact(); // * creates the token
  }
}
