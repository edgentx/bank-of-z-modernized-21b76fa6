Feature: Project scaffold with Aggregate Execute(cmd) pattern

  Scenario: Build succeeds with zero errors
    Given the Java project has pom.xml with the correct parent and group id
    And src/main/java/com/example/domain/shared contains AggregateRoot and DomainEvent
    And src/main/java/com/example/domain/shared contains an Aggregate interface with an execute(Command) method
    When I run mvn compile
    Then the build succeeds with zero errors

  Scenario: Aggregate interface defines Execute contract
    Given src/main/java/com/example/domain/shared/Aggregate.java exists
    Then it defines an Aggregate interface with: List<DomainEvent> execute(Command cmd), String id(), int getVersion()
    And AggregateRoot provides a base class with version and uncommitted-event tracking
    And UnknownCommandException is thrown for unrecognized commands

  Scenario: Each bounded context has an empty aggregate stub
    Given aggregate stubs exist for Customer, Account, Statement, Transaction, Transfer, ReconciliationBatch, TellerSession, ScreenMap
    Then each extends AggregateRoot and overrides execute(Command) returning List<DomainEvent>
    And each throws UnknownCommandException when the command type is not handled

  Scenario: Mock repositories implement domain interfaces — tests live under tests/ NOT src/test/
    Given tests/java/com/example/mocks contains in-memory repository implementations
    Then each mock implements the corresponding domain repository interface
    And mvn test runs the mock repository contract tests successfully
    And NO test files exist under src/test/ (DDD+Hex convention)
