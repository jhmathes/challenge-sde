package com.example.demo.model.mapper;

import com.example.demo.model.Document;
import com.example.demo.util.DateConverter;
import org.hl7.fhir.r4.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class DocumentMapper {
    private DocumentMapper() {
    }

    /**
     * Parses a <a href="https://simplifier.net/isik-dokumentenaustausch-v4/ISiKDokumentenMetadaten/~overview">ISiKDokumentenMetadaten</a>
     *
     * @param documentReference
     *         {@link DocumentReference} mit dem Profil ISiKDokumentenMetadaten
     * @return Document {@link Document} mit den Metadaten des Dokuments
     */
    public static Document mapDocumentReferenceToDocument(DocumentReference documentReference) {
        if (documentReference == null || documentReference.getContent().isEmpty()) {
            throw new IllegalArgumentException("DocumentReference or content is missing.");
        }
        Document.DocumentBuilder documentBuilder = new Document.DocumentBuilder();
        // ISiKDokumenten Profil beschränkt Content auf 1, somit muss hier keine Liste verarbeitet werden
        Attachment attachment = documentReference.getContent().get(0).getAttachment();
        // two possibilities to get the creation date of the document
        // 1. documentReference.getDate() (in IHE vorhanden)
        // 2. documentReference.getContent().get(0).getAttachment().getCreation() (empfohlen)
        documentBuilder.setDateCreated(DateConverter.convertDate(attachment.getCreation()));

        extractKDLCoding(documentReference).ifPresentOrElse(coding -> documentBuilder.setKdlCode(coding.getCode()), () -> {
            throw new IllegalArgumentException("KDL code is missing.");
        });

        documentBuilder.setContentB64(attachment.getData());

        documentBuilder.setPatientId(getIdFromReference(documentReference.getSubject()));
        List<Reference> encounter = documentReference.getContext().getEncounter();
        if (encounter.isEmpty()) {
            throw new IllegalArgumentException("Encounter is missing.");
        }
        // ISiKDokumenten Profil beschränkt Encounter auf 1, somit muss hier keine Liste verarbeitet werden
        documentBuilder.setVisitNumber(getIdFromReference(encounter.get(0)));
        return documentBuilder.build();
    }

    @NotNull
    protected static Optional<Coding> extractKDLCoding(DocumentReference documentReference) {
        // KDL code is a ValueSet https://simplifier.net/packages/dvmd.kdl.r4/2024.0.0/files/2436847
        return documentReference.getType().getCoding()
                .stream().filter(coding -> coding.getSystem().equals("http://dvmd.de/fhir/CodeSystem/kdl")).findFirst();
    }

    protected static Integer getIdFromReference(Reference reference) {
        // Example Handling Reference which is required
        //        if (reference == null || reference.getReference() == null) {
        //            throw new IllegalArgumentException("Reference missing.");
        //        }
        // return reference.getReference();

        Identifier identifier = reference.getIdentifier();
        if (identifier != null) {
            try {
                return Integer.valueOf(identifier.getValue());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Identifier is not a number.");
            }
        }
        throw new IllegalArgumentException("Reference missing.");

    }
}
