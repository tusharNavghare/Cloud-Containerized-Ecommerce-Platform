package com.commerzo.common_config.events;

import java.util.List;

public class OrderCreatedEvent {
	private Long orderId;
	private List<OrderItemPayload> items;

	public OrderCreatedEvent() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OrderCreatedEvent(Long orderId, List<OrderItemPayload> items) {
		super();
		this.orderId = orderId;
		this.items = items;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public List<OrderItemPayload> getItems() {
		return items;
	}

	public void setItems(List<OrderItemPayload> items) {
		this.items = items;
	}

}
