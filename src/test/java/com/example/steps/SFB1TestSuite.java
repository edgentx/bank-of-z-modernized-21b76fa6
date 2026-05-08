package com.example.steps;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for S-FB-1.
 * Aggregates all steps related to Validating VW-454.
 */
@Suite
@SelectClasses({
    SFB1Steps.class
})
public class SFB1TestSuite {
    // This class is empty, it serves only as a holder for the above annotation.
}
