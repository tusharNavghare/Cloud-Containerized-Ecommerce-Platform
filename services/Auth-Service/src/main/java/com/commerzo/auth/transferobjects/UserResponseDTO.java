package com.commerzo.auth.transferobjects;

import java.math.BigDecimal;

public class UserResponseDTO {
	private String username;
	private String roles;
	private Long id;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	
	public UserResponseDTO(String username, String roles) {
		super();
		this.username = username;
		this.roles = roles;
	}
	
	public UserResponseDTO(Long id, String username, String roles) {
		super();
		this.id = id;
		this.username = username;
		this.roles = roles;
	}
	
	public UserResponseDTO() {
		
	}
	
	@Override
	public String toString() {
		return "UserDTO [ username=" + username + ", roles=" + roles + "]";
	}
	
}
