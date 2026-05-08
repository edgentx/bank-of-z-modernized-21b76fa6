package com.example.infrastructure;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface TemporalActivities {
    String postToSlack(String message);
}
