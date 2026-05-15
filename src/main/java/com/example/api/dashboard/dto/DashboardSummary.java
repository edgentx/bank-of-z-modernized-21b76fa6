package com.example.api.dashboard.dto;

import java.time.Instant;

public record DashboardSummary(
    Instant generatedAt,
    String tellerId,
    String branch,
    long openAccountCount,
    long closedTodayCount,
    long activeCustomerCount,
    long pendingTransactionCount,
    long postedTransactionCount,
    long totalDepositsToday,
    long totalWithdrawalsToday,
    String currency) {}
