Feature: Progression Service
  As a user of the training tracker system
  So that I can create, read, update, and delete progressions with proper business logic

  Scenario: Create a new progression successfully
    Given I have a valid ProgressionRequest DTO with progression data
    When I call createProgression with the DTO
    Then the service should create the entity
    And proper relationships should be set on the entity (Mesocycle, MuscleGroup)
    And the entity should be saved to the repository
    And a ProgressionResponse DTO should be returned
    And the response should contain the created progression data
    And proper timestamps should be set on the entity

  Scenario: Retrieve an existing progression by ID
    Given a progression exists in the system with ID 1
    When I call getProgression with ID 1
    Then the service should retrieve the entity from the repository
    And the entity should be converted to ProgressionResponse DTO
    And the response should contain all progression data
    And the response should be returned successfully

  Scenario: Handle progression not found during retrieval
    Given no progression exists with ID 999
    When I call getProgression with ID 999
    Then a RuntimeException should be thrown
    And the exception message should indicate "Progression not found with id: 999"
    And no DTO should be returned


  Scenario: Update an existing progression
    Given a progression exists in the system with ID 1
    And I have updated progression data in a ProgressionResponse DTO
    When I call updateProgression with ID 1 and the updated DTO
    Then the service should find the existing entity
    And the entity should be updated using the mapper's merge functionality
    And the updated entity should be saved to the repository
    And a ProgressionResponse DTO should be returned with updated data


  Scenario: Handle progression not found during update
    Given no progression exists with ID 999
    And I have progression data in a ProgressionResponse DTO
    When I call updateProgression with ID 999 and the DTO
    Then a RuntimeException should be thrown
    And the exception message should indicate "Progression not found with id: 999"
    And no update should be performed

  Scenario: Soft delete a progression
    Given a progression exists in the system with ID 1
    When I call deleteProgression with ID 1
    Then the service should find the existing entity
    And the service should create a soft-deleted version
    And the soft-deleted entity should be saved to the repository
    And the deletedAt timestamp should be set

  Scenario: Handle progression not found during soft delete
    Given no progression exists with ID 999
    When I call deleteProgression with ID 999
    Then a RuntimeException should be thrown
    And the exception message should indicate "Progression not found with id: 999"
    And no delete operation should be performed

  Scenario: Finish a progression
    Given a progression exists in the system with ID 1 that is not finished
    When I call finishProgression with ID 1
    Then the service should find the existing entity
    And the service should create a finished version
    And the finished entity should be saved to the repository
    And the finishedAt timestamp should be set
    And a ProgressionResponse DTO should be returned
