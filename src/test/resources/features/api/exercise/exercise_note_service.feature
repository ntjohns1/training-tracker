Feature: ExerciseNote Service
  As a user of the training tracker system
  So that I can create, read, update, and delete exercise notes with proper business logic

  Scenario: Create a new exercise note successfully
    Given I have a valid ExerciseNoteRequest DTO with exercise note data
    When I call createExerciseNote with the DTO
    Then the service should use ExerciseNoteFactory to create the entity
    And the entity should be saved to the repository
    And the entity should have the correct relationships - exercise, user
    And an ExerciseNoteResponse DTO should be returned
    And the response should contain the created exercise note data
    And proper timestamps should be set on the entity

  Scenario: Retrieve an existing exercise note by ID
    Given an exercise note exists in the system with ID 1
    When I call getExerciseNote with ID 1
    Then the service should retrieve the entity from the repository
    And the entity should be converted to ExerciseNoteResponse DTO
    And the response should contain all exercise note data
    And the response should be returned successfully

  Scenario: Handle exercise note not found during retrieval
    Given no exercise note exists with ID 999
    When I call getExerciseNote with ID 999
    Then a RuntimeException should be thrown
    And the exception message should indicate "Exercise note not found with id: 999"
    And no DTO should be returned

  Scenario: Update an existing exercise note successfully
    Given an exercise note exists in the system with ID 1
    And I have a valid ExerciseNoteRequest DTO with updated exercise note data
    When I call updateExerciseNote with ID 1 and the DTO
    Then the service should find the existing entity
    And the service should use ExerciseNoteFactory to update the entity
    And the updated entity should be saved to the repository
    And an ExerciseNoteResponse DTO should be returned
    And the response should contain the updated exercise note data
    And proper timestamps should be set on the entity

  Scenario: Delete an existing exercise note successfully
    Given an exercise note exists in the system with ID 1
    When I call deleteExerciseNote with ID 1
    Then the service should find the existing entity
    And the entity should be marked as deleted
    And the entity should be saved to the repository
    And an ExerciseNoteResponse DTO should be returned
    And the response should contain the deleted exercise note data

