package com.example.domain.uinavigation.model;

import java.util.List;
import java.util.Map;

public class ScreenMap {
    private final List<String> mandatoryFields;
    private final Map<String, Integer> fieldLengthConstraints; // field name -> max length

    public ScreenMap(List<String> mandatoryFields, Map<String, Integer> fieldLengthConstraints) {
        this.mandatoryFields = mandatoryFields;
        this.fieldLengthConstraints = fieldLengthConstraints;
    }

    public List<String> getMandatoryFields() {
        return mandatoryFields;
    }

    public Map<String, Integer> getFieldLengthConstraints() {
        return fieldLengthConstraints;
    }
}
