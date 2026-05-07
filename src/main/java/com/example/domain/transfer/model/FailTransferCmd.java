package com.example.domain.transfer.model;
import com.example.domain.shared.Command;
public record FailTransferCmd(String transferId, String reason) implements Command {}
