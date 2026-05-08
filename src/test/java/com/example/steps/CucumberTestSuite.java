package com.example.steps;

import com.example.Application;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

// Ideally we avoid loading the full application context for unit domain tests,
// but to enable @Autowired repositories in Steps easily:
@CucumberContextConfiguration
@SpringBootTest(classes = Application.class)
public class CucumberTestSuite {}
