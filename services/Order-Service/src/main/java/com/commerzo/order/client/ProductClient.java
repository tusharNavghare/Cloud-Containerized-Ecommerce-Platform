package com.commerzo.order.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service", url = "${product.service.url:}")
public interface ProductClient {
	@PostMapping("/api/v1/products/batch")
	public ResponseEntity<?> getProductByIdList(@RequestBody List<Long> productIds);
}
