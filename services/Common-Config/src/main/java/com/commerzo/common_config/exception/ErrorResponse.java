package com.commerzo.common_config.exception;

public class ErrorResponse {
	
	String message;
	int value;
	
	public ErrorResponse(String message, int value) {
		// TODO Auto-generated constructor stub
		this.message = message;
		this.value = value;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	

}
