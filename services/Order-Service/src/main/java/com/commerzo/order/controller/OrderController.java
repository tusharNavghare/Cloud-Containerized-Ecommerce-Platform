package com.commerzo.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.commerzo.order.service.OrderService;
import com.commerzo.order.transferobjects.OrderRequestDTO;
import com.commerzo.order.transferobjects.OrderResponseDTO;
import com.commerzo.common_config.exception.OrderException;

@Controller
@RequestMapping("/api/v1/orders")
public class OrderController {

	@Autowired
	OrderService orderService;

	@PostMapping
	ResponseEntity<OrderResponseDTO> placeOrder(@RequestBody OrderRequestDTO requestDTO) throws OrderException {
		OrderResponseDTO response = orderService.placeOrder(requestDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@GetMapping("/{id}")
	ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long id) throws OrderException{
		OrderResponseDTO response = orderService.getOrderByID(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping("/status/{status}")
	ResponseEntity<List<OrderResponseDTO>> getOrder(@PathVariable String status) throws OrderException{
		List<OrderResponseDTO> response = orderService.getOrderByStatus(status);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	
}
