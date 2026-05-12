package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountOpenedEvent;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

/**
 * Story-specific step definitions for S-5 (OpenAccountCmd).
 * Shared Account Given/Then steps live in {@link AccountSharedSteps};
 * scenario state is shared via {@link AccountSharedContext}.
 */
public class S5Steps {

    private final AccountSharedContext ctx;
    private final ScenarioContext sc;

    public S5Steps(AccountSharedContext ctx, ScenarioContext sc) {
        this.ctx = ctx;
        this.sc = sc;
    }

    @When("the OpenAccountCmd command is executed")
    public void theOpenAccountCmdCommandIsExecuted() {
        try {
            AccountAggregate agg = ctx.repository.findById(ctx.aggregate.id()).orElse(ctx.aggregate);
            agg.execute(new OpenAccountCmd(agg.id(), "cust-1", "CHECKING", 10000L, "12-34-56"));
            ctx.repository.save(agg);
        } catch (Throwable t) {
            ctx.thrownException = t;
            sc.thrownException = t;
        }
    }

    @Then("a account.opened event is emitted")
    public void aAccountOpenedEventIsEmitted() {
        Assertions.assertNull(ctx.thrownException, "Expected no error, but got: " + ctx.thrownException);
        AccountAggregate updated = ctx.repository.findById(ctx.aggregate.id()).orElseThrow();
        List<DomainEvent> events = updated.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Expected events to be emitted");
        DomainEvent event = events.get(0);
        Assertions.assertEquals("account.opened", event.type());
        Assertions.assertInstanceOf(AccountOpenedEvent.class, event);
    }
}
