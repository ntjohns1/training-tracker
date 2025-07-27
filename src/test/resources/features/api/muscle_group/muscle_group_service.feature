Feature: Muscle Group Service
  As a user of the training tracker system
  I want to retrieve muscle group reference data
  So that I can use muscle groups in my training programs

  Background:
    Given the system has muscle groups pre-populated from the MgName enum
    And muscle groups are treated as read-only reference data

  Scenario: Retrieve all muscle groups
    Given muscle groups exist in the system
    When I call getAllMuscleGroups
    Then the service should retrieve all entities from the repository
    And each entity should be converted to MuscleGroupResponse DTO
    And a list of all muscle groups should be returned
    And the list should include all MgName enum values

  Scenario: Retrieve a specific muscle group by ID
    Given a muscle group exists in the system with ID 1
    When I call getMuscleGroupById with ID 1
    Then the service should retrieve the entity from the repository
    And the entity should be converted to MuscleGroupResponse DTO
    And the response should contain the muscle group data
    And the response should be returned successfully

  Scenario: Handle muscle group not found during retrieval by ID
    Given no muscle group exists with ID 999
    When I call getMuscleGroupById with ID 999
    Then a RuntimeException should be thrown
    And the exception message should indicate "MuscleGroup not found with id: 999"
    And no DTO should be returned

  Scenario: Retrieve muscle group by name
    Given a muscle group exists with name "CHEST"
    When I call getMuscleGroupByName with "CHEST"
    Then the service should retrieve the entity by name from the repository
    And the entity should be converted to MuscleGroupResponse DTO
    And the response should contain the CHEST muscle group data

  Scenario: Handle muscle group not found during retrieval by name
    Given no muscle group exists with name "INVALID_MUSCLE"
    When I call getMuscleGroupByName with "INVALID_MUSCLE"
    Then a RuntimeException should be thrown
    And the exception message should indicate muscle group not found
    And no DTO should be returned

  Scenario: Service layer architecture validation for read-only entity
    Given the MuscleGroupService implementation
    Then it should provide only read operations (get, find, list)
    And it should NOT provide create, update, or delete operations
    And it should use MuscleGroupRepo for data access
    And it should use MuscleGroupMapper for DTO conversions
    And the architecture should reflect read-only reference data patterns

  Scenario: Muscle groups are pre-populated reference data
    Given the application starts up
    When the system initializes
    Then all MgName enum values should be present in the database
    And each muscle group should have proper timestamps
    And the data should be available for other entities to reference
    And no runtime creation of muscle groups should be needed

  Scenario: Integration with other entities
    Given muscle groups exist as reference data
    When other entities (Exercise, Progression, DayMuscleGroup) reference muscle groups
    Then the muscle group relationships should be properly established
    And the muscle group data should be available for joins and queries
    And the referential integrity should be maintained