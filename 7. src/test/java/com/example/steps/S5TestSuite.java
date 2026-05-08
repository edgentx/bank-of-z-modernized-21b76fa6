{
  "path": "src/test/java/com/example/steps/S5TestSuite.java",
  "content": "package com.example.steps;\n\nimport org.junit.platform.suite.api.IncludeEngines;\nimport org.junit.platform.suite.api.SelectClasspathResource;\nimport org.junit.platform.suite.api.Suite;\n\n@Suite\n@IncludeEngines(\"cucumber\")\n@SelectClasspathResource(\"features/S-5.feature\")\npublic class S5TestSuite {\n}\n"
}