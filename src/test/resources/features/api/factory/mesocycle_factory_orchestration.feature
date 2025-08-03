Feature: MesocycleFactory Orchestration Integration
  As a service layer component
  I want to use MesocycleFactory to orchestrate complex entity creation
  So that mesocycle creation handles all nested relationships correctly

  Background:
    Given the MesocycleFactory is properly configured with dependencies:
      | dependency        | type                |
      | dayFactory        | DayFactory          |
      | progressionRepo   | ProgressionRepo     |
      | dayRepo           | DayRepo             |
      | dayExerciseRepo   | DayExerciseRepo     |
      | dayMuscleGroupRepo| DayMuscleGroupRepo  |
      | exerciseSetRepo   | ExerciseSetRepo     |

  Scenario: MesocycleFactory creates complete entity hierarchy
    Given I have a CreateMesocycleRequest with nested structure:
      """
      {
        "name": "Test Mesocycle",
        "weeks": 2,
        "days": [
          {
            "exercises": [{"exerciseId": 216}, {"exerciseId": 199}]
          }
        ],
        "progressions": {
          "9": {"mgProgressionType": "regular", "muscleGroupId": 9},
          "11": {"mgProgressionType": "regular", "muscleGroupId": 11}
        }
      }
      """
    When MesocycleFactory.createFromRequest() is called
    Then the factory should create the Mesocycle entity with proper timestamps
    And the factory should create 4 Day entities (2 weeks × 2 days)
    And the factory should create 8 DayExercise entities (4 days × 2 exercises)
    And the factory should create 8 DayMuscleGroup entities (4 days × 2 muscle groups)
    And the factory should create 2 Progression entities (one per muscle group)
    And the factory should create ExerciseSet entities only for the first week
    And all entities should have proper foreign key relationships
    And all timestamps should be consistent across related entities

  Scenario: Factory handles progression entity initialization
    Given I have a mesocycle request with specific progression types
    When the factory creates progression entities
    Then each Progression should be initialized with muscle-group-specific parameters
    And each Progression should have the correct progression type from the request
    And each Progression should reference the created mesocycle
    And the algorithm state should be properly initialized

  Scenario: Factory manages DayMuscleGroup status orchestration
    Given I have a mesocycle creation request
    When the factory creates DayMuscleGroup entities
    Then first week DayMuscleGroups should have status "programmed"
    And subsequent week DayMuscleGroups should have status "unprogrammed"
    And the sequence should be properly maintained across days and weeks
    And recommendedSets should be set appropriately based on status

  Scenario: Factory coordinates with DayFactory for nested creation
    Given the MesocycleFactory needs to create Day entities
    When createFromRequest() is called
    Then the factory should delegate Day creation to DayFactory
    And DayFactory should handle Day-specific logic and relationships
    And the coordination should maintain transactional integrity
    And both factories should work together seamlessly

  Scenario: Factory handles creation failure and rollback
    Given I have a mesocycle creation request
    But one of the referenced exercises does not exist
    When MesocycleFactory.createFromRequest() is called
    Then the factory should detect the validation error
    And the factory should throw a descriptive exception
    And no partial entities should be persisted
    And the transaction should be rolled back completely
    And the database should remain in a consistent state

  Scenario: Factory integrates with service layer properly
    Given MesocycleServiceImpl uses MesocycleFactory
    When the service receives a CreateMesocycleRequest
    Then the service should call factory.createFromRequest()
    And the service should handle any factory exceptions appropriately
    And the service should convert the result to response DTO
    And the service should not contain complex orchestration logic
    And the separation of concerns should be maintained

  Scenario: Factory supports different mesocycle configurations
    Given I have mesocycle requests with varying complexity:
      | weeks | daysPerWeek | exercisesPerDay | muscleGroups |
      | 4     | 3           | 2               | 2            |
      | 6     | 4           | 3               | 3            |
      | 8     | 2           | 4               | 4            |
    When the factory processes each configuration
    Then the entity counts should be calculated correctly for each scenario
    And all relationships should be properly established
    And the factory should handle the complexity scaling appropriately

  Scenario: Factory maintains referential integrity
    Given I have a complex mesocycle creation request
    When the factory creates all entities
    Then all foreign key relationships should be properly set
    And Day entities should reference the correct Mesocycle
    And DayExercise entities should reference the correct Day and Exercise
    And DayMuscleGroup entities should reference the correct Day and MuscleGroup
    And Progression entities should reference the correct Mesocycle and MuscleGroup
    And ExerciseSet entities should reference the correct DayExercise
    And no orphaned entities should exist

  Scenario: Factory performance with large mesocycles
    Given I have a large mesocycle request (12 weeks, 6 days/week, 8 exercises/day)
    When the factory processes the creation
    Then the factory should handle the volume efficiently
    And batch operations should be used where appropriate
    And memory usage should remain reasonable
    And the creation should complete within acceptable time limits
    And the database should not be overwhelmed with individual inserts
