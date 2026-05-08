package com.example.application;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface DefectReportingActivityInterface {
    @ActivityMethod
    String reportDefect(String defectDetails);
}
