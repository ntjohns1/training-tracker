Feature: Day Muscle Group Service
  As a user of the training tracker system
  I want to manage day muscle groups through a clean service layer
  So that I can create, read, update, and delete day muscle groups with proper relationships

  Background:
    Given the DayMuscleGroupService will be configured with simplified architecture
    And the service will use DayMuscleGroupRepo for data persistence
    And the service will use DayMuscleGroupMapper for DTO conversions


    Scenario: Create a new day muscle group successfully with relationships
      Given I have a valid DayMuscleGroupRequest DTO with user ID
      And the user entity exists in the system
      When I call createDayMuscleGroup with the DTO
      Then the service should use DayMuscleGroupFactory to create the entity
      And the service should establish User relationships
      And the service should establish Day relationships
      And the service should establish MuscleGroup relationships
      And the service should establish DayExercise relationships (transient)
      And the entity should be saved to the repository