package com.example.api.dashboard.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lightweight aggregate counters for the teller landing page.
 *
 * Frontend contract: {@code dashboard/lib/api/dashboard.ts#DashboardSummary}.
 * Field shape and names must stay in sync (Jackson serialises with default
 * camelCase, matching the TS interface).
 *
 * <p>Demo-readiness scaffold: numbers are placeholders until the read-side
 * projection stories ship. See follow-up issue for wiring real counters
 * sourced from MongoDB projections of account/customer/transaction events.
 */
public record DashboardSummaryResponse(
    Instant generatedAt,
    String tellerId,
    String branch,
    long openAccountCount,
    long closedTodayCount,
    long activeCustomerCount,
    long pendingTransactionCount,
    long postedTransactionCount,
    BigDecimal totalDepositsToday,
    BigDecimal totalWithdrawalsToday,
    String currency) {}
