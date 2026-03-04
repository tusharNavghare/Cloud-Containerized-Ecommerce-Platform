package com.commerzo.common_config.exception;

import org.springframework.http.HttpStatus;

public class InventoryException extends Exception{
	HttpStatus httpStatus;

	public InventoryException(String exceptionMsg, HttpStatus status) {
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
