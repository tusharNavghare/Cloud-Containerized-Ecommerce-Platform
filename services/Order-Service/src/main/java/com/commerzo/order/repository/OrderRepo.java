package com.commerzo.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.commerzo.order.model.Order;
import com.commerzo.order.model.OrderStatus;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long>{

	List<Order> getOrderByStatus(OrderStatus enumStatus);

	Order getOrderById(Long id);
	
}
