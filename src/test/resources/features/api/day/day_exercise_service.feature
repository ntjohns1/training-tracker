Feature: Day Exercise Service
  As a user of the training tracker system
  I want to manage day exercises through a clean service layer
  So that I can create, read, update, and delete day exercises with proper relationships

  Background:
    Given the DayExerciseService will be configured with simplified architecture
    And the service will use DayExerciseFactory for entity creation
    And the service will use DayExerciseRepo for data persistence
    And the service will use DayExerciseMapper for DTO conversions
    And no redundant orchestration or CRUD service layers will exist

  Scenario: Create a new day exercise successfully with relationships
    Given I have a valid DayExerciseResponse DTO with day and exercise IDs
    And the day and exercise entities exist in the system
    When I call createDayExercise with the DTO
    Then the service should use DayExerciseFactory to create the entity
    And the factory should establish day and exercise relationships
    And the entity should be saved to the repository
    And a DayExerciseResponse DTO should be returned
    And proper timestamps should be set on the entity

  Scenario: Handle missing relationships during day exercise creation
    Given I have a DayExerciseResponse DTO with non-existent day or exercise ID
    When I call createDayExercise with the DTO
    Then the factory should throw a RuntimeException
    And the exception message should indicate the missing entity
    And no day exercise entity should be created or saved

  Scenario: Retrieve an existing day exercise by ID
    Given a day exercise exists in the system with ID 1
    When I call getDayExercise with ID 1
    Then the service should retrieve the entity from the repository
    And the entity should be converted to DayExerciseResponse DTO
    And the response should contain all day exercise data including relationships
    And the response should be returned successfully

  Scenario: Update an existing day exercise
    Given a day exercise exists in the system with ID 1
    And I have updated day exercise data in a DayExerciseResponse DTO
    When I call updateDayExercise with ID 1 and the updated DTO
    Then the service should find the existing entity
    And the entity should be updated using the mapper's merge functionality
    And the updatedAt timestamp should be set to current time
    And the updated entity should be saved to the repository
    And a DayExerciseResponse DTO should be returned with updated data

  Scenario: Delete a day exercise
    Given a day exercise exists in the system with ID 1
    When I call deleteDayExercise with ID 1
    Then the service should verify the entity exists
    And the entity should be deleted from the repository
    And no response should be returned

  Scenario: Retrieve day exercises by day ID
    Given multiple day exercises exist for day ID 1
    When I call getDayExercisesByDayId with day ID 1
    Then the service should retrieve entities for that day from the repository
    And each entity should be converted to DayExerciseResponse DTO
    And a list of DayExerciseResponse DTOs should be returned
    And only day exercises belonging to the specified day should be included

  Scenario: Service layer architecture validation (Future Implementation)
    Given the DayExerciseService implementation will be refactored
    Then it should use only the simplified Service + Factory pattern
    And it should directly use DayExerciseFactory for entity creation
    And it should directly use DayExerciseRepo for data access
    And it should directly use DayExerciseMapper for DTO conversions
    And it should NOT have unnecessary orchestration service dependencies
    And the architecture should be clean and maintainable

  Scenario: Eliminate redundant instantiation in service layer (Future Implementation)
    Given any day exercise operation with relationships is performed
    When the service processes the request
    Then no mapper.toEntity() followed by builder reconstruction should occur
    And the factory should handle all entity creation and relationship logic
    And the service should focus on business logic and validation
    And the code should be more maintainable and performant

  Scenario: Factory integration eliminates multiple repository dependencies
    Given the DayExerciseService needs to create entities with day and exercise relationships
    When any day exercise creation operation is performed
    Then the service should NOT directly access DayRepo or ExerciseRepo
    And the DayExerciseFactory should handle relationship establishment
    And the service should have cleaner dependencies
    And separation of concerns should be maintained

  Scenario: Handle complex day exercise with exercise sets
    Given I have a DayExerciseResponse DTO with nested exercise sets
    When I call createDayExercise with the complex DTO
    Then the factory should handle the day exercise creation
    And nested exercise set relationships should be properly established
    And all entities should be persisted correctly
    And the response should include all nested data

  Scenario: Consistent with established factory pattern
    Given the DayExerciseService follows the same pattern as MesocycleService and DayService
    When any operation is performed
    Then the architectural consistency should be maintained
    And the same benefits of the factory pattern should be realized
    And the codebase should have uniform service layer architecture
    And maintenance and testing should be simplified