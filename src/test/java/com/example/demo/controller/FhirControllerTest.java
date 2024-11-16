package com.example.demo.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.example.demo.service.ProprietaryApiService;
import org.assertj.core.api.Assertions;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class FhirControllerTest {
    @Autowired
    FhirController fhirController;
    @Autowired
    FhirContext fhirContext;

    @MockBean
    ProprietaryApiService proprietaryApiService;
    IParser iParser;

    @BeforeEach
    void setUp() {
        iParser = fhirContext.newJsonParser();
    }

    @Test
    void createPatient_EmptyString() {
        ResponseEntity<String> responseEntity = fhirController.createPatient("");
        OperationOutcome operationOutcome = iParser.parseResource(OperationOutcome.class, responseEntity.getBody());
        Assertions.assertThat(operationOutcome).isNotNull();
        Assertions.assertThat(operationOutcome).extracting(OperationOutcome::getIssue).extracting(i -> i.get(0).getDiagnostics()).isEqualTo("Exception occurred while creating patient.");
    }

    @Test
    void createPatient_Null() {
        ResponseEntity<String> responseEntity = fhirController.createPatient(null);
        OperationOutcome operationOutcome = iParser.parseResource(OperationOutcome.class, responseEntity.getBody());
        Assertions.assertThat(operationOutcome).isNotNull();
        Assertions.assertThat(operationOutcome).extracting(OperationOutcome::getIssue).extracting(i -> i.get(0).getDiagnostics()).isEqualTo("Exception occurred while creating patient.");
    }

    @Test
    void createPatient_ValidPatient() {
        Patient patient = new Patient();
        patient.addName().setFamily("Doe").addGiven("John");
        when(proprietaryApiService.sendPatientData(Mockito.any())).thenReturn(true);
        ResponseEntity<String> responseEntity = fhirController.createPatient(iParser.encodeResourceToString(patient));
        Patient patientResponse = iParser.parseResource(Patient.class, responseEntity.getBody());
        Assertions.assertThat(patientResponse).isNotNull();
        assertTrue(patient.equalsDeep(patientResponse));
    }

    @Test
    void createPatient_InvalidPatient() {
        Patient patient = new Patient();
        patient.addName().setFamily("Doe").addGiven("John");
        when(proprietaryApiService.sendPatientData(Mockito.any())).thenReturn(false);

        ResponseEntity<String> responseEntity = fhirController.createPatient(iParser.encodeResourceToString(patient));
        OperationOutcome operationOutcome = iParser.parseResource(OperationOutcome.class, responseEntity.getBody());
        Assertions.assertThat(operationOutcome).isNotNull();
        Assertions.assertThat(operationOutcome).extracting(OperationOutcome::getIssue).extracting(i -> i.get(0).getDiagnostics()).isEqualTo("Failed to send patient data to proprietary API.");
    }

    @Test
    void createDocument_EmptyString() {
        ResponseEntity<String> responseEntity = fhirController.createDocument("");
        OperationOutcome operationOutcome = iParser.parseResource(OperationOutcome.class, responseEntity.getBody());
        Assertions.assertThat(operationOutcome).isNotNull();
        Assertions.assertThat(operationOutcome).extracting(OperationOutcome::getIssue).extracting(i -> i.get(0).getDiagnostics())
                .isEqualTo("Exception occurred while creating document. Failed to parse JSON encoded FHIR content: Did not find any content to parse");

    }

    @Test
    void createDocument_Null() {
        ResponseEntity<String> responseEntity = fhirController.createDocument(null);
        OperationOutcome operationOutcome = iParser.parseResource(OperationOutcome.class, responseEntity.getBody());
        Assertions.assertThat(operationOutcome).isNotNull();
        Assertions.assertThat(operationOutcome).extracting(OperationOutcome::getIssue).extracting(i -> i.get(0).getDiagnostics()).isEqualTo("Exception occurred while creating document. null");
    }

    @Test
    void createDocument_ValidDocument() {
        DocumentReference documentReference = new DocumentReference();
        documentReference.setType(new CodeableConcept().addCoding(new Coding().setSystem("http://dvmd.de/fhir/CodeSystem/kdl").setCode("1234")))
                .setSubject(new Reference("123").setIdentifier(new Identifier().setValue("123")))
                .setContext(new DocumentReference.DocumentReferenceContextComponent().setEncounter(List.of(new Reference("123").setIdentifier(new Identifier().setValue("123")))))
                .setContent(List.of(new DocumentReference.DocumentReferenceContentComponent().setAttachment(new Attachment().setCreation(new Date()).setData("data".getBytes()))));
        when(proprietaryApiService.sendDocument(Mockito.any())).thenReturn(true);
        ResponseEntity<String> responseEntity = fhirController.createDocument(iParser.encodeResourceToString(documentReference));
        DocumentReference documentReferenceResponse = iParser.parseResource(DocumentReference.class, responseEntity.getBody());
        Assertions.assertThat(documentReferenceResponse).isNotNull();
    }

    @Test
    void createDocument_InvalidDocument() {
        DocumentReference documentReference = new DocumentReference();
        when(proprietaryApiService.sendDocument(Mockito.any())).thenReturn(false);
        ResponseEntity<String> responseEntity = fhirController.createDocument(iParser.encodeResourceToString(documentReference));
        OperationOutcome operationOutcome = iParser.parseResource(OperationOutcome.class, responseEntity.getBody());
        Assertions.assertThat(operationOutcome).isNotNull();
        Assertions.assertThat(operationOutcome).extracting(OperationOutcome::getIssue).extracting(i -> i.get(0).getDiagnostics())
                .isEqualTo("Exception occurred while creating document. DocumentReference or content is missing.");
    }

}
