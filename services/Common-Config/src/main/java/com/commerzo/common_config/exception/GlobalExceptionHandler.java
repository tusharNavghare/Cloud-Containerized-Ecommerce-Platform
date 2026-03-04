package com.commerzo.common_config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UserException.class)
	public ResponseEntity<ErrorResponse> handleUserException(UserException ex) {
		System.out.println("In exceptionHandler of auth service " + ex.getMessage());
		return ResponseEntity.status(ex.getHttpStatus())
				.body(new ErrorResponse(ex.getMessage(), ex.getHttpStatus().value()));
	}

	@ExceptionHandler(ProductException.class)
	public ResponseEntity<ErrorResponse> handleProductException(ProductException ex) {
		return ResponseEntity.status(ex.getHttpStatus())
				.body(new ErrorResponse(ex.getMessage(), ex.getHttpStatus().value()));
	}

	@ExceptionHandler(InventoryException.class)
	public ResponseEntity<ErrorResponse> handleInventoryException(InventoryException ex) {
		return ResponseEntity.status(ex.getHttpStatus())
				.body(new ErrorResponse(ex.getMessage(), ex.getHttpStatus().value()));
	}

	@ExceptionHandler(OrderException.class)
	public ResponseEntity<ErrorResponse> handleOrderException(OrderException ex) {
		return ResponseEntity.status(ex.getHttpStatus())
				.body(new ErrorResponse(ex.getMessage(), ex.getHttpStatus().value()));
	}
}