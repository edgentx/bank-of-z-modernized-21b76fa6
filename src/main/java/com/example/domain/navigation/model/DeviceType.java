package com.example.domain.navigation.model;

/**
 * Target device type for the screen rendering.
 * Enforces BMS constraints.
 */
public enum DeviceType {
    TERMINAL_3270,
    WEB,
    MOBILE_TABLET,
    MOBILE_PHONE;

    public boolean isBmsCompatible() {
        return this == TERMINAL_3270;
    }
}
