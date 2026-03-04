package com.commerzo.order.listeners;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.commerzo.order.model.OrderStatus;
import com.commerzo.order.repository.OrderRepo;
import com.commerzo.common_config.events.InventoryResultEvent;

@Component
public class InventoryResultListener {

	@Autowired
	OrderRepo orderRepository;
	
	private static final Logger log = LoggerFactory.getLogger(InventoryResultListener.class);

	@RabbitListener(queues = "order.result.queue")
    public void handleInventoryResult(InventoryResultEvent event) {
		log.info("rabbitmq event received in order-service");
        orderRepository.findById(event.getOrderId())
            .ifPresent(order -> {
                if (event.isSuccess()) {
                    order.setStatus(OrderStatus.CONFIRMED);
                } else {
                    order.setStatus(OrderStatus.FAILED);
                }
                orderRepository.save(order);
            });
    }
}

