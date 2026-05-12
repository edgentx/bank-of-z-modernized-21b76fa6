package com.example.steps;

import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

/**
 * Step definitions shared by every TellerSession-aggregate story
 * (S-18 StartSession, S-19 NavigateMenu, S-20 EndSession).
 * Each Given seeds {@link TellerSessionSharedContext#aggregate}; the
 * story-specific @When step classes read the aggregate from the same
 * context. Without this consolidation, Cucumber's glue scanner sees
 * duplicate @Given text in two step classes and fails the suite.
 */
public class TellerSessionSharedSteps {

    private final TellerSessionSharedContext ctx;

    public TellerSessionSharedSteps(TellerSessionSharedContext ctx) {
        this.ctx = ctx;
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        ctx.aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Prepared in @When step (command construction).
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Prepared in @When step.
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        ctx.aggregate = new TellerSessionAggregate("session-auth-fail");
        ctx.aggregate.setAuthenticated(false);
    }

    /**
     * Shared across S-18/S-19/S-20. S-18/S-19 reject when the session is in a
     * timed-out state; S-20 rejects when the inactivity-timeout rule itself is
     * being violated (since a timed-out session can still be formally ended).
     * Set both flags so every consumer's rejection scenario fires consistently
     * from the same Gherkin Given.
     */
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        ctx.aggregate = new TellerSessionAggregate("session-timeout-fail");
        ctx.aggregate.setTimedOut(true);
        ctx.aggregate.setInactivityTimeoutRuleViolated(true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigation() {
        ctx.aggregate = new TellerSessionAggregate("session-nav-fail");
        ctx.aggregate.setNavigationStateValid(false);
    }
}
