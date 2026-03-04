package com.commerzo.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.commerzo.auth.model.UserPrincipal;
import com.commerzo.auth.model.Users;
import com.commerzo.auth.repository.UserRepo;

@Service
public class CustomUserDetailService implements UserDetailsService{

	@Autowired
	UserRepo userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user= userRepo.findByUsername(username);
		if(user == null) {
			throw new UsernameNotFoundException("User Not found");
		}else {
			return new UserPrincipal(user);
		}
	}

}
