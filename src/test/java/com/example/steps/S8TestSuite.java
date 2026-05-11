package com.example.steps;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-8 (GenerateStatementCmd).
 * Executed via JUnit Platform Suite.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-8.feature")
public class S8TestSuite {
}
