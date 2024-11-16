package com.example.demo.model;

public class Document {
    String kdlSource;
    Integer patientId;
    Integer visitNumber;
    String dateCreated;
    byte[] contentB64;

    public Document() {
    }

    public Document(String kdlSource, Integer patientId, Integer visitNumber, String dateCreated, byte[] contentB64) {
        this.kdlSource = kdlSource;
        this.patientId = patientId;
        this.visitNumber = visitNumber;
        this.dateCreated = dateCreated;
        this.contentB64 = contentB64;
    }

    public String getKdlSource() {
        return kdlSource;
    }

    public void setKdlSource(String kdlSource) {
        this.kdlSource = kdlSource;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getVisitNumber() {
        return visitNumber;
    }

    public void setVisitNumber(Integer visitNumber) {
        this.visitNumber = visitNumber;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public byte[] getContentB64() {
        return contentB64;
    }

    public void setContentB64(byte[] contentB64) {
        this.contentB64 = contentB64;
    }
}
