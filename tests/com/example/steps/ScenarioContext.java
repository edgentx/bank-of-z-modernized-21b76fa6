package com.example.steps;

/**
 * Cucumber-scoped scenario state shared by every step class.
 *
 * Before this existed, each story Steps class kept its own private
 * {@code thrownException} field and a duplicate @Then("the command is
 * rejected with a domain error") that read it. Cucumber refuses to load
 * a glue package that defines the same step text in two classes, so the
 * Phase-2 BDD suite could never run end-to-end. CommonSteps now owns the
 * single rejection assertion and reads it from this context; every
 * Steps @When catch block writes {@code sc.thrownException = e}.
 *
 * Picocontainer instantiates one instance per scenario, so state does
 * not leak across scenarios.
 */
public class ScenarioContext {
    public Throwable thrownException;
}
