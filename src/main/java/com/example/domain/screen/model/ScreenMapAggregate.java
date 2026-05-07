package com.example.domain.screen.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

/**
 * BMS ScreenMap aggregate — preserves a 3270 / CICS BMS screen layout
 * during the COBOL→Java/Spring modernization. Each registered map captures
 * the original screen identifier, terminal dimensions, and field metadata
 * so the modernized stack can render an equivalent UI without losing the
 * mainframe-era screen behavior.
 */
public class ScreenMapAggregate extends AggregateRoot {
  private final String screenMapId;
  private String mapName;
  private int rows;
  private int columns;
  private String layoutSpec;
  private boolean registered;

  public ScreenMapAggregate(String screenMapId) {
    this.screenMapId = screenMapId;
  }

  @Override public String id() { return screenMapId; }

  @Override public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof RegisterScreenMapCmd c) {
      if (registered) throw new IllegalStateException("ScreenMap already registered: " + c.screenMapId());
      if (c.mapName() == null || c.mapName().isBlank()) throw new IllegalArgumentException("mapName required");
      if (c.rows() <= 0 || c.columns() <= 0) throw new IllegalArgumentException("rows/columns must be positive");
      if (c.layoutSpec() == null || c.layoutSpec().isBlank()) throw new IllegalArgumentException("layoutSpec required");
      var event = new ScreenMapRegisteredEvent(c.screenMapId(), c.mapName(), c.rows(), c.columns(), c.layoutSpec(), Instant.now());
      this.mapName = c.mapName();
      this.rows = c.rows();
      this.columns = c.columns();
      this.layoutSpec = c.layoutSpec();
      this.registered = true;
      addEvent(event);
      incrementVersion();
      return List.of(event);
    }
    throw new UnknownCommandException(cmd);
  }

  public boolean isRegistered() { return registered; }
  public String getMapName() { return mapName; }
  public int getRows() { return rows; }
  public int getColumns() { return columns; }
  public String getLayoutSpec() { return layoutSpec; }
}
