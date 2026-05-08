package com.example.ports;

import com.example.domain.shared.DomainEvent;

public interface SlackNotifierPort {
    void notify(DomainEvent event, String messageBody);
}
