package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to render a specific screen based on device type.
 */
public class RenderScreenCmd implements Command {
    private final String screenId;
    private final String deviceType;

    public RenderScreenCmd(String screenId, String deviceType) {
        this.screenId = screenId;
        this.deviceType = deviceType;
    }

    public String getScreenId() {
        return screenId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderScreenCmd that = (RenderScreenCmd) o;
        return Objects.equals(screenId, that.screenId) && Objects.equals(deviceType, that.deviceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(screenId, deviceType);
    }
}
