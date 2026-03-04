package com.commerzo.order.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "order.exchange";
    public static final String QUEUE = "order.result.queue";
    public static final String ROUTING_KEY = "order.result";
    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

	@Bean
	public DirectExchange exchange() {
		log.info("Exchange is created");
		return new DirectExchange(EXCHANGE);
	}

	@Bean
	public Queue queue() {
		log.info("QUeueu is created");
		return new Queue(QUEUE);
	}

	@Bean
	public Binding binding() {
		log.info("Building in progress");
		return BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY);
	}

}
