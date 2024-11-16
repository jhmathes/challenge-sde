package com.example.demo.service;

import org.hl7.fhir.r4.model.DateType;

import javax.annotation.Nonnull;

/**
 * Diese Klasse enthält Methoden zum Konvertieren von Datumsobjekten.
 */
public class DateConverter {

    private DateConverter() {
    }

    /**
     * Konvertiert ein DateType-Objekt in einen String im Format "TT.MM.JJJJ".
     *
     * @param dateType
     *         Typ {@link org.hl7.fhir.r4.model.DateType}
     * @return Ein String im Format "TT.MM.JJJJ"
     */
    @Nonnull
    public static String convertDate(DateType dateType) {
        if (dateType == null) {
            return "";
        }
        return dateType.getDay() + "." + dateType.getMonth() + "." + dateType.getYear();
    }
}
