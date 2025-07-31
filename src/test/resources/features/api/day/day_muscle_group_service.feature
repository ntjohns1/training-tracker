Feature: Day Muscle Group Service
  As a user of the training tracker system
  I want to manage day muscle groups through a clean service layer
  So that I can create, read, update, and delete day muscle groups with proper relationships

  Background:
    Given the DayMuscleGroupService will be configured with simplified architecture
    And the service will use DayMuscleGroupRepo for data persistence
    And the service will use DayMuscleGroupMapper for DTO conversions


  Scenario: Create a new day muscle group successfully
    Given I have a valid DayMuscleGroupRequest DTO with day and muscle group IDs
    And the day and muscle group entities exist in the system
    When I call createDayMuscleGroup with the DTO
    Then the service should use DayMuscleGroupMapper to create the entity
    And the entity should be saved to the repository
    And the day muscle group, progression, and day exercise relationships should be established
    And a DayMuscleGroupResponse DTO should be returned
    And proper timestamps should be set on the entity

  Scenario: Handle day not found during day muscle group creation
    Given I have a DayMuscleGroupRequest DTO with non-existent day ID
    When I call createDayMuscleGroup with the DTO
    Then the service should throw a RuntimeException
    And the exception message should indicate day not found
    And no day muscle group entity should be created or saved

  Scenario: Handle muscle group not found during day muscle group creation
    Given I have a DayMuscleGroupRequest DTO with non-existent muscle group ID
    When I call createDayMuscleGroup with the DTO
    Then the service should throw a RuntimeException
    And the exception message should indicate muscle group not found
    And no day muscle group entity should be created or saved

  Scenario: Retrieve all day muscle groups
    Given I have day muscle groups in the system
    When I call getAllDayMuscleGroups
    Then the service should retrieve all entities from the repository
    And each entity should be converted to DayMuscleGroupResponse DTO
    And a list of all day muscle groups should be returned

  Scenario: Retrieve all day muscle groups for a specific day
    Given I have day muscle groups in the system for a specific day
    When I call getAllDayMuscleGroupsForDay with the day ID
    Then the service should retrieve all entities from the repository
    And each entity should be converted to DayMuscleGroupResponse DTO
    And a list of all day muscle groups for the day should be returned

  Scenario: Retrieve a specific day muscle group by ID
    Given I have a day muscle group in the system with ID 1
    When I call getDayMuscleGroupById with ID 1
    Then the service should retrieve the entity from the repository
    And the entity should be converted to DayMuscleGroupResponse DTO
    And the response should contain the day muscle group data
    And the response should be returned successfully

  Scenario: Handle mark day muscle group as completed
    Given a day muscle group exists in the system with ID 1
    And the day muscle group is not completed
    And all DayExercises are completed or skipped for the day muscle group
    When I call completeDayMuscleGroup with ID 1
    Then the service should retrieve the entity from the repository
    And the day muscle group should be marked as completed
    And the response should be returned successfully