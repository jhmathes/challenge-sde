package com.example.demo.service.mapper;

import com.example.demo.entities.Person;
import com.example.demo.service.DateConverter;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PrimitiveType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PersonMapper {
    private static final Logger logger = Logger.getLogger(PersonMapper.class.getName());

    private final static List<HumanName.NameUse> preferredNameUses = List.of(HumanName.NameUse.OFFICIAL, HumanName.NameUse.USUAL);

    private PersonMapper() {
    }

    public static Person mapPatientToPerson(Patient patient) {
        List<HumanName> name = patient.getName();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Patient name is missing.");
        }
        HumanName humanName = extractPreferredName(name);

        // Extrahieren des Vornamens aus der Patient-Ressource

        String firstName = humanName.getGiven().stream()
                .map(PrimitiveType::getValue)
                .collect(Collectors.joining(" "));
        // Extrahieren des Nachnamens aus der Patient-Ressource
        String lastName = humanName.getFamily();

        // Konvertierung des Geburtsdatums in das gewünschte Format
        String birthDate = DateConverter.convertDate(patient.getBirthDateElement());
        return new Person(firstName, lastName, birthDate);
    }

    @NotNull
    private static HumanName extractPreferredName(List<HumanName> name) {
        Map<HumanName.NameUse, HumanName> collect = name.stream().collect(Collectors.toMap(HumanName::getUse, Function.identity()));
        for (HumanName.NameUse nameUs : preferredNameUses) {
            if (collect.containsKey(nameUs)) {
                return collect.get(nameUs);
            }
        }
        logger.info("Preferred nameUse not found, using any name.");
        return collect.values().stream().findAny().orElseThrow(() -> new IllegalArgumentException("Patient name is missing."));
    }
}
