package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-3 Feature (UpdateCustomerDetailsCmd).
 * Run with 'mvn test -Dtest=S3TestSuite' or similar JUnit Platform configuration.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-3.feature")
public class S3TestSuite {
}
