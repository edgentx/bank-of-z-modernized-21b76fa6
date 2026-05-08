package com.example.ports;

import java.util.concurrent.CompletableFuture;

public interface SlackPort {
    CompletableFuture<Void> sendMessage(String channel, String message);
}
