package com.example.demo.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.example.demo.service.LocalFhirContext;
import com.example.demo.service.ProprietaryApiService;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PrimitiveType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController // Kennzeichnet diese Klasse als Spring REST Controller
@RequestMapping("") // Basis-URL für alle Endpunkte in dieser Klasse
public class FhirController {

    private static final Logger logger = Logger.getLogger(FhirController.class.getName());

    private final ProprietaryApiService proprietaryApiService;
    private final FhirContext fhirContext;

    @Autowired
    public FhirController(ProprietaryApiService proprietaryApiService, LocalFhirContext localFhirContext) {
        this.proprietaryApiService = proprietaryApiService;
        this.fhirContext = localFhirContext.getFhirContext();
    }

    @PostMapping("/Patient") // Mapped HTTP POST-Anfragen auf diesen Endpunkt
    public ResponseEntity<DomainResource> createPatient(@RequestBody String patientResource) {
        try {
            // Loggt die erhaltene Anfrage
            logger.info(() -> "Received request to create patient: " + patientResource);

            // Erzeugt einen JSON-Parser für FHIR
            IParser parser = fhirContext.newJsonParser();
            // Parsen des Patient-Ressource-Strings in ein Patient-Objekt
            Patient patient = parser.parseResource(Patient.class, patientResource);

            // Extrahieren des Vornamens aus der Patient-Ressource
            String firstName = patient.getName().get(0).getGiven().stream()
                    .map(PrimitiveType::getValue)
                    .collect(Collectors.joining(" "));
            // Extrahieren des Nachnamens aus der Patient-Ressource
            String lastName = patient.getName().get(0).getFamily();
            // Extrahieren des Geburtsdatums aus der Patient-Ressource
            String birthDate = patient.getBirthDateElement().getValueAsString();

            // Konvertierung des Geburtsdatums in das gewünschte Format
            birthDate = convertDate(birthDate);

            // Loggt die extrahierten und konvertierten Patientendaten
            String finalBirthDate = birthDate;
            logger.info(() -> "Parsed patient data: " + firstName + " " + lastName + ", Birthdate: " + finalBirthDate);

            // Sendet die Patientendaten an die proprietäre API
            boolean apiSuccess = proprietaryApiService.sendPatientData(firstName, lastName, birthDate);

            if (apiSuccess) {
                // Loggt und gibt eine Erfolgsantwort zurück, wenn die API-Anfrage erfolgreich war
                logger.info("Patient data sent successfully.");
                return ResponseEntity.status(HttpStatus.CREATED).body(patient);
            } else {
                // Loggt und gibt eine Fehlerantwort zurück, wenn die API-Anfrage fehlschlägt
                logger.severe("Failed to send patient data to proprietary API.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildOpperationOutcome("Error occurred while creating patient."));
            }
        } catch (RuntimeException e) {
            // Loggt und gibt eine Fehlerantwort zurück, wenn eine Ausnahme auftritt
            logger.log(Level.SEVERE, "Exception occurred while creating patient", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildOpperationOutcome("Exception occurred while creating patient."));
        }
    }

    @Nonnull
    private static OperationOutcome buildOpperationOutcome(String message) {
        OperationOutcome operationOutcome = new OperationOutcome();
        operationOutcome.addIssue().setSeverity(OperationOutcome.IssueSeverity.ERROR)
                .setCode(OperationOutcome.IssueType.EXCEPTION)
                .setDiagnostics(message);
        return operationOutcome;
    }

    private String convertDate(String birthDate) {
        // Konvertierung des Geburtsdatums von YYYY-MM-DD zu DD.MM.YYYY
        String[] parts = birthDate.split("-");
        return parts[2] + "." + parts[1] + "." + parts[0];
    }
}
