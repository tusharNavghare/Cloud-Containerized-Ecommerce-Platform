package com.commerzo.auth.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.lang.Arrays;

public class UserPrincipal implements UserDetails{

	String username;
	String password;
	List<SimpleGrantedAuthority> roles;

	public UserPrincipal(Users user) {
		username = user.getUsername();
		password = user.getPassword();
		setAuthorities(user.getRoles());
	}
	
	private void setAuthorities(String roles2) {
		// TODO Auto-generated method stub
		if(roles2 != null && !roles2.isEmpty()) {
			String[] rolesArray = roles2.split(",");
			roles = new ArrayList<SimpleGrantedAuthority>();
			for(String role : rolesArray) {
				roles.add(new SimpleGrantedAuthority(role));
			}
		}
	}

	private List<SimpleGrantedAuthority> createAuthorities(String[] rolesArray) {
		// TODO Auto-generated method stub

		return null;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return roles;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return username;
	}
}
