package com.example.config;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class PortConfiguration {

    // @Primary Beans for testing purposes are now in the test context configuration,
    // but we keep the file structure consistent with the main source if required by the build.
    // However, the build errors referenced Mock classes here. We will leave this empty
    // and ensure the Mocks are exposed via the Test context in the test source folder.
}
