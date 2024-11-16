package com.example.demo.model.mapper;

import com.example.demo.model.Document;
import org.hl7.fhir.r4.model.DocumentReference;

public class DocumentMapper {
    private DocumentMapper() {
    }

    public static Document mapDocumentReferenceToDocument(DocumentReference documentReference) {
        Document document = new Document();

        // https://simplifier.net/packages/de.ihe-d.terminology/3.0.0/files/2374174/~overview
        document.setContentB64(documentReference.getContent().get(0).getAttachment().getData());
        return document;
    }
}
