package com.commerzo.order.transferobjects;

import java.util.List;

public class OrderRequestDTO {
	List<OrderItemDTO> itemList;

	public List<OrderItemDTO> getItemList() {
		return itemList;
	}

	public void setItemList(List<OrderItemDTO> itemList) {
		this.itemList = itemList;
	}
}
