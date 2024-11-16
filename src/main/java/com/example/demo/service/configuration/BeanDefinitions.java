package com.example.demo.service.configuration;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootConfiguration
public class BeanDefinitions {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
