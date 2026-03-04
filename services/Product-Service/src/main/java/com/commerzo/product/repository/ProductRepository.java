package com.commerzo.product.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.commerzo.product.model.Product;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	Product findByName(String name);
	
	@Transactional
	void deleteById(Long id);
	
	@Modifying
	@Transactional
	@Query("update Product p set  p.name = :name, p.description = :description, p.price = :price, p.category = :category, p.active = :active where p.id = :id")
	int updateProductById(@Param("name") String name,
			@Param("description") String description,
			@Param("price") BigDecimal price,
			@Param("category") String category,
			@Param("active") boolean active,
			@Param("id") Long id);

	
	List<Product> getProductByIdIn(List<Long> productIds);
	
	Optional<Product> findById(Long id);

	boolean existsById(Long id);
}
