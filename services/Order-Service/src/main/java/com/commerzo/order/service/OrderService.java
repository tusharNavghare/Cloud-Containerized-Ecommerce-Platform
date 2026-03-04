package com.commerzo.order.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.commerzo.order.client.ProductClient;
import com.commerzo.order.config.RabbitMQConfig;
import com.commerzo.order.model.Order;
import com.commerzo.order.model.OrderItem;
import com.commerzo.order.model.OrderStatus;
import com.commerzo.order.repository.OrderRepo;
import com.commerzo.order.transferobjects.OrderItemDTO;
import com.commerzo.order.transferobjects.OrderRequestDTO;
import com.commerzo.order.transferobjects.OrderResponseDTO;
import com.commerzo.common_config.events.OrderCreatedEvent;
import com.commerzo.common_config.events.OrderItemPayload;
import com.commerzo.common_config.exception.InventoryException;
import com.commerzo.common_config.exception.OrderException;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

	OrderRepo orderRepo;

	ProductClient productClient;

	RabbitTemplate rabbitTemplate;

	private static final Logger log = LoggerFactory.getLogger(OrderService.class);

	public OrderService(OrderRepo orderRepo, ProductClient productClient, RabbitTemplate rabbitTemplate) {
		this.orderRepo = orderRepo;
		this.productClient = productClient;
		this.rabbitTemplate = rabbitTemplate;
	}

	public OrderResponseDTO placeOrder(OrderRequestDTO requestDTO) throws OrderException {

		validateIncomingOrderRequest(requestDTO);

		Order order = new Order();
		order.setStatus(OrderStatus.CREATED);
		order.setLocaldate(LocalDateTime.now());

		setOrderProperties(order, requestDTO);

		Order savedOrder = null;
		try {
			savedOrder = orderRepo.save(order);
		} catch (Exception e) {
			e.printStackTrace();
			throw new OrderException("Unable to create order", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		OrderCreatedEvent event = new OrderCreatedEvent();
		event.setOrderId(order.getId());

		event.setItems(
				order.getItems().stream().map(i -> new OrderItemPayload(i.getProductId(), i.getQuantity())).toList());

		sendOrderCreatedEvent(event);

		return new OrderResponseDTO(savedOrder.getId(), getItemDTOs(savedOrder.getItems()), savedOrder.getLocaldate(),
				savedOrder.getStatus());
	}

	private void validateIncomingOrderRequest(OrderRequestDTO requestDTO) throws OrderException {
		// TODO Auto-generated method stub
		if (requestDTO == null || requestDTO.getItemList() == null || requestDTO.getItemList().isEmpty()) {
			throw new OrderException("Important fields are missing in order request", HttpStatus.BAD_REQUEST);
		}
		
		Set<Long> productIdsSet = requestDTO.getItemList().stream().map(i -> i.getProductId()).collect(Collectors.toSet());

		if(productIdsSet.size() < requestDTO.getItemList().size()) {
			throw new OrderException("ProductIds duplicated or not added correctly", HttpStatus.BAD_REQUEST);
		}
		
		List<Long> productIds = createInventoryRequestDTOs(requestDTO.getItemList());
		validateProductViaProductClient(productIds);
	}

	private void validateProductViaProductClient(List<Long> productIds) throws OrderException {
		try {
			ResponseEntity<?> response = productClient.getProductByIdList(productIds);
			if (response.getStatusCode() != HttpStatus.OK) {
				log.info("response body got is " + response.getBody());
				throw new OrderException("Product Validation Failed",
						HttpStatus.valueOf(response.getStatusCode().value()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new OrderException("Product Validation Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<Long> createInventoryRequestDTOs(List<OrderItemDTO> itemList) throws OrderException {
		List<Long> productIds = new ArrayList<Long>();
		for (OrderItemDTO itemDTO : itemList) {
			if (itemDTO.getProductId() != null && itemDTO.getQuantity() > 0) {
				productIds.add(itemDTO.getProductId());
			} else {
				throw new OrderException("Product id along with it's quantity is not mentioned properly",
						HttpStatus.BAD_REQUEST);
			}
		}
		return productIds;
	}

	private List<OrderItemDTO> getItemDTOs(List<OrderItem> items) throws OrderException {
		if (items == null || items.isEmpty()) {
			throw new OrderException("Product Validation Failed", HttpStatus.NOT_ACCEPTABLE);
		}
		List<OrderItemDTO> itemDTOs = new ArrayList<OrderItemDTO>();
		for (OrderItem item : items) {
			OrderItemDTO itemDTO = new OrderItemDTO();
			itemDTO.setProductId(item.getProductId());
			itemDTO.setQuantity(item.getQuantity());
			itemDTOs.add(itemDTO);
		}
		return itemDTOs;
	}

	private void setOrderProperties(Order order, OrderRequestDTO request) {
		// TODO Auto-generated method stub
		List<OrderItem> items = new ArrayList<OrderItem>();
		for (OrderItemDTO itemDTO : request.getItemList()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setProductId(itemDTO.getProductId());
			orderItem.setQuantity(itemDTO.getQuantity());
			items.add(orderItem);
		}
		order.setItems(items);
		order.setLocaldate(LocalDateTime.now());
	}

	public void sendOrderCreatedEvent(OrderCreatedEvent event) {
		rabbitTemplate.convertAndSend("order.exchange", "inventory.reserve", event);
	}

	public OrderResponseDTO getOrderByID(Long id) throws OrderException {
		// TODO Auto-generated method stub
		Order order = null;
		try {
			order = orderRepo.getOrderById(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new OrderException("Unable to fetch record", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (order == null) {
			throw new OrderException("Order not found for given Id ", HttpStatus.NOT_FOUND);
		}
		return new OrderResponseDTO(order.getId(), getItemDTOs(order.getItems()), order.getLocaldate(),
				order.getStatus());
	}

	public List<OrderResponseDTO> getOrderByStatus(String status) throws OrderException {
		// TODO Auto-generated method stub
		OrderStatus enumStatus = null;
		try {
			enumStatus = OrderStatus.valueOf(status);
		} catch (IllegalArgumentException e) {
			throw new OrderException("Invalid status shared ", HttpStatus.BAD_REQUEST);
		}

		List<Order> orders = null;
		try {
			orders = orderRepo.getOrderByStatus(enumStatus);
		} catch (Exception e) {
			e.printStackTrace();
			throw new OrderException("Unable to fetch records", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (orders == null || orders.isEmpty()) {
			throw new OrderException("Order not found for given status ", HttpStatus.NOT_FOUND);
		}
		List<OrderResponseDTO> responseList = new ArrayList<OrderResponseDTO>();
		for (Order order : orders) {
			responseList.add(new OrderResponseDTO(order.getId(), getItemDTOs(order.getItems()), order.getLocaldate(),
					order.getStatus()));
		}
		return responseList;
	}

}
