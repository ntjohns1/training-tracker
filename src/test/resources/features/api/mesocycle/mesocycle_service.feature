Feature: Mesocycle Service
  As a user of the training tracker system
  So that I can create, read, update, and delete mesocycles with proper business logic

  Background:
    Given the MesocycleService will be configured with simplified architecture
    And the service will use MesocycleRepo for data persistence
    And the service will use MesocycleMapper for DTO conversions


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