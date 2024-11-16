package com.example.demo.util;

import org.hl7.fhir.r4.model.DateType;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DateConverterTest {

    @Test
    void convertDate() {
        String s = DateConverter.convertDate((Date) null);
        assertThat(s).isEmpty();
    }

    @Test
    void convertDateType() {
        String s = DateConverter.convertDate((DateType) null);
        assertThat(s).isEmpty();
    }
}
