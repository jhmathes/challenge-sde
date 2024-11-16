package com.example.demo.service.mapper;

import com.example.demo.entities.Person;
import com.example.demo.service.DateConverter;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PrimitiveType;

import java.util.stream.Collectors;

public class PersonMapper {

    public static Person mapPatientToPerson(Patient patient) {
        // Extrahieren des Vornamens aus der Patient-Ressource
        String firstName = patient.getName().get(0).getGiven().stream()
                .map(PrimitiveType::getValue)
                .collect(Collectors.joining(" "));
        // Extrahieren des Nachnamens aus der Patient-Ressource
        String lastName = patient.getName().get(0).getFamily();

        // Konvertierung des Geburtsdatums in das gewünschte Format
        String birthDate = DateConverter.convertDate(patient.getBirthDateElement());
        return new Person(firstName, lastName, birthDate);
    }
}
