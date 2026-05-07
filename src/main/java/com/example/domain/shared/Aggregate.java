package com.example.domain.shared;
import java.util.List;
public interface Aggregate {
  List<DomainEvent> execute(Command cmd);
  String id();
  int getVersion();
}
