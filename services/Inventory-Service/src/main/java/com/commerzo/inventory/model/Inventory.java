package com.commerzo.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Inventory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	@Column(nullable = false, unique = true)
	private Long productId;
	private Integer avlStock;
	
	public Inventory() {
		super();
	}

	public Inventory(Long productId, Integer avlStock) {
		super();
		this.productId = productId;
		this.avlStock = avlStock;
	}
	
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Integer getAvlStock() {
		return avlStock;
	}
	public void setAvlStock(Integer avlStock) {
		this.avlStock = avlStock;
	}
}
