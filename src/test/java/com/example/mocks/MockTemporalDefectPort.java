package com.example.mocks;

import com.example.ports.TemporalDefectPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Temporal workflow triggering.
 */
public class MockTemporalDefectPort implements TemporalDefectPort {

    public boolean triggered = false;
    public String lastDefectId;
    public final List<String> triggeredDefects = new ArrayList<>();

    @Override
    public void triggerDefectReport(String defectId, String description) {
        this.triggered = true;
        this.lastDefectId = defectId;
        this.triggeredDefects.add(defectId);
    }

    public void reset() {
        triggered = false;
        lastDefectId = null;
        triggeredDefects.clear();
    }
}
