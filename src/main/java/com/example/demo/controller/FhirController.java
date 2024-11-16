package com.example.demo.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.example.demo.entities.Person;
import com.example.demo.service.ProprietaryApiService;
import com.example.demo.service.fhir.LocalFhirContext;
import com.example.demo.service.mapper.PersonMapper;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController // Kennzeichnet diese Klasse als Spring REST Controller
@RequestMapping("") // Basis-URL für alle Endpunkte in dieser Klasse
public class FhirController {

    private static final Logger logger = Logger.getLogger(FhirController.class.getName());

    private final ProprietaryApiService proprietaryApiService;
    private final IParser parser;

    @Autowired
    public FhirController(ProprietaryApiService proprietaryApiService, LocalFhirContext localFhirContext) {
        this.proprietaryApiService = proprietaryApiService;
        FhirContext fhirContext = localFhirContext.getFhirContext();
        // Erzeugt einen JSON-Parser für FHIR
        parser = fhirContext.newJsonParser();
    }

    //FIXME RESPONSE Currently parsed to String, there is a parsing error during serialization my be fixed in newer HAPI FHIR version
    @PostMapping(value = "/Patient", produces = "application/json") // Mapped HTTP POST-Anfragen auf diesen Endpunkt
    public ResponseEntity<String> createPatient(@RequestBody String patientResource) {

        try {
            // Loggt die erhaltene Anfrage
            logger.info(() -> "Received request to create patient: " + patientResource);

            // Parsen des Patient-Ressource-Strings in ein Patient-Objekt
            Patient patient = parser.parseResource(Patient.class, patientResource);
            //Mappe den Patienten auf eine Person
            Person person = PersonMapper.mapPatientToPerson(patient);
            // Loggt die extrahierten und konvertierten Patientendaten
            logger.info(() -> "Parsed patient data: " + person);

            // Sendet die Patientendaten an die proprietäre API
            if (proprietaryApiService.sendPatientData(person)) {
                // Loggt und gibt eine Erfolgsantwort zurück, wenn die API-Anfrage erfolgreich war
                logger.info("Patient data sent successfully.");
                return ResponseEntity.status(HttpStatus.CREATED).body(parser.encodeResourceToString(patient));
            } else {
                // Loggt und gibt eine Fehlerantwort zurück, wenn die API-Anfrage fehlschlägt
                logger.severe("Failed to send patient data to proprietary API.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(parser.encodeResourceToString(buildOperationOutcome("Error occurred while creating patient.")));
            }
        } catch (RuntimeException e) {
            // Loggt und gibt eine Fehlerantwort zurück, wenn eine Ausnahme auftritt
            logger.log(Level.SEVERE, "Exception occurred while creating patient", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(parser.encodeResourceToString(buildOperationOutcome("Exception occurred while creating patient.")));
        }
    }

    private static OperationOutcome buildOperationOutcome(String message) {
        OperationOutcome operationOutcome = new OperationOutcome();
        operationOutcome.addIssue().setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.EXCEPTION)
                .setDiagnostics(message);
        return operationOutcome;
    }

}
