package com.commerzo.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.commerzo.auth.model.Users;
import com.commerzo.auth.service.UserService;
import com.commerzo.auth.transferobjects.AuthResponse;
import com.commerzo.auth.transferobjects.RefreshTokenRequest;
import com.commerzo.auth.transferobjects.UserRequestDTO;
import com.commerzo.auth.transferobjects.UserResponseDTO;
import com.commerzo.common_config.exception.UserException;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	
	@Autowired
	UserService userService;
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> loginUser(@RequestParam String username, @RequestParam String password) throws UserException {
		AuthResponse authResponse = userService.varify(username,password);
		//return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK).body(authResponse);
	}
	
	@PostMapping("/refresh-token")
	public ResponseEntity<AuthResponse> refreshTokenLogin(@RequestBody RefreshTokenRequest refreshTokenRequest) throws UserException {
		String refreshToken = refreshTokenRequest.getRefreshToken();
		AuthResponse authResponse = userService.varifyRefreshToken(refreshToken);
		return ResponseEntity.status(HttpStatus.OK).body(authResponse);
		//return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
	}
}
