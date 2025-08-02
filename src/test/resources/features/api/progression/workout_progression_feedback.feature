Feature: Workout Progression and Feedback Workflow
  As a user performing workouts
  I want to provide feedback on sets, exercises, and days
  So that the system can adapt my future training recommendations

  Background:
    Given I have a mesocycle with the following structure:
      | dayId     | exerciseId | muscleGroupId | setCount |
      | 21195296  | 216        | 11           | 3        |
      | 21195296  | 199        | 9            | 2        |
    And all sets are initially in "pendingWeight" status
    And all exercises are initially in "ready" status
    And all muscle groups are initially in "programmed" status

  Scenario: Complete workout set progression
    Given I have a set with ID 168902714 in "pendingWeight" status
    When I update the set with weight and reps:
      """
      {
        "weight": 19,
        "reps": 11
      }
      """
    Then the set should be updated with the new values
    And the set status should remain "pendingWeight"

  Scenario: Finish first set of exercise with soreness feedback
    Given I have the first set of an exercise with ID 168902714
    When I finish the set with soreness feedback:
      """
      {
        "finishedAt": "2025-07-31T02:31:51.071Z",
        "reps": 11,
        "soreness": -1
      }
      """
    Then the set should be marked as "complete"
    And the set should have the finishedAt timestamp
    And the soreness feedback should be recorded
    And the muscle group should receive the soreness data

  Scenario: Finish subsequent sets without soreness feedback
    Given I have a non-first set of an exercise with ID 168902715
    When I finish the set without soreness feedback:
      """
      {
        "finishedAt": "2025-07-31T02:36:26.828Z",
        "reps": 11
      }
      """
    Then the set should be marked as "complete"
    And the set should have the finishedAt timestamp
    And no additional soreness feedback should be required

  Scenario: Complete exercise with all sets finished
    Given I have an exercise with all sets completed
    When I update the day with the completed exercise:
      """
      {
        "exercises": [
          {
            "id": 130072407,
            "status": "complete",
            "sets": [
              {"id": 168902714, "status": "complete"},
              {"id": 168902715, "status": "complete"},
              {"id": 168902716, "status": "complete"}
            ]
          }
        ]
      }
      """
    Then the exercise should be marked as "complete"
    And the associated muscle group should transition to "pendingFeedback"

  Scenario: Provide muscle group feedback during exercise completion
    Given I have completed an exercise for muscle group 11
    When I provide muscle group feedback:
      """
      {
        "muscleGroups": [
          {
            "id": 95635090,
            "muscleGroupId": 11,
            "pump": 1,
            "soreness": -1,
            "workload": 1,
            "status": "complete"
          }
        ]
      }
      """
    Then the muscle group feedback should be recorded
    And the muscle group should be marked as "complete"
    And the feedback should be available for progression calculations

  Scenario: Complete entire workout day
    Given I have completed all exercises and provided all muscle group feedback
    When I finish the day:
      """
      {
        "id": 21195296,
        "finishedAt": "2025-07-31T02:48:23.126Z",
        "status": "pendingConfirmation",
        "exercises": [
          {"id": 130072407, "status": "complete"},
          {"id": 130072408, "status": "complete"}
        ],
        "muscleGroups": [
          {
            "id": 95635090,
            "muscleGroupId": 11,
            "pump": 1,
            "soreness": -1,
            "workload": 1,
            "status": "complete"
          },
          {
            "id": 95635091,
            "muscleGroupId": 9,
            "pump": 1,
            "soreness": -1,
            "workload": 1,
            "status": "complete"
          }
        ]
      }
      """
    Then the day should be marked as "pendingConfirmation"
    And all feedback data should be aggregated for progression calculations
    And the system should trigger progression calculations for the next week

  Scenario: Asynchronous feedback affects future programming
    Given I completed chest exercises on Monday with feedback:
      | pump | soreness | workload |
      | 2    | 1        | 2        |
    And I have chest exercises scheduled for Thursday
    When Thursday's workout is programmed
    Then the Monday feedback should influence Thursday's set recommendations
    And the progression algorithm should adjust based on recovery indicators

  Scenario: Feedback aggregation triggers set count adjustments
    Given I have a running soreness score for calves muscle group
    And the cumulative feedback exceeds the threshold for set increases
    When the next week is programmed
    Then the recommended set count should be increased
    And the DayMuscleGroup should reflect the new set recommendations
    And the status should transition from "unprogrammed" to "programmed"

  Scenario: Muscle group specific progression algorithms
    Given I have feedback for both calves and hamstrings muscle groups
    When progression calculations are performed
    Then calves should use the calves-specific algorithm parameters
    And hamstrings should use the hamstrings-specific algorithm parameters
    And each muscle group should have different healing pattern considerations
    And the progression recommendations should reflect muscle-specific characteristics
