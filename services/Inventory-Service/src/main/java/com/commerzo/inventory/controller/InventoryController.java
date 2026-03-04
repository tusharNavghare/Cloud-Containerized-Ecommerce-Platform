package com.commerzo.inventory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.commerzo.inventory.model.Inventory;
import com.commerzo.inventory.service.InventoryService;
import com.commerzo.inventory.transferobject.InventoryDTO;
import com.commerzo.common_config.exception.InventoryException;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
	
	@Autowired
	InventoryService inventoryService;
	
	@GetMapping("/stocks/{productId}")
	public ResponseEntity<?> checkCurrentStock(@PathVariable("productId") Long productId) throws InventoryException {
		InventoryDTO responseDTO = inventoryService.checkStock(productId);
		return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PatchMapping("/stocks/restore")
	public ResponseEntity<List<InventoryDTO>> restoreStock(@RequestBody List<InventoryDTO> requestList) throws InventoryException {
		List<InventoryDTO> savedList = inventoryService.restoreStock(requestList);
		return ResponseEntity.status(HttpStatus.OK).body(savedList);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/stocks")
	public ResponseEntity<List<InventoryDTO>> saveNewStocks(@RequestBody List<InventoryDTO> requestList)
			throws InventoryException {
		List<InventoryDTO> savedList = inventoryService.saveNewStocks(requestList);
		return ResponseEntity.status(HttpStatus.OK).body(savedList);
	}
	
	@DeleteMapping("/stocks/{id}")
	public ResponseEntity<String> deleteStocks(@PathVariable Long id)
			throws InventoryException {
		inventoryService.deleteStock(id);
		return ResponseEntity.status(HttpStatus.OK).body("Record Deleted");
	}
	
	
	
}
