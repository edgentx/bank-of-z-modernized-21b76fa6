package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountStatusUpdatedEvent;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

/**
 * Story-specific step definitions for S-6 (UpdateAccountStatusCmd).
 * Shared Account Given/Then steps live in {@link AccountSharedSteps};
 * scenario state is shared via {@link AccountSharedContext}.
 */
public class S6Steps {

    private final AccountSharedContext ctx;

    public S6Steps(AccountSharedContext ctx) {
        this.ctx = ctx;
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        ctx.accountNumber = "ACC-0000123";
    }

    @And("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        // No-op: newStatus is supplied when the command is constructed in @When.
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            AccountAggregate agg = ctx.repository.findById(ctx.aggregate.id()).orElse(ctx.aggregate);
            agg.execute(new UpdateAccountStatusCmd(agg.id(), "FROZEN"));
            ctx.repository.save(agg);
        } catch (Throwable t) {
            ctx.thrownException = t;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        Assertions.assertNull(ctx.thrownException, "Expected no error, but got: " + ctx.thrownException);
        AccountAggregate updated = ctx.repository.findById(ctx.aggregate.id()).orElseThrow();
        List<DomainEvent> events = updated.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Expected events to be emitted");
        DomainEvent event = events.get(0);
        Assertions.assertEquals("account.status.updated", event.type());
        Assertions.assertInstanceOf(AccountStatusUpdatedEvent.class, event);
    }
}
