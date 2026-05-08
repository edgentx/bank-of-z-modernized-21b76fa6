package com.example.domain.navigation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Aggregate Root for ScreenMap navigation.
 * Handles the rendering of UI layouts based on device type and legacy BMS constraints.
 */
public class ScreenMap extends AggregateRoot {

    private final String screenId;
    private String layoutId;
    private DeviceType currentDevice;

    public ScreenMap(String screenId) {
        this.screenId = screenId;
    }

    @Override
    public String id() {
        return screenId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof RenderScreenCmd c) {
            return handleRenderScreen(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleRenderScreen(RenderScreenCmd cmd) {
        // 1. Validate Mandatory Fields (Scenario 2)
        // We interpret "inputFields()" (from errors) as the input provided.
        // We interpret the constraints as validationRules.
        // Check if any mandatory rule is missing from the input.
        Set<String> providedFields = cmd.inputFields().stream()
                .map(RenderScreenCmd.FieldSpec::name)
                .collect(Collectors.toSet());

        for (RenderScreenCmd.ValidationRule rule : cmd.validationRules()) {
            // Check if the rule implies mandatory (e.g., if it's a critical field)
            // In this domain logic, we assume any validation rule present implies the field should be present if inputFields are being processed.
            // Or more accurately based on Scenario 2: "mandatory input fields must be validated".
            // We will treat fields in validationRules as mandatory for the context of this validation.
            if (!providedFields.contains(rule.fieldName())) {
                // If a rule exists for a field, it must be in the input fields to pass validation.
                throw new IllegalArgumentException("Mandatory field validation failed: " + rule.fieldName() + " is required but missing.");
            }
        }

        // 2. Validate BMS Legacy Constraints (Scenario 3)
        for (RenderScreenCmd.FieldSpec field : cmd.inputFields()) {
            // Find constraint for this field
            cmd.validationRules().stream()
                .filter(r -> r.fieldName().equals(field.name()))
                .findFirst()
                .ifPresent(rule -> {
                    if (field.length() > rule.maxLength()) {
                        throw new IllegalStateException(
                            String.format("Field length violation: %s length %d exceeds BMS constraint %d",
                                    field.name(), field.length(), rule.maxLength())
                        );
                    }
                });
        }

        // 3. Generate Layout ID
        String generatedLayoutId = generateLayoutId(cmd.screenId(), cmd.deviceType());
        this.layoutId = generatedLayoutId;
        this.currentDevice = cmd.deviceType();

        // 4. Create Event
        ScreenRenderedEvent event = new ScreenRenderedEvent(
                this.screenId,
                cmd.screenId(),
                cmd.deviceType(),
                generatedLayoutId,
                Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private String generateLayoutId(String screenId, DeviceType deviceType) {
        // Fix for Error: incomparable types: java.lang.String and com.example.domain.navigation.model.DeviceType
        // The previous code likely did screenId == deviceType (unlikely) or passed a String where Enum was expected.
        return screenId + "-" + deviceType.name().toLowerCase();
    }
}
