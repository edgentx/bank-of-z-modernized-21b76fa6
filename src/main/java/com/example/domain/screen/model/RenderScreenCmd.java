package com.example.domain.screen.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout.
 * Story S-21: Implement RenderScreenCmd on ScreenMap.
 */
public class RenderScreenCmd implements Command {

    private final String screenId;
    private final DeviceType deviceType;

    private RenderScreenCmd(String screenId, DeviceType deviceType) {
        this.screenId = screenId;
        this.deviceType = deviceType;
    }

    public String screenId() {
        return screenId;
    }

    public DeviceType deviceType() {
        return deviceType;
    }

    /**
     * Builder to construct valid commands.
     * Allows enforcing invariants at creation time if desired,
     * though the Aggregate is the ultimate authority.
     */
    public static class Builder {
        private final String screenId;
        private final DeviceType deviceType;

        public Builder(String screenId, DeviceType deviceType) {
            this.screenId = screenId;
            this.deviceType = deviceType;
        }

        public RenderScreenCmd build() {
            return new RenderScreenCmd(this.screenId, this.deviceType);
        }
    }

    public enum DeviceType {
        WEB, MOBILE, TSO // Terminal Script Option for 3270
    }
}
