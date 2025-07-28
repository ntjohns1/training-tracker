Feature: Day Factory
  As a developer working with the training tracker system
  I want a factory class that creates Day entities consistently
  So that I can eliminate redundant instantiation patterns and handle mesocycle relationships properly

  Background:
    Given the DayFactory is properly configured with dependencies
    And the system has access to DayMapper for DTO conversions
    And the system has access to MesocycleRepo for relationship handling

  Scenario: Create new Day from Request DTO
    Given I have a valid DayRequest DTO with a mesocycle ID
    And the mesocycle exists in the database
    When I call createFromRequest with the DTO
    Then a new Day entity should be created
    And the entity should have proper timestamps set
    And the mesocycle relationship should be properly established
    And all other fields should be mapped correctly
    And the entity should be ready for persistence

  Scenario: Handle mesocycle not found during Day creation
    Given I have a DayRequest DTO with a non-existent mesocycle ID
    When I call createFromRequest with the DTO
    Then a RuntimeException should be thrown
    And the exception message should indicate "Mesocycle not found with id: [id]"
    And no Day entity should be created

  Scenario: Create Day for finish operation
    Given I have an existing Day entity that is not finished
    When I call createForFinish with the existing entity
    Then a new Day entity should be created based on the existing one
    And the finishedAt timestamp should be set to current time
    And the updatedAt timestamp should be set to current time
    And all other fields should remain unchanged from the original entity
    And the mesocycle relationship should be preserved
    And the original entity should remain unmodified

  Scenario: Handle null input gracefully in createFromRequest
    Given I have a null DayRequest DTO
    When I call createFromRequest with the null DTO
    Then an IllegalArgumentException should be thrown
    And the exception message should indicate "DayRequest cannot be null"

  Scenario: Handle null input gracefully in createForFinish
    Given I have a null existing Day entity
    When I call createForFinish with the null entity
    Then an IllegalArgumentException should be thrown
    And the exception message should indicate "Existing Day cannot be null"

  Scenario: Eliminate redundant instantiation pattern
    Given I have a DayResponse DTO with mesocycle relationship
    When I use the factory instead of the old mapper-then-builder pattern
    Then the entity creation should be done in a single step
    And no redundant instantiation should occur
    And the mesocycle relationship should be handled efficiently
    And the code should be more maintainable and readable

  Scenario: Consistent timestamp handling across operations
    Given I create multiple Day entities at different times
    When I use the factory for different operations (create, finish)
    Then each entity should have accurate timestamps
    And createdAt should reflect the actual creation time for new entities
    And updatedAt should be set appropriately for each operation type
    And finishedAt should only be set during finish operations
    And timestamp precision should be consistent across all operations

  Scenario: Factory integration with DayServiceImpl
    Given the DayServiceImpl uses the DayFactory
    When service methods are called for Day operations
    Then the factory should handle all entity creation logic
    And the service should focus on business logic and validation
    And mesocycle relationship handling should be encapsulated in the factory
    And the separation of concerns should be maintained

  Scenario: Handle complex Day entity with all fields populated
    Given I have a DayResponse DTO with all optional fields populated
    When I call createFromResponse with the complete DTO
    Then a new Day entity should be created with all fields mapped
    And timestamps should be set to current time
    And the mesocycle relationship should be properly established
    And optional fields like bodyweight and label should be preserved
    And the entity should maintain data integrity

  Scenario: Factory handles concurrent Day creation
    Given multiple threads are creating Day entities simultaneously
    When each thread uses the factory for entity creation
    Then each Day entity should be created independently
    And timestamps should be accurate for each creation
    And mesocycle relationships should be handled correctly
    And no race conditions should occur in entity creation