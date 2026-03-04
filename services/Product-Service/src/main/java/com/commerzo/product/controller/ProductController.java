package com.commerzo.product.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.commerzo.common_config.exception.ProductException;
import com.commerzo.product.service.ProductService;
import com.commerzo.product.transferobject.ProductDTO;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

	@Autowired
	ProductService productService;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping
	public ResponseEntity<ProductDTO> saveProduct(@RequestBody ProductDTO newProduct) throws ProductException {
		ProductDTO savedProduct = productService.saveProduct(newProduct);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
	}

	@GetMapping
	public ResponseEntity<List<ProductDTO>> getAllProducts() throws ProductException {
		List<ProductDTO> productDTOs = productService.getAllProducts();
		return ResponseEntity.status(HttpStatus.OK).body(productDTOs);

	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductDTO> getProductById(@PathVariable("id") Long id) throws ProductException {
		ProductDTO productDTO = productService.getProductById(id);
		return ResponseEntity.status(HttpStatus.OK).body(productDTO);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PatchMapping("/{id}")
	public ResponseEntity<?> updateProductById(@PathVariable("id") Long id, @RequestBody ProductDTO product)
			throws ProductException {
		ProductDTO productDTO = productService.updateProductById(id, product);
		return ResponseEntity.status(HttpStatus.OK).body(productDTO);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteProductById(@PathVariable("id") Long id) throws ProductException {
		productService.deleteByProductId(id);
		return ResponseEntity.status(HttpStatus.OK).body("Product Deleted");
	}

	@PostMapping("/batch")
	public ResponseEntity<List<ProductDTO>> getProductByIdList(@RequestBody List<Long> productIds)
			throws ProductException {
		List<ProductDTO> productDTOs = productService.getProductByIdIn(productIds);
		return ResponseEntity.status(HttpStatus.OK).body(productDTOs);
	}

}
