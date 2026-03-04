package com.commerzo.auth.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.commerzo.auth.model.UserPrincipal;
import com.commerzo.auth.model.Users;
import com.commerzo.auth.repository.UserRepo;
import com.commerzo.auth.transferobjects.AuthResponse;
import com.commerzo.auth.transferobjects.UserRequestDTO;
import com.commerzo.auth.transferobjects.UserResponseDTO;
import com.commerzo.common_config.exception.UserException;

@Service
public class UserService {

	UserRepo userRepo;

	JwtService jwtService;

	CustomUserDetailService customUserDetailService;

	AuthenticationManager authenticationManager;

	PasswordEncoder bCryptPasswordEncoder;
	
	public UserService(UserRepo userRepo, JwtService jwtService, CustomUserDetailService customUserDetailService,
			AuthenticationManager authenticationManager, PasswordEncoder bCryptPasswordEncoder) {
		this.userRepo = userRepo;
		this.jwtService = jwtService;
		this.customUserDetailService = customUserDetailService;
		this.authenticationManager = authenticationManager;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	public UserResponseDTO saveUser(UserRequestDTO userDTO) throws UserException {
		if(!validateUserFields(userDTO)) {
			throw new UserException("Mendatory fields not filled",HttpStatus.BAD_REQUEST);
		}
		if(userNameAlreadyExists(userDTO.getUsername())) {
			throw new UserException("User by same username already exists",HttpStatus.CONFLICT);
		}
		try {
			Users users = new Users(userDTO.getUsername(), bCryptPasswordEncoder.encode(userDTO.getPassword()),
					userDTO.getRoles(), userDTO.getPassword());
			users = userRepo.save(users);
			return new UserResponseDTO(users.getId(), users.getUsername(), users.getRoles());
		} catch (Exception e) {
			throw new UserException("Unable to save the user " + e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
	}

	private boolean userNameAlreadyExists(String username) throws UserException {
		// TODO Auto-generated method stub
		List<Users> users = null;
		try {
			users = userRepo.getUsersByUsername(username);
		}catch(Exception e) {
			throw new UserException("Unable to validate incoming user",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(users != null && !users.isEmpty()) {
			return true;
		}else {
			return false;
		}
	}

	public AuthResponse varify(String username, String password) throws UserException {
		// TODO Auto-generated method stub
		try{
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			if (authentication.isAuthenticated()) {
				String accessToken = jwtService.generateToken((UserPrincipal) authentication.getPrincipal());
				String refreshToken = jwtService.generateRefreshToken((UserPrincipal) authentication.getPrincipal());
				return new AuthResponse(accessToken, refreshToken);
			} else {
				throw new UserException("Credentials Not correct",HttpStatus.UNAUTHORIZED);
			}
		}catch(BadCredentialsException e) {
			throw new UserException("Credentials Not correct",HttpStatus.UNAUTHORIZED);
		}
	}

	public AuthResponse varifyRefreshToken(String refreshToken) throws UserException {
		String username = jwtService.extractUserName(refreshToken);
		UserPrincipal userPrincipal = (UserPrincipal) customUserDetailService.loadUserByUsername(username);
		if (jwtService.validateToken(refreshToken, userPrincipal)) {
			String newAccessToken = jwtService.generateToken(userPrincipal);
			String newRefreshToken = jwtService.generateRefreshToken(userPrincipal);
			return new AuthResponse(newAccessToken, newRefreshToken);
		} else {
			throw new UserException("Refresh token not valid",HttpStatus.UNAUTHORIZED);
		}
	}

	public UserResponseDTO updateUser(Long id, UserRequestDTO userDTO) throws UserException {
		Optional<Users> existingUserOpt;
		Users existingUser = null;
		try {
			existingUserOpt = userRepo.findById(id);
		} catch (Exception e) {
			throw new UserException("Unable to fetch user", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (!existingUserOpt.isPresent()) {
			throw new UserException("User not found with id: " + id, HttpStatus.NOT_FOUND);
		}
		existingUser = existingUserOpt.get();
		updateUserValues(userDTO, existingUser);
		try {
			Users users = userRepo.save(existingUser);
			return new UserResponseDTO(users.getId(), users.getUsername(), users.getRoles());
		} catch (Exception e) {
			throw new UserException("Unable to save the user " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void updateUserValues(UserRequestDTO userDTO, Users existingUser) throws UserException {
		// TODO Auto-generated method stub
		if (userDTO == null) {
			throw new UserException("user details not provided",HttpStatus.BAD_REQUEST);
		}
		boolean isChanged = false;
		if (userDTO.getUsername() != null) {
			if (!userDTO.getUsername().equals(existingUser.getUsername())) {
				isChanged = true;
				existingUser.setUsername(userDTO.getUsername());
			}
		}
		if (userDTO.getRoles() != null) {
			if (!userDTO.getRoles().equals(existingUser.getRoles())) {
				isChanged = true;
				existingUser.setRoles(userDTO.getRoles());
			}
		}
		if (userDTO.getPassword() != null) {
			if (!userDTO.getPassword().equals(existingUser.getNonCryptPwd())) {
				isChanged = true;
				existingUser.setNonCryptPwd(userDTO.getPassword());
				existingUser.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
			}
		}

		if (!isChanged) {
			throw new UserException("Nothing to update",HttpStatus.BAD_REQUEST);
		}
	}

	public UserResponseDTO getUserDetails(Long id) throws UserException {
		// TODO Auto-generated method stub
		Optional<Users> existingUserOpt;
		Users existingUser;
		try {
			existingUserOpt = userRepo.findById(id);
		} catch (Exception e) {
			throw new UserException("Unable to fetch user", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(!existingUserOpt.isPresent()) {
			throw new UserException("User not found with id: " + id, HttpStatus.NOT_FOUND);
		}
		existingUser = existingUserOpt.get();
		UserResponseDTO userDTO = new UserResponseDTO(existingUser.getId(), existingUser.getUsername(), existingUser.getRoles());
		return userDTO;
	}
	
	public void deleteUser(Long id) throws UserException {
		if (!userRepo.existsById(id)) {
            throw new UserException("User not found with id: " + id, HttpStatus.NOT_FOUND);
        }
		try {
			userRepo.deleteById(id);
		}catch(Exception e){
			throw new UserException("Unable to delete User", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private Boolean validateUserFields(UserRequestDTO userDTO) throws UserException {
		if (userDTO == null) {
			throw new UserException("user details not provided",HttpStatus.BAD_REQUEST);
		}
		if(userDTO.getUsername() == null || userDTO.getUsername().trim().isBlank()) {
			return false;
		}
		if(userDTO.getRoles() == null || userDTO.getRoles().trim().isBlank()) {
			return false;
		}
		if(userDTO.getPassword() == null || userDTO.getPassword().trim().isBlank()) {
			return false;
		}
		return true;
	}

}
