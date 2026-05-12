package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.EnrollCustomerCmd;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

/**
 * Step definitions shared by all Customer-aggregate stories (S-3, S-4, ...).
 * Each Given seeds {@link CustomerSharedContext#aggregate}; story-specific
 * step classes ({@code S3Steps}, {@code S4Steps}) read from that context in
 * their @When/@Then methods.
 */
public class CustomerSharedSteps {

    private final CustomerSharedContext ctx;

    public CustomerSharedSteps(CustomerSharedContext ctx) {
        this.ctx = ctx;
    }

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        ctx.aggregate = new CustomerAggregate("cust-1");
        ctx.aggregate.execute(new EnrollCustomerCmd("cust-1", "John Doe", "john@example.com", "GOV-ID-123"));
        ctx.aggregate.clearEvents();
        ctx.repository.save(ctx.aggregate);
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // No-op: customerId is taken from ctx.aggregate.id() in the @When step.
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // No-op: emailAddress is supplied when the command is constructed in @When.
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // No-op: sortCode is supplied when the command is constructed in @When.
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndId() {
        // Un-enrolled customer: no email, no government ID.
        ctx.aggregate = new CustomerAggregate("cust-invalid");
        ctx.repository.save(ctx.aggregate);
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        // Enroll first (to clear the email/govID invariant), then mark the name/dob violation.
        ctx.aggregate = new CustomerAggregate("cust-no-name");
        ctx.aggregate.execute(new EnrollCustomerCmd("cust-no-name", "Bootstrap", "boot@example.com", "GOV-BOOT"));
        ctx.aggregate.clearEvents();
        ctx.aggregate.markNameDobViolation();
        ctx.repository.save(ctx.aggregate);
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        ctx.aggregate = new CustomerAggregate("cust-active");
        ctx.aggregate.execute(new EnrollCustomerCmd("cust-active", "Active User", "active@example.com", "GOV-999"));
        ctx.aggregate.clearEvents();
        ctx.aggregate.markActiveAccountsViolation();
        ctx.repository.save(ctx.aggregate);
    }

}
