package com.example.domain.shared;
public class UnknownCommandException extends RuntimeException {
  public UnknownCommandException(Command cmd) {
    super("Unknown command: " + cmd.getClass().getSimpleName());
  }
}
