package com.commerzo.inventory.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.commerzo.inventory.config.RabbitMQConfig;
import com.commerzo.inventory.model.Inventory;
import com.commerzo.inventory.repository.InventoryRepo;
import com.commerzo.common_config.events.InventoryResultEvent;
import com.commerzo.common_config.events.OrderCreatedEvent;
import com.commerzo.common_config.events.OrderItemPayload;
import com.commerzo.common_config.exception.InventoryException;

import jakarta.transaction.Transactional;

@Component
public class OrderCreatedListener {

	@Autowired
	InventoryRepo inventoryRepository;
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

	@RabbitListener(queues = "inventory.queue")
	@Transactional
	public void handleOrderCreated(OrderCreatedEvent event){
		log.info("Coming from rabbitmq in handleOrderCreated");
		boolean success = false;
	    try {
			Set<Long> productIds = event.getItems().stream().map(i -> i.getProductId()).collect(Collectors.toSet());
			log.info("Product ids count handleOrderCreated" + productIds.size());	
			
			List<Inventory> inventories = inventoryRepository.findByProductIdIn(new ArrayList(productIds));
			
			if (inventories != null && !inventories.isEmpty()) {
				Set<Long> incomingProdIds = inventories.stream().map(i-> i.getProductId()).collect(Collectors.toSet());
				
				String missedProdIds = productIds.stream().filter(id-> !incomingProdIds.contains(id)).map(id -> String.valueOf(id))
						.collect(Collectors.joining(","));
				
				log.info("missedProdIds are " + missedProdIds);
				if (missedProdIds != null && !missedProdIds.trim().isEmpty()) {
					log.info("Inventories missing for productIds " + missedProdIds);
					throw new InventoryException("Inventories missing for productIds " + missedProdIds,
							HttpStatus.NOT_FOUND);
				}

			} else {
				log.info("Inventories came null");
				throw new InventoryException("Inventories for productIds not present", HttpStatus.NOT_FOUND);
			}
			
			for (OrderItemPayload item : event.getItems()) {
	            Inventory inv = inventoryRepository.findByProductIdWithLock(item.getProductId());

	            if (inv == null || inv.getAvlStock() < item.getQuantity()) {
	            	log.info("Insufficient stock or product missing");
	                throw new InventoryException("Insufficient stock or product missing", HttpStatus.CONFLICT);
	            }

	            inv.setAvlStock(inv.getAvlStock() - item.getQuantity());
	            inventoryRepository.save(inv);
	        }	        
	        success = true;
	    } catch (Exception e) {
	    		log.info(e.getMessage());
	        success = false;
	    }
	    log.info("Response going from listener in inventory service with status " + success);
	    rabbitTemplate.convertAndSend(
	    		RabbitMQConfig.EXCHANGE, 
	        "order.result", 
	        new InventoryResultEvent(event.getOrderId(), success)
	    );
	}
}

