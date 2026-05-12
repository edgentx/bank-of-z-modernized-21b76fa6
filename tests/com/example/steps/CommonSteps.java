package com.example.steps;

import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;

/**
 * Step definitions shared by every story across every aggregate.
 *
 * The "the command is rejected with a domain error" @Then must exist
 * in exactly one place — Cucumber treats identical step text in two
 * different classes as a glue conflict and fails the whole suite.
 * Each story Steps class writes {@link ScenarioContext#thrownException}
 * in its @When catch block; this class is the only reader.
 */
public class CommonSteps {

    private final ScenarioContext sc;

    public CommonSteps(ScenarioContext sc) {
        this.sc = sc;
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(
                sc.thrownException,
                "Expected a domain error but the command completed successfully");
        Assertions.assertTrue(
                sc.thrownException instanceof IllegalArgumentException
                        || sc.thrownException instanceof IllegalStateException,
                "Expected IllegalArgumentException or IllegalStateException, got: "
                        + sc.thrownException.getClass());
    }
}
