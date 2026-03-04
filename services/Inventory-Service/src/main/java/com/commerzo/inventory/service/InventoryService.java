package com.commerzo.inventory.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.commerzo.inventory.client.ProductFeignClient;
import com.commerzo.inventory.model.Inventory;
import com.commerzo.inventory.repository.InventoryRepo;
import com.commerzo.inventory.transferobject.InventoryDTO;
import com.commerzo.common_config.exception.InventoryException;

import jakarta.transaction.Transactional;

@Service
public class InventoryService {
	
	@Autowired
	InventoryRepo inventoryRepo;
	
	@Autowired
	ProductFeignClient productFeignClient;
	
	private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
	
	public InventoryDTO checkStock(Long productId) throws InventoryException {
		Inventory inventory = inventoryRepo.findByProductId(productId);
		if(inventory == null) {
			throw new InventoryException("No product is found by given productId", HttpStatus.NOT_FOUND);
		}
		return new InventoryDTO(inventory.getId(),inventory.getProductId(), inventory.getAvlStock());
	}

	public List<InventoryDTO> restoreStock(List<InventoryDTO> inventoryDTOs) throws InventoryException {
		List<Inventory> inventories = validateProductIdsAndUpdateObj(inventoryDTOs);
		List<Inventory> inventoryResult = null;
		try {
			inventoryResult = inventoryRepo.saveAll(inventories);
		} catch(Exception e) {
			throw new InventoryException("Unable to save records ", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(inventoryResult == null || inventoryResult.isEmpty()) {
			throw new InventoryException("Something went wrong while restocking existing inventories", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return convertModelToDTO(inventoryResult);
	}

	private List<Inventory> validateProductIdsAndUpdateObj(List<InventoryDTO> inventoryDTOs) throws InventoryException {
		// TODO Auto-generated method stub
		validateIncomingInventories(inventoryDTOs);
		Set<Long> incomingInventoryProductIds = inventoryDTOs.stream().map(i -> i.getProductId())
				.collect(Collectors.toSet());
		
		List<Inventory> returnedInventories = checkIfStockPresentForProducts(incomingInventoryProductIds);
		
		Map<Long, Inventory> inventoryMap = returnedInventories.stream()
				.collect(Collectors.toMap(i -> i.getProductId(), i -> i));
		List<Long> missingIds = incomingInventoryProductIds.stream().filter(id -> !inventoryMap.containsKey(id))
				.collect(Collectors.toList());
		if (missingIds != null && !missingIds.isEmpty()) {
			throw new InventoryException("Existing Stocks for given product Id not present " + missingIds, HttpStatus.NOT_FOUND);
		}
		inventoryDTOs.stream().forEach(dto -> {
			Inventory inventory = inventoryMap.get(dto.getProductId());
			if (inventory != null) {
				inventory.setAvlStock(inventory.getAvlStock() + dto.getAvlQuantity());
			}
		});
		return new ArrayList(inventoryMap.values());
	}

	private List<Inventory> checkIfStockPresentForProducts(Set<Long> incomingInventoryProductIds) {
		// TODO Auto-generated method stub
		List<Inventory> returnedInventories = inventoryRepo
				.findByProductIdIn(new ArrayList(incomingInventoryProductIds));
		return returnedInventories;
		
	}

	public List<InventoryDTO> saveNewStocks(List<InventoryDTO> requestList) throws InventoryException {
		validateIncomingInventoriesForSaving(requestList);
		List<Inventory> inventoryList = new ArrayList<Inventory>();
		for (InventoryDTO requestDTO : requestList) {
			inventoryList.add(new Inventory(requestDTO.getProductId(), requestDTO.getAvlQuantity()));
		}
		List<Inventory> savedList = null;
		try {
			savedList = inventoryRepo.saveAll(inventoryList);
		} catch (Exception e) {
			throw new InventoryException("Unable to save stock ", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return convertModelToDTO(savedList);
	}

	private void validateIncomingInventoriesForSaving(List<InventoryDTO> requestList) throws InventoryException {
		// TODO Auto-generated method stub
		validateIncomingInventories(requestList);
		checkStockPresenceForProduct(requestList);
		
	}

	private void checkStockPresenceForProduct(List<InventoryDTO> requestList) throws InventoryException {
		// TODO Auto-generated method stub
		Set<Long> incomingInventoryProductIds = requestList.stream().map(i -> i.getProductId())
				.collect(Collectors.toSet());
		List<Inventory> returnedInventories = checkIfStockPresentForProducts(incomingInventoryProductIds);
		if (returnedInventories != null && !returnedInventories.isEmpty()) {
			String productIds = returnedInventories.stream().map(inventory -> String.valueOf(inventory.getProductId()))
					.collect(Collectors.joining(","));
			throw new InventoryException("Stock already present for product ids " + productIds,
					HttpStatus.NOT_ACCEPTABLE);
		}

	}

	private List<InventoryDTO> convertModelToDTO(List<Inventory> savedList) {
		// TODO Auto-generated method stub
		List<InventoryDTO> inventoryDTOs;
		if(savedList != null && !savedList.isEmpty()) {
			inventoryDTOs = new ArrayList<InventoryDTO>();
			for(Inventory inventory : savedList) {
				inventoryDTOs.add(new InventoryDTO(inventory.getId(),inventory.getProductId(), inventory.getAvlStock()));
			}
			return inventoryDTOs;
		}else {
			return Collections.EMPTY_LIST;
		}
	}

	private void validateIncomingInventories(List<InventoryDTO> requestList) throws InventoryException {
		// TODO Auto-generated method stub
		if (requestList == null || requestList.isEmpty()) {
			throw new InventoryException("List is not provided", HttpStatus.BAD_REQUEST);
		}
		for (InventoryDTO inventoryDTO : requestList) {
			if (inventoryDTO.getProductId() == null || inventoryDTO.getAvlQuantity() == null) {
				throw new InventoryException("Important inventory field missing", HttpStatus.BAD_REQUEST);
			}
		}
		List<Long> productIds = requestList.stream().map(i -> i.getProductId()).collect(Collectors.toList());
		try {
			ResponseEntity<?> response = productFeignClient.getProductByIdList(productIds);
			if (!(response.getStatusCode() == HttpStatus.OK)) {
				log.info("request body got is " + response.getBody());
				throw new InventoryException("Product Validation Failed", HttpStatus.valueOf(response.getStatusCode().value()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InventoryException("Product Validation Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		Set<Long> incomingInventoryProductIds = requestList.stream().map(i -> i.getProductId())
				.collect(Collectors.toSet());
		if (requestList.size() != incomingInventoryProductIds.size()) {
			throw new InventoryException("ProductIds duplicated or not provided correctly", HttpStatus.BAD_REQUEST);
		}
	}
	
	@Transactional
	public void deleteStock(Long id) throws InventoryException{
		Integer deleted = -1;
		try {
			deleted = inventoryRepo.deleteById(id);
		}catch(Exception e) {
			e.printStackTrace();
			throw new InventoryException("Unable to delete record", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(deleted == 0) {
			throw new InventoryException("Record with given Id not found ", HttpStatus.NOT_FOUND);
		}
	}
}
