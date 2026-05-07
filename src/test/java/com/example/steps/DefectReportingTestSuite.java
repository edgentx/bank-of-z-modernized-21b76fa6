package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for Defect Reporting.
 * Maps to features/DefectReporting.feature (implied by context, though file list shows S-10, S-17)
 * Note: Assuming feature file exists or will be placed at features/DefectReporting.feature
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class DefectReportingTestSuite {
}
