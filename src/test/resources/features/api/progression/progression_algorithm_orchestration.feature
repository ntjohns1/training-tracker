Feature: Progression Algorithm Orchestration
  As a training system
  I want to calculate adaptive recommendations based on workout feedback
  So that users receive personalized progression that optimizes their training

  Background:
    Given I have a mesocycle with the following progression entities:
      | progressionId | mesocycleId | muscleGroupId | progressionType | algorithmState |
      | 1001         | 843698      | 9             | regular         | {}             |
      | 1002         | 843698      | 11            | intensive       | {}             |
    And the system has muscle-group-specific algorithm parameters:
      | muscleGroupId | name      | healingRate | thresholdMultiplier | maxSetsIncrease |
      | 9             | Calves    | 0.8         | 1.2                | 2               |
      | 11            | Forearms  | 1.0         | 1.0                | 1               |

  Scenario: Initialize progression entities for new mesocycle
    Given I create a new mesocycle with muscle groups [9, 11]
    When the mesocycle creation is orchestrated
    Then a Progression entity should be created for each muscle group
    And each Progression should have the correct progression type from the request
    And each Progression should initialize with muscle-group-specific parameters
    And the algorithm state should be empty for new progressions

  Scenario: Calculate recommendations based on workout feedback
    Given I have completed a workout with the following feedback:
      | muscleGroupId | pump | soreness | workload | dayId    |
      | 9             | 1    | -1       | 1        | 21195296 |
      | 11            | 2    | 0        | 2        | 21195296 |
    When the progression calculation is triggered
    Then the Progression entity for muscle group 9 should update its algorithm state
    And the Progression entity for muscle group 11 should update its algorithm state
    And feedback scores should be aggregated with previous data
    And running averages should be calculated for trend analysis

  Scenario: Generate set recommendations for next week based on feedback
    Given I have accumulated feedback data for calves muscle group:
      | day | pump | soreness | workload | runningScore |
      | Mon | 1    | -1       | 1        | 0.5          |
      | Thu | 2    | 0        | 2        | 1.2          |
    And the current recommended sets for calves is 3
    And the threshold for set increase is 1.0
    When I calculate recommendations for the following week
    Then the running score should exceed the threshold
    And the recommended sets should increase to 4
    And the DayMuscleGroup entities should be updated with new recommendations
    And the status should change from "unprogrammed" to "programmed"

  Scenario: Different algorithms for different progression types
    Given I have two muscle groups with different progression types:
      | muscleGroupId | progressionType | currentSets |
      | 9             | regular         | 3           |
      | 11            | intensive       | 2           |
    And both have similar feedback scores
    When progression calculations are performed
    Then the regular progression should use conservative set increases
    And the intensive progression should use aggressive set increases
    And the weight recommendations should differ based on progression type

  Scenario: Exercise type influences progression calculations
    Given I have the same muscle group trained with different exercise types:
      | exerciseId | exerciseType | muscleGroupId |
      | 216        | dumbbell     | 11           |
      | 199        | cable        | 11           |
      | 200        | barbell      | 11           |
    When progression calculations are performed
    Then dumbbell exercises should use dumbbell-specific progression rates
    And cable exercises should use cable-specific progression rates
    And barbell exercises should use barbell-specific progression rates
    And weight increments should be appropriate for each exercise type

  Scenario: Asynchronous feedback aggregation
    Given I have chest exercises on Monday and Thursday
    And I complete Monday's workout with feedback:
      | pump | soreness | workload |
      | 2    | 1        | 2        |
    When Thursday's workout is being programmed
    Then Monday's feedback should influence Thursday's programming
    But Thursday's workout should still be programmable before Thursday's feedback
    And the progression state should track partial week data

  Scenario: Handle incomplete feedback gracefully
    Given I have completed some exercises but not provided all muscle group feedback
    When progression calculations are triggered
    Then the system should use available feedback data
    And missing feedback should not prevent calculations for other muscle groups
    And partial data should be weighted appropriately in calculations
    And the system should prompt for missing feedback when possible

  Scenario: Progression state persistence and recovery
    Given I have progression entities with accumulated algorithm state
    When the system restarts or encounters an error
    Then all progression state should be persisted in the database
    And calculations should resume from the last known state
    And no feedback data should be lost
    And running calculations should continue accurately

  Scenario: Muscle group healing pattern differences
    Given I have feedback for both fast-healing and slow-healing muscle groups:
      | muscleGroupId | name      | healingRate | feedback |
      | 9             | Calves    | 0.8         | high     |
      | 10            | Hamstrings| 1.2         | high     |
    When progression calculations are performed
    Then calves should require more recovery time between sessions
    And hamstrings should recover faster and allow more frequent increases
    And the algorithm should account for muscle-specific healing patterns
    And set recommendations should reflect recovery capacity differences

  Scenario: Threshold-based programming decisions
    Given I have a muscle group with accumulated feedback scores:
      | week | runningScore | threshold | action       |
      | 1    | 0.5          | 1.0       | maintain     |
      | 2    | 0.8          | 1.0       | maintain     |
      | 3    | 1.2          | 1.0       | increase     |
    When weekly programming decisions are made
    Then weeks 1-2 should maintain current set recommendations
    And week 3 should trigger a set increase
    And the threshold should be dynamically adjusted based on progress
    And the algorithm should prevent excessive increases
