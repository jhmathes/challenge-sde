package com.example.demo.util;

import org.hl7.fhir.r4.model.DateType;
import org.springframework.format.datetime.DateFormatter;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.Locale;

/**
 * Diese Klasse enthält Methoden zum Konvertieren von Datumsobjekten.
 */
public class DateConverter {

    private DateConverter() {
    }

    /**
     * Konvertiert ein Date-Objekt in einen String im Format "TT.MM.JJJJ".
     *
     * @param date
     *         Typ {@link java.util.Date}
     * @return Ein String im Format "TT.MM.JJJJ"
     */
    @Nonnull
    public static String convertDate(Date date) {
        if (date == null) {
            return "";
        }
        DateFormatter dateFormatter = new DateFormatter("dd.MM.yyyy");
        return dateFormatter.print(date, Locale.GERMANY);
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
