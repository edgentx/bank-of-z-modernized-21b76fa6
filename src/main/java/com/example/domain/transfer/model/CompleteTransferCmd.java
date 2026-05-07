package com.example.domain.transfer.model;
import com.example.domain.shared.Command;
public record CompleteTransferCmd(String transferId) implements Command {}
