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
    Given I have a valid DayExerciseRequest DTO with day and exercise IDs
    And the day and exercise entities exist in the system
    When I call createDayExercise with the DTO
    Then the service should use DayExerciseFactory to create the entity
    And the service should establish Day, DayMuscleGroup, ExerciseSet, Exercise, and User relationships
    And the entity should be saved to the repository
    And a DayExerciseResponse DTO should be returned
    And proper timestamps should be set on the entity

  Scenario: Handle missing relationships during day exercise creation
    Given I have a DayExerciseRequest DTO with non-existent day or exercise ID
    When I call createDayExercise with the DTO
    Then the service should throw a RuntimeException
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
    And I have updated day exercise data in a DayExerciseRequest DTO
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

    Scenario: Complete a day exercise
      Given a day exercise exists in the system with ID 1
      And the day exercise is not completed
      And all ExerciseSets are completed or skipped for the day exercise
      When I call completeDayExercise with ID 1
      Then the service should retrieve the entity from the repository
      And the response should be returned successfully
      And the corresponding DayMuscleGroup entity should be updated

      Scenario: Handle day exercise not found during completion
        Given no day exercise exists with ID 999
        When I call completeDayExercise with ID 999
        Then a RuntimeException should be thrown
        And the exception message should indicate "Day exercise not found with id: 999"
        And no completion should be performed

        Scenario: Retrieve day exercises by exercise ID
          Given multiple day exercises exist for exercise ID 1
          When I call getDayExercisesByExerciseId with exercise ID 1
          Then the service should retrieve entities for that exercise from the repository
          And each entity should be converted to DayExerciseResponse DTO
          And a list of DayExerciseResponse DTOs should be returned
          And only day exercises belonging to the specified exercise should be included
