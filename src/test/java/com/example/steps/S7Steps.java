package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.Aggregate;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S7Steps {

    private AccountAggregate aggregate;
    private String accountNumber;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        accountNumber = "ACC-123-456";
        aggregate = new AccountAggregate(accountNumber);
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus("ACTIVE");
        aggregate.setAccountType("CHECKING");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Logic handled in Given a valid Account aggregate
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance() {
        accountNumber = "ACC-HIGH-BAL";
        aggregate = new AccountAggregate(accountNumber);
        // Set balance > 0
        aggregate.setBalance(new BigDecimal("100.50"));
        aggregate.setStatus("ACTIVE");
        aggregate.setAccountType("CHECKING");
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_status() {
        accountNumber = "ACC-INACTIVE";
        aggregate = new AccountAggregate(accountNumber);
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus("DORMANT"); // Not ACTIVE
        aggregate.setAccountType("CHECKING");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        aggregate = new AccountAggregate("ACC-ORIG");
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus("ACTIVE");
        // We simulate violation by passing a different accountNumber in the cmd
        accountNumber = "ACC-FAKE";
    }

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        try {
            CloseAccountCmd cmd = new CloseAccountCmd(
                accountNumber, 
                aggregate.getBalance(), 
                aggregate.getStatus(), 
                aggregate.getAccountType()
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("account.closed", resultEvents.get(0).type());
        Assertions.assertEquals("ACC-123-456", resultEvents.get(0).aggregateId());
        Assertions.assertEquals("CLOSED", aggregate.getStatus());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // The error could be IllegalStateException or IllegalArgumentException depending on violation
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
