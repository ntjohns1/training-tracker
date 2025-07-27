Feature: Mesocycle Factory
  As a developer working with the training tracker system
  I want a factory class that creates Mesocycle entities consistently
  So that I can eliminate redundant instantiation patterns and improve code maintainability

  Background:
    Given the MesocycleFactory is properly configured with dependencies
    And the system has access to MesocycleMapper for DTO conversions

  Scenario: Create new Mesocycle from Response DTO
    Given I have a valid MesocycleResponse DTO with basic mesocycle data
    When I call createFromResponse with the DTO
    Then a new Mesocycle entity should be created
    And the entity should have proper timestamps set (createdAt and updatedAt)
    And all fields from the DTO should be mapped correctly
    And the entity should be ready for persistence

  Scenario: Create new Mesocycle with all optional fields
    Given I have a MesocycleResponse DTO with all optional fields populated
    When I call createFromResponse with the complete DTO
    Then a new Mesocycle entity should be created with all fields mapped
    And timestamps should be set to current time
    And optional fields like sourceTemplate and sourceMeso should be preserved
    And the entity should maintain data integrity

  Scenario: Create Mesocycle for soft delete operation
    Given I have an existing Mesocycle entity
    When I call createForSoftDelete with the existing entity
    Then a new Mesocycle entity should be created based on the existing one
    And the deletedAt timestamp should be set to current time
    And the updatedAt timestamp should be set to current time
    And all other fields should remain unchanged from the original entity
    And the original entity should remain unmodified

  Scenario: Create Mesocycle for finish operation
    Given I have an existing Mesocycle entity that is not finished
    When I call createForFinish with the existing entity
    Then a new Mesocycle entity should be created based on the existing one
    And the finishedAt timestamp should be set to current time
    And the updatedAt timestamp should be set to current time
    And all other fields should remain unchanged from the original entity
    And the original entity should remain unmodified

  Scenario: Handle null input gracefully
    Given I have a null MesocycleResponse DTO
    When I call createFromResponse with the null DTO
    Then an IllegalArgumentException should be thrown
    And the exception message should indicate "MesocycleResponse cannot be null"

  Scenario: Handle null existing entity gracefully
    Given I have a null existing Mesocycle entity
    When I call createForSoftDelete with the null entity
    Then an IllegalArgumentException should be thrown
    And the exception message should indicate "Existing Mesocycle cannot be null"

  Scenario: Eliminate redundant instantiation pattern
    Given I have a MesocycleResponse DTO
    When I use the factory instead of the old mapper-then-builder pattern
    Then the entity creation should be done in a single step
    And no redundant instantiation should occur
    And the code should be more maintainable and readable
    And the performance should be improved by avoiding double instantiation

  Scenario: Consistent timestamp handling
    Given I create multiple Mesocycle entities at different times
    When I use the factory for entity creation
    Then each entity should have accurate timestamps
    And createdAt should reflect the actual creation time
    And updatedAt should be set appropriately for each operation type
    And timestamp precision should be consistent across all operations

  Scenario: Factory integration with service layer
    Given the MesocycleServiceImpl uses the MesocycleFactory
    When service methods are called for CRUD operations
    Then the factory should handle all entity creation logic
    And the service should focus on business logic and orchestration
    And the separation of concerns should be maintained
    And the architecture should be clean and testable