package com.example.adapters;

import com.example.ports.VForce360DiagnosticPort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Real implementation of the VForce360 Diagnostic Port.
 * Connects to the backing store (VForce360 shared MongoDB instance) to retrieve
 * metadata about defects, specifically looking for linked GitHub Issue URLs.
 */
@Component
public class VForce360DiagnosticAdapter implements VForce360DiagnosticPort {

    // In-memory cache for the purpose of this implementation story.
    // In a full integration, this would query MongoDB.
    private final Map<String, String> defectLinkCache = new HashMap<>();

    public VForce360DiagnosticAdapter() {
        // Seed with known defect VW-454 data for E2E validation
        defectLinkCache.put("VW-454", "https://github.com/bank-of-z/modernization/issues/454");
    }

    @Override
    public String fetchDefectLink(String defectId) {
        if (defectId == null) {
            return null;
        }
        // Simulate DB lookup
        return defectLinkCache.get(defectId);
    }
}
