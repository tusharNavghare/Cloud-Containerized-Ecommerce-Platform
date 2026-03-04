package com.commerzo.auth.service;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.commerzo.auth.config.SecurityConfiguration;
import com.commerzo.auth.model.UserPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.lang.Collections;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	private static final Logger log = LoggerFactory.getLogger(JwtService.class);
	
	@Value("${auth.jwt.secret}")
	private String secretKey;
	
	private SecretKey getKey() {
		log.info("Secret Key is " + secretKey);
		byte[] skbytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(skbytes);	
	}
	
	protected String generateToken(UserPrincipal principal) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("Roles", Optional.ofNullable(principal.getAuthorities()).orElse(Collections.emptyList()).stream()
				.map(authority -> authority.getAuthority()).collect(Collectors.toList()));
		claims.put("type", "access");
		return Jwts.builder()
				.claims()
				.add(claims)
				.subject(principal.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 60 * 30 * 1000))
				.and()
				.signWith(getKey())
				.compact();
	}

	protected String generateRefreshToken(UserPrincipal principal) {
		return Jwts.builder()
				.claim("type", "refresh")
				.subject(principal.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000))
				.signWith(getKey())
				.compact();
	}
	
	
	public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }
	
	public String extractTokenType(String token) {
		return extractClaim(token, claims -> claims.get("type", String.class));
	}

		
    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
    	Claims claims = extractAllclaims(token);
    	return claimsResolver.apply(claims);
		
	}

	private Claims extractAllclaims(String token) {
		return Jwts.parser()
				.verifyWith(getKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public List extractAuthorities(String token) {
        Claims claims = extractAllclaims(token);
        List<String> roles = (List) claims.get("Roles");
        if(roles != null && !roles.isEmpty()) {
        	return (List) roles.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
        }
        return null;
    }

}
