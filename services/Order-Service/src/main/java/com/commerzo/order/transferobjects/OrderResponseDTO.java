package com.commerzo.order.transferobjects;

import java.time.LocalDateTime;
import java.util.List;

import com.commerzo.order.model.OrderStatus;

public class OrderResponseDTO {
	private Long orderId;
	private List<OrderItemDTO> itemList;
	private LocalDateTime localdate;
	private OrderStatus status;
	
	public OrderResponseDTO(Long orderId, List<OrderItemDTO> itemList, LocalDateTime localdate, OrderStatus status) {
		super();
		this.orderId = orderId;
		this.itemList = itemList;
		this.localdate = localdate;
		this.status = status;
	}
	
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public List<OrderItemDTO> getItemList() {
		return itemList;
	}
	public void setItemList(List<OrderItemDTO> itemList) {
		this.itemList = itemList;
	}

	public LocalDateTime getLocaldate() {
		return localdate;
	}

	public void setLocaldate(LocalDateTime localdate) {
		this.localdate = localdate;
	}
	
	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}
}
