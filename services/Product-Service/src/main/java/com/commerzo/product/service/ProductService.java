package com.commerzo.product.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.commerzo.product.model.Product;
import com.commerzo.product.repository.ProductRepository;
import com.commerzo.product.transferobject.ProductDTO;
import com.commerzo.common_config.exception.ProductException;

@Service
public class ProductService {
	
	@Autowired
	ProductRepository productRepository;
	
	@Caching(put = { @CachePut(value = "product", key = "#result.id") }, evict = {
			@CacheEvict(value = "allProduct", key = "'allProduct'") })
	public ProductDTO saveProduct(ProductDTO newProduct) throws ProductException {
		if(!validateProduct(newProduct)) {
			throw new ProductException("Important properties of product missing",HttpStatus.BAD_REQUEST);
		}
		if(isProductPresent(newProduct.getName())) {
			throw new ProductException("Product by same name already exists",HttpStatus.CONFLICT);
		}
		Product product = createProduct(newProduct);
		try {
			Product savedProduct = productRepository.save(product);
			newProduct.setId(savedProduct.getId());
			return newProduct;
		}catch(Exception e) {
			e.printStackTrace();
			throw new ProductException("Not able to save product",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private Product createProduct(ProductDTO newProduct) {
		// TODO Auto-generated method stub
		Product product = new Product();
		product.setName(newProduct.getName());
		product.setDescription(newProduct.getDescription());
		product.setCategory(newProduct.getCategory());
		product.setPrice(newProduct.getPrice());
		product.setActive(newProduct.getActive());
		return product;
	}

	public boolean isProductPresent(String productName) {
		if(productRepository.findByName(productName) != null) {
			return true;
		}else {
			return false;
		}
	}

	@Cacheable(value = "allProduct", key = "'allProduct'")
	public List<ProductDTO> getAllProducts() throws ProductException {
		List<Product> products = productRepository.findAll();
		if(products == null || products.isEmpty()) {
			throw new ProductException("No products present",HttpStatus.NOT_FOUND);
		}
		List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();
		for (Product product : products) {
			productDTOs.add(new ProductDTO(product.getId(), product.getName(), product.getDescription(),
					product.getPrice(), product.getCategory(), product.getActive()));
		}
		return productDTOs;
	}

	@Cacheable(value = "product", key = "#id")
	public ProductDTO getProductById(Long id) throws ProductException {
		Optional<Product> productOpt = productRepository.findById(id);
		if (!productOpt.isPresent()) {
			throw new ProductException("Product not present with given Id " + id, HttpStatus.NOT_FOUND);
		}
		Product product = productOpt.get();
		return new ProductDTO(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
				product.getCategory(), product.getActive());
	}

	@Caching(evict = { @CacheEvict(value = "allProduct", key = "'allProduct'"),
			@CacheEvict(value = "product", key = "#id") })
	public ProductDTO updateProductById(Long id, ProductDTO productDTO) throws ProductException {
		// TODO Auto-generated method stub
		if(!validateProduct(productDTO)) {
			throw new ProductException("Important properties of product missing",HttpStatus.BAD_REQUEST);
		}
		Optional<Product> existingProductOpt;
		Product existingProduct = null;
		try {
			existingProductOpt = productRepository.findById(id);
		} catch (Exception e) {
			throw new ProductException("Unable to fetch Product", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (!existingProductOpt.isPresent()) {
			throw new ProductException("Product not found with id: " + id,HttpStatus.NOT_FOUND);
		}
		existingProduct = existingProductOpt.get();
		updateProductValues(productDTO, existingProduct);
		try {
			Product product = productRepository.save(existingProduct);
			return new ProductDTO(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
					product.getCategory(), product.getActive());
		} catch (Exception e) {
			throw new ProductException("Unable to save the user " + e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		

	}

	private void updateProductValues(ProductDTO productDTO, Product existingProduct) throws ProductException {
		if (productDTO == null) {
			throw new ProductException("Product details not provided", HttpStatus.BAD_REQUEST);
		}
		boolean isChanged = false;
		if (productDTO.getName() != null) {
			if (!productDTO.getName().equals(existingProduct.getName())) {
				isChanged = true;
				existingProduct.setName(productDTO.getName());
			}
		}
		
		if (productDTO.getDescription() != null) {
			if (!productDTO.getDescription().equals(existingProduct.getDescription())) {
				isChanged = true;
				existingProduct.setDescription(productDTO.getDescription());
			}
		}
		
		if (productDTO.getCategory() != null) {
			if (!productDTO.getCategory().equals(existingProduct.getCategory())) {
				isChanged = true;
				existingProduct.setCategory(productDTO.getCategory());
			}
		}
		
		if (productDTO.getPrice() != null) {
			if (!productDTO.getPrice().equals(existingProduct.getPrice())) {
				isChanged = true;
				existingProduct.setPrice(productDTO.getPrice());
			}
		}
		
		if (productDTO.getActive() != null) {
			if (productDTO.getActive() != existingProduct.getActive()) {
				isChanged = true;
				existingProduct.setActive(productDTO.getActive());
			}
		}

		if (!isChanged) {
			throw new ProductException("Nothing to update", HttpStatus.OK);
		}

	}

	public boolean isUpdatedProductPresent(String productName, Long id) {
		// TODO Auto-generated method stub
		Product product = productRepository.findByName(productName);
		if(product != null && id != product.getId()) {
			return false;
		}else {
			return true;
		}
	}

	@Caching(evict = { @CacheEvict(value = "allProduct", key = "'allProduct'"),
			@CacheEvict(value = "product", key = "#id") })
	public void deleteByProductId(Long id) throws ProductException {
		if (!productRepository.existsById(id)) {
            throw new ProductException("Product not found with id: " + id,HttpStatus.NOT_FOUND);
        }
		try {
			productRepository.deleteById(id);
		}catch(Exception e){
			throw new ProductException("Unable to delete Product",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<ProductDTO> getProductByIdIn(List<Long> productIds) throws ProductException {
		// TODO Auto-generated method stub
		List<Product> products = null;
		try{
			products = productRepository.getProductByIdIn(productIds);
		}catch(Exception e) {
			throw new ProductException("Unable to fetch list of products",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(products == null || products.isEmpty()) {
			throw new ProductException("No products present",HttpStatus.NOT_FOUND);
		}
		
		for(int i = 0; i < products.size(); i++) {
			if(products.get(i).getActive() == false) {
				throw new ProductException("Product " + products.get(i).getName() + " is inactive",HttpStatus.NOT_ACCEPTABLE);
			}
		}
		
		List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();
		
		Map<Long,Product> productMap = products.stream().collect(Collectors.toMap(product -> product.getId(), product -> product));
		for(Long productId : productIds) {
			if(!productMap.containsKey(productId)) {
				throw new ProductException("no product present for productId " + productId,HttpStatus.NOT_FOUND);
			}
		}
		for (Product product : products) {
			productDTOs.add(new ProductDTO(product.getId(), product.getName(), product.getDescription(),
					product.getPrice(), product.getCategory(), product.getActive()));
		}
		return productDTOs;
	}

	public boolean validateProduct(ProductDTO newProduct) {
		// TODO Auto-generated method stub
		if(newProduct == null) {
			return false;
		}
		if (newProduct.getName() == null || newProduct.getDescription() == null || newProduct.getPrice() == null
				|| newProduct.getCategory() == null || newProduct.getActive() == null) {
			return false;
		}
		return true;
	}
	
}
