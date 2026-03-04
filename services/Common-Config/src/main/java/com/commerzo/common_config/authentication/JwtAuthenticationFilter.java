package com.commerzo.common_config.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	// Replace this with your actual secret key
	@Value("${auth.jwt.secret}")
	private String SECRET_KEY;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String authHeader = request.getHeader("Authorization");

			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String token = authHeader.substring(7);

				Claims claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY))).build()
						.parseSignedClaims(token).getPayload();

				String username = claims.getSubject();
				String tokenType = (String) claims.get("type");
				log.info("token type in filter found to be " + tokenType);

		        if (!"access".equals(tokenType)) {
		            // If someone tries to use a Refresh token for an API call, reject it
		            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		            response.getWriter().write("Only Access Tokens are allowed for this request.");
		            return; 
		        }

				ArrayList<String> roles = (ArrayList<String>) claims.get("Roles");
				Collection<SimpleGrantedAuthority> authorities = roles.stream().map(role -> role.trim())
						.map(SimpleGrantedAuthority::new).collect(Collectors.toList());

				Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			// Invalid token - do nothing or log
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": \"Token Expired\"}");
		} catch (JwtException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("Invalid Token");
		}

	}
}
