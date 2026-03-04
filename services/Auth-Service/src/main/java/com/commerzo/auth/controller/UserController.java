package com.commerzo.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.commerzo.auth.service.UserService;
import com.commerzo.auth.transferobjects.UserRequestDTO;
import com.commerzo.auth.transferobjects.UserResponseDTO;
import com.commerzo.common_config.exception.UserException;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
	
	@Autowired
	UserService userService;

	@Autowired
	PasswordEncoder bCryptPasswordEncoder;
	
	@PostMapping("/new")
	public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRequestDTO userDTO) throws UserException {
		UserResponseDTO returnedUserDTO = userService.saveUser(userDTO);
		return ResponseEntity.status(HttpStatus.OK).body(returnedUserDTO);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> getUserDetails(@PathVariable Long id) throws UserException {
		UserResponseDTO userDTO = userService.getUserDetails(id);
		return ResponseEntity.status(HttpStatus.OK).body(userDTO);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable Long id) throws UserException {
		userService.deleteUser(id);
		return ResponseEntity.status(HttpStatus.OK).body("User deleted");
	}
	
	@PatchMapping("/{id}")//Updating the user
	public ResponseEntity<UserResponseDTO> updateUserInfo(@PathVariable Long id, @RequestBody UserRequestDTO userDTO) throws UserException {
		UserResponseDTO updatedUserDTO = userService.updateUser(id,userDTO);
		return ResponseEntity.status(HttpStatus.OK).body(updatedUserDTO);
	}
}
