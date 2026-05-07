package com.example.runners;

import org.junit.platform.suite.api.Configuration;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@Configuration(
        // Ensure Spring context is shared if needed, though we manage it via @SpringBootTest in steps
        // For standard JUnit 5 + Cucumber, this minimal configuration often suffices if glue path is auto-detected
        // However, explicitly specifying glue is safer:
        // plugin = {"pretty"} is not available via this annotation easily without properties file.
        // We rely on defaults.
        strict = true
)
public class CucumberTestSuite {
}
