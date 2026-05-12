package com.example.application.tellersession;

import com.example.application.AggregateNotFoundException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class TellerSessionAppService {

  private final TellerSessionRepository repository;

  public TellerSessionAppService(TellerSessionRepository repository) {
    this.repository = repository;
  }

  public TellerSessionAggregate startSession(StartSessionCmd cmd) {
    String sessionId = UUID.randomUUID().toString();
    TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
    aggregate.execute(cmd);
    repository.save(aggregate);
    return aggregate;
  }

  public TellerSessionAggregate navigateMenu(String sessionId, NavigateMenuCmd cmd) {
    TellerSessionAggregate aggregate = repository
        .findById(sessionId)
        .orElseThrow(() -> new AggregateNotFoundException("TellerSession", sessionId));
    aggregate.execute(cmd);
    repository.save(aggregate);
    return aggregate;
  }

  public TellerSessionAggregate endSession(String sessionId, EndSessionCmd cmd) {
    TellerSessionAggregate aggregate = repository
        .findById(sessionId)
        .orElseThrow(() -> new AggregateNotFoundException("TellerSession", sessionId));
    aggregate.execute(cmd);
    repository.save(aggregate);
    return aggregate;
  }

  public TellerSessionAggregate findById(String sessionId) {
    return repository
        .findById(sessionId)
        .orElseThrow(() -> new AggregateNotFoundException("TellerSession", sessionId));
  }
}
