package com.example.demo.configuration;

import ca.uhn.fhir.context.FhirContext;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootConfiguration
public class BeanDefinitions {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public FhirContext getFhirContext() {
        return FhirContext.forR4();
    }
}
