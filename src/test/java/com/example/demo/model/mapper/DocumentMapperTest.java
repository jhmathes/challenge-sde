package com.example.demo.model.mapper;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DocumentMapperTest {

    @Test
    void extractKDLCoding_emptyDocumentReference() {
        Optional<Coding> kdlCoding = DocumentMapper.extractKDLCoding(new DocumentReference());
        assertFalse(kdlCoding.isPresent());
    }

    @Test
    void getIdFromReference_emptyReference() {
        assertThatThrownBy(() -> DocumentMapper.getIdFromReference(new Reference())).isExactlyInstanceOf(IllegalArgumentException.class);
    }

}
