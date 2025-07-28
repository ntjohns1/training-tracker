Feature: Exercise Service
  As a user of the training tracker system
  So that I can create, read, update, and delete exercises with proper business logic

  Scenario: Create a new exercise successfully
    Given I have a valid ExerciseRequest DTO with exercise data
    When I call createExercise with the DTO
    Then the service should use ExerciseFactory to create the entity
    And the entity should be saved to the repository
    And an ExerciseRequest DTO should be returned
    And the response should contain the created exercise data
    And proper timestamps should be set on the entity

  Scenario: Retrieve an existing exercise by ID
    Given an exercise exists in the system with ID 1
    When I call getExercise with ID 1
    Then the service should retrieve the entity from the repository
    And the entity should be converted to ExerciseRequest DTO
    And the response should contain all exercise data
    And the response should be returned successfully

  Scenario: Handle exercise not found during retrieval
    Given no exercise exists with ID 999
    When I call getExercise with ID 999
    Then a RuntimeException should be thrown
    And the exception message should indicate "Exercise not found with id: 999"
    And no DTO should be returned

  Scenario: Update an existing exercise successfully
    Given an exercise exists in the system with ID 1
    And I have a valid ExerciseRequest DTO with updated exercise data
    When I call updateExercise with ID 1 and the DTO
    Then the service should find the existing entity
    And the service should use ExerciseFactory to update the entity
    And the updated entity should be saved to the repository
    And an ExerciseRequest DTO should be returned
    And the response should contain the updated exercise data
    And proper timestamps should be set on the entity

  Scenario: Delete an existing exercise successfully
    Given an exercise exists in the system with ID 1
    When I call deleteExercise with ID 1
    Then the service should find the existing entity
    And the entity should be marked as deleted
    And the entity should be saved to the repository
    And an ExerciseRequest DTO should be returned
    And the response should contain the deleted exercise data
    And proper timestamps should be set on the entity

  Scenario: Handle exercise not found during deletion
    Given no exercise exists with ID 999
    When I call deleteExercise with ID 999
    Then a RuntimeException should be thrown
    And the exception message should indicate "Exercise not found with id: 999"
    And no DTO should be returned

  Scenario: Handle missing relationships during exercise creation
    Given I have a ExerciseRequest DTO with non-existent day or exercise ID
    When I call createExercise with the DTO
    Then the factory should throw a RuntimeException
    And the exception message should indicate the missing entity
    And no exercise entity should be created or saved

  Scenario: Handle missing relationships during exercise update
    Given I have a ExerciseRequest DTO with non-existent day or exercise ID
    When I call updateExercise with the DTO
    Then the factory should throw a RuntimeException
    And the exception message should indicate the missing entity
    And no exercise entity should be updated or saved

  Scenario: Handle missing relationships during exercise deletion
    Given I have a ExerciseRequest DTO with non-existent day or exercise ID
    When I call deleteExercise with the DTO
    Then the factory should throw a RuntimeException
    And the exception message should indicate the missing entity
    And no exercise entity should be deleted

  Scenario: Handle non user-generated exercise deletion
    Given an exercise exists in the system with ID 1 and no userId (non user-generated exercise)
    When I call deleteExercise with ID 1
    Then the factory should throw a RuntimeException
    And the exception message should indicate the missing entity
    And no exercise entity should be deleted
    
    
    