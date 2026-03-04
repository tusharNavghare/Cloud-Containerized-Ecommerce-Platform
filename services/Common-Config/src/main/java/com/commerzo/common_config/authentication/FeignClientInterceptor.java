package com.commerzo.common_config.authentication;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class FeignClientInterceptor implements RequestInterceptor{

	@Override
	public void apply(RequestTemplate template) {
		// TODO Auto-generated method stub
		ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		
		if(attributes != null) {
			HttpServletRequest servletRequest = attributes.getRequest();
			String authHeader = servletRequest.getHeader("Authorization");
			if(authHeader != null && authHeader.startsWith("Bearer ")) {
				template.header("Authorization", authHeader);
			}
		}
	}
	
}
