Feature: Day Service
  As a user of the training tracker system
  I want to manage days through a clean service layer
  So that I can create, read, update, and delete days with proper mesocycle relationships

  Background:
    Given the DayService is configured with dependencies
    And the service uses DayFactory for entity creation
    And the service uses DayRepo for data persistence
    And the service uses DayMapper for DTO conversions

  Scenario: Create a new day successfully
    Given I have a valid DayRequest DTO with mesocycle ID
    And the mesocycle exists in the system
    When I call createDay with the DTO
    Then the service should use DayFactory to create the entity
    And the factory should establish the mesocycle relationship
    And the entity should be saved to the repository
    And a DayResponse DTO should be returned with mesocycle data

  Scenario: Handle mesocycle not found during day creation
    Given I have a DayRequest DTO with non-existent mesocycle ID
    When I call createDay with the DTO
    Then the factory should throw a RuntimeException
    And the exception message should indicate mesocycle not found
    And no day entity should be created or saved

  Scenario: Retrieve an existing day by ID
    Given a day exists in the system with ID 1
    When I call getDay with ID 1
    Then the service should retrieve the entity from the repository
    And the entity should be converted to DayResponse DTO
    And the response should contain all day data
    And the response should be returned successfully

  Scenario: Handle day not found during retrieval
    Given no day exists with ID 999
    When I call getDay with ID 999
    Then a RuntimeException should be thrown
    And the exception message should indicate "Day not found with id: 999"
    And no DTO should be returned

  Scenario: Update an existing day
    Given a day exists in the system with ID 1
    And I have updated day data in a DayRequest DTO
    When I call updateDay with ID 1 and the updated DTO
    Then the service should find the existing entity
    And the entity should be updated using the mapper's merge functionality
    And the updatedAt timestamp should be set to current time
    And the updated entity should be saved to the repository
    And a DayResponse DTO should be returned with updated data

  Scenario: Handle day not found during update
    Given no day exists with ID 999
    And I have day data in a DayResponse DTO
    When I call updateDay with ID 999 and the DTO
    Then a RuntimeException should be thrown
    And the exception message should indicate "Day not found with id: 999"
    And no update should be performed


  Scenario: Handle CompleteDayRequest during update
    Given a day exists in the system with ID 1
    And I have updated day data in a CompleteDayRequest DTO
    When I call updateDay with ID 1 and the updated DTO
    Then the service should find the existing entity
    And the entity should be updated using the mapper's merge functionality
    And the entity should be marked as completed
    And the updatedAt timestamp should be set to current time
    And the updated entity should be saved to the repository
    And a DayResponse DTO should be returned with updated data

  Scenario: Delete a day
    Given a day exists in the system with ID 1
    When I call deleteDay with ID 1
    Then the service should verify the entity exists
    And the entity should be deleted from the repository
    And the mesocycle relationship should be removed
    And the corresponding DayMuscleGroup entities should be deleted
    And the corresponding DayExercise entities should be deleted
    And the corresponding ExerciseSet entities should be deleted
    And the corresponding DayNote entities should be deleted
    And no response should be returned

  Scenario: Handle day not found during deletion
    Given no day exists with ID 999
    When I call deleteDay with ID 999
    Then a RuntimeException should be thrown
    And the exception message should indicate "Day not found with id: 999"
    And no deletion should be performed

  Scenario: Retrieve days by mesocycle ID
    Given multiple days exist for mesocycle ID 1
    When I call getDaysByMesocycleId with mesocycle ID 1
    Then the service should retrieve entities for that mesocycle from the repository
    And each entity should be converted to DayResponse DTO
    And a list of DayResponse DTOs should be returned
    And only days belonging to the specified mesocycle should be included

  Scenario: Handle input validation
    Given I call any service method with null parameters
    When the service processes the request
    Then appropriate IllegalArgumentException should be thrown
    And descriptive error messages should be provided
    And no database operations should be performed