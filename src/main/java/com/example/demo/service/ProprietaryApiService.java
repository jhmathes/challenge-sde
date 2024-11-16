package com.example.demo.service;

import com.example.demo.model.Document;
import com.example.demo.model.Person;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service // Kennzeichnet diese Klasse als Spring Service-Komponente
public class ProprietaryApiService {

    private static final Logger logger = Logger.getLogger(ProprietaryApiService.class.getName());
    private final RestTemplate restTemplate;

    private final String basePath;

    public ProprietaryApiService(RestTemplate restTemplate, @Value("${api.server.basePath}") String basePath) {
        this.restTemplate = restTemplate;
        this.basePath = basePath;
    }

    /**
     * Sendet Patientendaten an eine proprietäre API.
     *
     * @param person
     *         {@link Person}-Objekt, das die Patientendaten enthält
     * @return true, wenn die API-Anfrage erfolgreich war; false, wenn ein Fehler aufgetreten ist
     */

    public boolean sendPatientData(Person person) {
        try {
            // URL der proprietären API
            String url = basePath + "/Person";
            // Erstellen des Anfragekörpers mit den Patientendaten
            String requestBody = String.format("{\"firstName\":\"%s\",\"lastName\":\"%s\",\"birthDate\":\"%s\"}",
                    person.getFirstName(), person.getLastName(), person.getBirthDate());

            return sendPostRequest(url, requestBody);
        } catch (Exception e) {
            // Loggt eine Ausnahme, falls eine auftritt
            logger.log(Level.SEVERE, "Exception occurred while sending patient data", e);
            return false;
        }
    }

    public boolean sendDocument(Document document) {
        try {
            String url = basePath + "/Document";
            String requestBody = String.format("{\"kdlCode\":\"%s\"," +
                            "\"patientId\":\"%s\"," +
                            "\"visitNumber\":\"%s\"" +
                            "\"dateCreated\":\"%s\"" +
                            "\"contentB64\":\"%s\"" +
                            "}",
                    document.getKdlSource(), document.getPatientId(), document.getVisitNumber(), document.getDateCreated(), Arrays.toString(document.getContentB64()));
            return sendPostRequest(url, requestBody);

        } catch (Exception e) {
            // Loggt eine Ausnahme, falls eine auftritt
            logger.log(Level.SEVERE, "Exception occurred while sending patient data", e);
            return false;
        }
    }

    private boolean sendPostRequest(String url, String requestBody) {
        // Loggt die URL und den Anfragekörper
        logger.info(() -> "Sending request to proprietary API: " + url);
        logger.info(() -> "Request body: " + requestBody);

        // Sendet eine POST-Anfrage an die proprietäre API
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestBody, String.class);

        // Loggt den Statuscode der Antwort
        logger.info("Response from proprietary API: " + response.getStatusCode());
        HttpStatus statusCode = response.getStatusCode();

        // Akzeptiere s 201 (Created) als erfolgreichen Status
        if (statusCode == HttpStatus.CREATED) {
            return true;
        } else {
            // Loggt einen Fehler, wenn der Statuscode nicht 200 oder 201 ist
            logger.severe(() -> "Proprietary API returned an error: " + response.getBody());
            return false;
        }
    }
}
