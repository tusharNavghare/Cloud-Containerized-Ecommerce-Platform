package com.commerzo.auth.transferobjects;

public class RefreshTokenRequest {
	String refreshToken;

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public RefreshTokenRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RefreshTokenRequest(String refreshToken) {
		super();
		this.refreshToken = refreshToken;
	}
	
	
}
