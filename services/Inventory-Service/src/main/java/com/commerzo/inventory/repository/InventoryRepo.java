package com.commerzo.inventory.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.commerzo.inventory.model.Inventory;

import jakarta.persistence.LockModeType;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Integer>{

	Inventory findByProductId(Long productId);

	List<Inventory> findByProductIdIn(List<Long> productIds);
	
	@Query("SELECT i.id FROM Inventory i WHERE i.id IN :inventoryIds")
	Set<Long> findIdByIncomingIds(@Param("inventoryIds") List<Long> inventoryIds);

	Integer deleteById(Long id);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT i FROM Inventory i WHERE i.productId = :productId")
	Inventory findByProductIdWithLock(Long productId);
	
}
