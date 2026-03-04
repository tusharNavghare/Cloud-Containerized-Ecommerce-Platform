package com.commerzo.inventory.transferobject;

import java.io.Serializable;

public class InventoryDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public InventoryDTO(Long productId, Integer avlQuantity) {
		super();
		this.productId = productId;
		this.avlQuantity = avlQuantity;
	}
	
	public InventoryDTO(Long Id,Long productId, Integer avlQuantity) {
		super();
		this.Id = Id;
		this.productId = productId;
		this.avlQuantity = avlQuantity;
	}
	
	public InventoryDTO() {
		super();
		// TODO Auto-generated constructor stub
	}



	private Long Id;
	private Long productId;
	private Integer avlQuantity;
	
	public Long getProductId() {
		return productId;
	}
	
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	public Integer getAvlQuantity() {
		return avlQuantity;
	}
	
	public void setAvlQuantity(Integer avlQuantity) {
		this.avlQuantity = avlQuantity;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}
}
