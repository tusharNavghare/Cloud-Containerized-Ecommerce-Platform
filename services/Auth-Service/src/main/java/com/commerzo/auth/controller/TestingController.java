package com.commerzo.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.commerzo.auth.model.Users;
import com.commerzo.auth.service.JwtService;
import com.commerzo.auth.service.UserService;
import com.commerzo.common_config.exception.UserException;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/testing")
public class TestingController {

    @Autowired
	UserService userService;
	
	@Autowired
	JwtService jwtService;
	
	@GetMapping("/test")
	public String getUsername() throws UserException {
		UsernamePasswordAuthenticationToken authentication= (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails)authentication.getPrincipal();
		if(userDetails != null) {
			return "Hello " + userDetails.getUsername() + " With " + userDetails.getAuthorities();
		}
		return "No user Found";
	}

}
