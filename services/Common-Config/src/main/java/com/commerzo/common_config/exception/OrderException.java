package com.commerzo.common_config.exception;

import org.springframework.http.HttpStatus;

public class OrderException extends Exception {
	HttpStatus httpStatus;

	public OrderException(String exceptionMsg, HttpStatus status) {
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
