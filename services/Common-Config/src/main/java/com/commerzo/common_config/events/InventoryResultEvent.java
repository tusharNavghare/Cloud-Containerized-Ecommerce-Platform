package com.commerzo.common_config.events;

public class InventoryResultEvent {
	private Long orderId;
	private Boolean success;

	public InventoryResultEvent(Long orderId, Boolean success) {
		super();
		this.orderId = orderId;
		this.success = success;
	}

	public InventoryResultEvent() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Boolean isSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

}
