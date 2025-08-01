Feature: Mesocycle Factory
  As a developer working with the training tracker system
  I want a factory class that creates Mesocycle entities consistently
  So that I can eliminate redundant instantiation patterns and improve code maintainability

  Background:
    Given the MesocycleFactory is properly configured with dependencies
    And the system has access to MesocycleMapper for DTO conversions


    Scenario: Create a new mesocycle successfully with relationships
        Given I have a valid MesocycleRequest DTO with user ID
        And the user entity exists in the system
        When I call createMesocycle with the DTO
        Then the service should use MesocycleFactory to create the entity
        And the service should establish User relationships
        And the service should establish Day relationships
        And the service should establish DayExercise relationships
        And the service should establish Exercise relationships
        And the service should establish ExerciseSet relationships
        And the service should establish MuscleGroup relationships
        And the service should establish DayMuscleGroup relationships
        And the service should establish Progression relationships
        And the Day Service should be called to create the Day entities
        And the DayExercise Service should be called to create the DayExercise entities
        And the ExerciseSet Service should be called to create the ExerciseSet entities for each DayExercise in first week
        And the DayMuscleGroup Service should be called to create the DayMuscleGroup entities
        And the Progression Service should be called to create the Progression entities
        And the entity should be saved to the repository
