package com.example.steps;

import com.example.domain.validation.ReportDefectE2ETest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite to run the E2E test for S-FB-1.
 */
@Suite
@SelectClasses(ReportDefectE2ETest.class)
public class ReportDefectTestSuite {
}
