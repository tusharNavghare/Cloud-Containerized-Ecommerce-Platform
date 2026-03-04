package com.commerzo.common_config.events;

public class OrderItemPayload {
	private Long productId;
	private int quantity;

	public OrderItemPayload() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OrderItemPayload(Long productId, int quantity) {
		super();
		this.productId = productId;
		this.quantity = quantity;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
