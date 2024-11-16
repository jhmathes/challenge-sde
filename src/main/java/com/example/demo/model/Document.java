package com.example.demo.model;

public class Document {
    String kdlCode;
    //SubjectReference in DocumentReference is currently only required as String
    Integer patientId;
    //SubjectReference in DocumentReference is currently only required as String
    Integer visitNumber;
    String dateCreated;
    byte[] contentB64;

    private Document(String kdlCode, Integer patientId, Integer visitNumber, String dateCreated, byte[] contentB64) {
        this.kdlCode = kdlCode;
        this.patientId = patientId;
        this.visitNumber = visitNumber;
        this.dateCreated = dateCreated;
        this.contentB64 = contentB64;
    }

    public String getKdlCode() {
        return kdlCode;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public Integer getVisitNumber() {
        return visitNumber;
    }

    public String getDateCreated() {
        return dateCreated;
    }
    
    public byte[] getContentB64() {
        return contentB64;
    }

    public static class DocumentBuilder {
        String kdlCode;
        Integer patientId;
        Integer visitNumber;
        String dateCreated;
        byte[] contentB64;

        public DocumentBuilder setKdlCode(String kdlCode) {
            this.kdlCode = kdlCode;
            return this;
        }

        public DocumentBuilder setPatientId(Integer patientId) {
            this.patientId = patientId;
            return this;
        }

        public DocumentBuilder setVisitNumber(Integer visitNumber) {
            this.visitNumber = visitNumber;
            return this;
        }

        public DocumentBuilder setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
            return this;
        }

        public DocumentBuilder setContentB64(byte[] contentB64) {
            this.contentB64 = contentB64;
            return this;
        }

        public Document build() {
            if (kdlCode == null || patientId == null || visitNumber == null || dateCreated == null || contentB64 == null) {
                throw new IllegalArgumentException("Missing required fields.");
            }
            return new Document(kdlCode, patientId, visitNumber, dateCreated, contentB64);
        }
    }
}
