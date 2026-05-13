package com.example.api.dashboard;

import com.example.api.dashboard.dto.DashboardSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Teller-landing dashboard counters.
 *
 * Demo-readiness scaffold: returns placeholder values so the
 * {@code /} page in the teller frontend renders without the
 * "Could not load dashboard summary" error. The real numbers
 * must come from MongoDB read-side projections (open accounts,
 * today's deposits/withdrawals, etc.) — that wiring is filed as
 * a follow-up story.
 */
@RestController
@RequestMapping(value = "/api/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Dashboard", description = "Teller-landing counters")
public class DashboardController {

  @GetMapping("/summary")
  @Operation(summary = "Aggregate counters for the teller landing page")
  public DashboardSummaryResponse summary() {
    // TODO(read-side-projection): replace placeholders with counts queried
    // from the projections of AccountAggregate, CustomerAggregate, and
    // TransactionAggregate. Currency must match teller session locale.
    return new DashboardSummaryResponse(
        Instant.now(),
        "TELLER-001",
        "BRANCH-NYC-1",
        /* openAccountCount        */ 0L,
        /* closedTodayCount        */ 0L,
        /* activeCustomerCount     */ 0L,
        /* pendingTransactionCount */ 0L,
        /* postedTransactionCount  */ 0L,
        /* totalDepositsToday      */ BigDecimal.ZERO,
        /* totalWithdrawalsToday   */ BigDecimal.ZERO,
        "USD");
  }
}
