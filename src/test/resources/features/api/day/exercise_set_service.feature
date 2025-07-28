Feature: Exercise Set Service
    As a user of the training tracker system
    I want to manage exercise sets through a clean service layer
    So that I can create, read, update, and delete exercise sets with proper relationships

    Background:
      Given the ExerciseSetService will be configured with simplified architecture
      And the service will use ExerciseSetRepo for data persistence
      And the service will use ExerciseSetMapper for DTO conversions
      And no redundant orchestration or CRUD service layers will exist


      Scenario: Create a new exercise set successfully with relationships
        Given I have a valid ExerciseSetRequest DTO with exercise ID
        And the day, day exercise, and exercise entity exists in the system
        When I call createExerciseSet with the DTO
        Then the service should use ExerciseSetFactory to create the entity
        And the service should establish User, Day, DayExercise, and Exercise relationships
        And the entity should be saved to the repository

