package com.commerzo.auth.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	private String username;
	private String cryptPassword;
	private String roles;
	private String nonCryptPwd;
	
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getNonCryptPwd() {
		return nonCryptPwd;
	}
	public void setNonCryptPwd(String nonCryptPwd) {
		this.nonCryptPwd = nonCryptPwd;
	}
	
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return cryptPassword;
	}
	public void setPassword(String password) {
		this.cryptPassword = password;
	}
	
	public String getCryptPassword() {
		return cryptPassword;
	}
	public void setCryptPassword(String cryptPassword) {
		this.cryptPassword = cryptPassword;
	}

	public Users() {
		super();
	}
	public Users(String username, String password, String roles, String nonCryptPwd) {
		super();
		this.username = username;
		this.cryptPassword = password;
		this.roles = roles;
		this.nonCryptPwd = nonCryptPwd;
	}
	
	public Users(String username, String cryptPassword, String roles, String nonCryptPwd, BigDecimal amount) {
		super();
		this.username = username;
		this.cryptPassword = cryptPassword;
		this.roles = roles;
		this.nonCryptPwd = nonCryptPwd;
	}
	@Override
	public String toString() {
		return "Users [Id=" + Id + ", username=" + username + ", cryptPassword=" + cryptPassword + ", roles=" + roles
				+ ", nonCryptPwd=" + nonCryptPwd + "]";
	}
	
	
}
