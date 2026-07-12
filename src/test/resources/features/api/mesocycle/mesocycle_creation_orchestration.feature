Feature: Mesocycle Creation Orchestration
  As a user creating a new mesocycle
  I want the system to orchestrate the creation of all related entities
  So that I have a complete, properly structured training program

  Background:
    Given the system has the following exercises available:
      | exerciseId | name           | muscleGroupId |
      | 216        | Forearm Curls  | 11           |
      | 199        | Calf Raises    | 9            |
      | 200        | Seated Calves  | 9            |
      | 429281     | Standing Curls | 11           |
    And the system has the following muscle groups:
      | muscleGroupId | name      |
      | 9             | Calves    |
      | 11            | Forearms  |

  Scenario: Create mesocycle with nested day and exercise structure
    Given I have a mesocycle creation request:
      """
      {
        "name": "Forearms & Calves",
        "weeks": 6,
        "days": [
          {
            "label": null,
            "exercises": [
              {"exerciseId": 216},
              {"exerciseId": 199}
            ]
          },
          {
            "label": null,
            "exercises": [
              {"exerciseId": 200},
              {"exerciseId": 429281}
            ]
          }
        ],
        "unit": "lb",
        "progressions": {
          "9": {
            "mgProgressionType": "regular",
            "muscleGroupId": 9
          },
          "11": {
            "mgProgressionType": "regular",
            "muscleGroupId": 11
          }
        },
        "sourceTemplateId": null,
        "sourceMesoId": null
      }
      """
    When I create the mesocycle
    Then the mesocycle should be created successfully
    And the system should create 72 Day entities (6 weeks × 2 days × 6 weeks)
    And the system should create 288 DayExercise entities (72 days × 4 exercises)
    And the system should create 144 DayMuscleGroup entities (72 days × 2 muscle groups)
    And the system should create 2 Progression entities (one per muscle group)
    And ExerciseSet entities should only be created for the first week
    And all DayMuscleGroup entities should have status "unprogrammed" except first week
    And first week DayMuscleGroup entities should have status "programmed"

  Scenario: Mesocycle creation with progression configuration
    Given I have a mesocycle creation request with progression types:
      | muscleGroupId | progressionType |
      | 9             | regular         |
      | 11            | intensive       |
    When I create the mesocycle
    Then each Progression entity should have the correct progression type
    And each Progression entity should reference the correct muscle group
    And Progression entities should store algorithm parameters for their muscle group

  Scenario: Mesocycle creation validation
    Given I have an invalid mesocycle creation request with missing required fields
    When I attempt to create the mesocycle
    Then the creation should fail with validation errors
    And no entities should be created in the database

  Scenario: Mesocycle creation with complex day structure
    Given I have a mesocycle creation request with 4 days per week and 8 weeks
    When I create the mesocycle
    Then the system should create 32 Day entities (4 days × 8 weeks)
    And each Day should have the correct week and position values
    And Day entities should be properly sequenced across weeks

  Scenario: Mesocycle creation orchestration failure handling
    Given I have a valid mesocycle creation request
    But the exercise with ID 216 does not exist in the system
    When I attempt to create the mesocycle
    Then the creation should fail with a clear error message
    And no partial entities should be left in the database
    And the transaction should be rolled back completely
