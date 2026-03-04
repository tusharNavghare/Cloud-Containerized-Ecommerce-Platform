package com.commerzo.common_config.exception;

import org.springframework.http.HttpStatus;

public class UserException extends Exception{
	HttpStatus httpStatus;

	public UserException(String exceptionMsg, HttpStatus status) {
		super(exceptionMsg);
		this.httpStatus = status;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
}
