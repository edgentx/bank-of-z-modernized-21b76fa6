package com.example.domain;

/**
 * Marker interface for events resulting from S-11 commands.
 */
public sealed interface S11Event permits WithdrawalPostedEvent {
}
