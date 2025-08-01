Feature: Progression Service
  As a user of the training tracker system
  So that I can create, read, update, and delete progressions with proper business logic


    Scenario: Create a new progression successfully with relationships\
    Given I have a valid ProgressionRequest DTO with muscle group ID and mesocycle ID\
    And the muscle group entity exists in the system\
    And the mesocycle entity exists in the system\
    When I call createProgression with the DTO\
    Then the service should use ProgressionFactory to create the entity\
    And the service should establish DayMuscleGroup relationships\
    And the service should establish MuscleGroup relationships\
    And the service should establish Mesocycle relationships\
    And the entity should be saved to the repository\
    And a ProgressionResponse DTO should be returned with created data
    
      