Feature: Day Service
  As a user of the training tracker system
  I want to manage days through a clean service layer
  So that I can create, read, update, and delete days with proper mesocycle relationships

  # Background:
  #   Given the DayService is configured with dependencies
  #   And the service uses DayFactory for entity creation
  #   And the service uses DayRepo for data persistence
  #   And the service uses DayMapper for DTO conversions




  Scenario: Handle CompleteDayRequest during update
    Given the day, day exercise, day muscle group, and exercise entities exist in the system
    And I have updated day data in a CompleteDayRequest DTO
    When I call updateDay with ID 1 and the updated DTO
    Then the service should find the existing entity
    And the entity should be updated using the mapper's merge functionality
    And the entity should be marked as complete
    And the updatedAt timestamp should be set to current time
    And the updated entity should be saved to the repository
    And the service should look up the next occurence of each day muscle group (thisDayMuscleGroup.muscleGroup.id == nextDayMuscleGroup.muscleGroup.id)
    And the service should update the recommended sets for that day exercise in the next week
    And the service should update the recommended reps for that day exercise in the next week
    And the service should update the recommended weight for that day exercise in the next week
    And the service should create ExerciseSet entities for each day exercise in the next day muscle group occurrence with recommended values from the previous week
    And the service should update the next day muscle group status to programmed
    And a DayResponse DTO should be returned with updated data