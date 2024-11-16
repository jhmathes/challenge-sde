package com.example.demo.service;

import com.example.demo.model.Document;
import com.example.demo.model.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application.properties")
class ProprietaryApiServiceTest {
    @InjectMocks
    ProprietaryApiService proprietaryApiService;

    @Mock
    private RestTemplate restTemplate;

    @Test
    void sendPatientData() {
        Mockito.when(restTemplate.postForEntity("http://localhost:3001/Person",
                "{\"firstName\":\"Max\",\"lastName\":\"Mustermann\",\"birthDate\":\"01.01.1970\"}",
                String.class)
        ).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));
        //set field basePath in ProprietaryApiService, which is not loaded from application.properties
        ReflectionTestUtils.setField(proprietaryApiService, "basePath", "http://localhost:3001");
        boolean b = proprietaryApiService.sendPatientData(new Person("Max", "Mustermann", "01.01.1970"));
        Assertions.assertTrue(b);
    }

    @Test
    void sendPatientDataError() {
        Mockito.when(restTemplate.postForEntity("http://localhost:3001/Person",
                "{\"firstName\":\"Max\",\"lastName\":\"Mustermann\",\"birthDate\":\"01.01.1970\"}",
                String.class)
        ).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        //set field basePath in ProprietaryApiService, which is not loaded from application.properties
        ReflectionTestUtils.setField(proprietaryApiService, "basePath", "http://localhost:3001");
        boolean b = proprietaryApiService.sendPatientData(new Person("Max", "Mustermann", "01.01.1970"));
        Assertions.assertFalse(b);
    }

    @Test
    void sendDocument() {
        Mockito.when(restTemplate.postForEntity("http://localhost:3001/Document",
                "{\"kdlCode\":\"codeCodeCode\",\"patientId\":\"1\",\"visitNumber\":\"2\"\"dateCreated\":\"01.01.1970\"\"contentB64\":\"[99, 111, 110, 116, 101, 110, 116, 66, 54, 52]\"}",
                String.class)
        ).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));
        //set field basePath in ProprietaryApiService, which is not loaded from application.properties
        ReflectionTestUtils.setField(proprietaryApiService, "basePath", "http://localhost:3001");
        boolean b = proprietaryApiService.sendDocument(buildDefaultDocument());
        Assertions.assertTrue(b);
    }

    @Test
    void sendDocumentError() {
        Mockito.when(restTemplate.postForEntity("http://localhost:3001/Document",
                "{\"kdlCode\":\"codeCodeCode\",\"patientId\":\"1\",\"visitNumber\":\"2\"\"dateCreated\":\"01.01.1970\"\"contentB64\":\"[99, 111, 110, 116, 101, 110, 116, 66, 54, 52]\"}",
                String.class)
        ).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        //set field basePath in ProprietaryApiService, which is not loaded from application.properties
        ReflectionTestUtils.setField(proprietaryApiService, "basePath", "http://localhost:3001");
        boolean b = proprietaryApiService.sendDocument(buildDefaultDocument());
        Assertions.assertFalse(b);
    }

    private static Document buildDefaultDocument() {
        Document.DocumentBuilder documentBuilder = new Document.DocumentBuilder();
        documentBuilder.setKdlCode("codeCodeCode");
        documentBuilder.setPatientId(1);
        documentBuilder.setVisitNumber(2);
        documentBuilder.setDateCreated("01.01.1970");
        documentBuilder.setContentB64("contentB64".getBytes(StandardCharsets.UTF_8));
        return documentBuilder.build();
    }
}
