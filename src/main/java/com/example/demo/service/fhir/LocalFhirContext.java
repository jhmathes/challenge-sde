package com.example.demo.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import org.springframework.stereotype.Component;

@Component
public class LocalFhirContext {
    // Erzeugt ein FhirContext-Objekt für FHIR R4

    private final FhirContext fhirContext = FhirContext.forR4();

    public FhirContext getFhirContext() {
        return fhirContext;
    }
}
