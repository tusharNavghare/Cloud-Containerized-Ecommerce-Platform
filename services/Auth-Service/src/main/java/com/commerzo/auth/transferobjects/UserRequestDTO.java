package com.commerzo.auth.transferobjects;

import java.math.BigDecimal;

public class UserRequestDTO {
	private String username;
	private String roles;
	private String password;

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
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public UserRequestDTO(String username, String roles, String password) {
		super();
		this.username = username;
		this.roles = roles;
		this.password = password;
	}
	
	public UserRequestDTO( String username, String roles) {
		super();
		this.username = username;
		this.roles = roles;
	}
	
	public UserRequestDTO() {
		
	}
	
	@Override
	public String toString() {
		return "UserDTO [ username=" + username + ", roles=" + roles + ", password=" + password+ "]";
	}
	
}
