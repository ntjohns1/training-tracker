Feature: Day Service
  As a user of the training tracker system
  I want to manage days through a clean service layer
  So that I can create, read, update, and delete days with proper mesocycle relationships

  Background:
    Given the DayService is configured with simplified architecture
    And the service uses DayFactory for entity creation
    And the service uses DayRepo for data persistence
    And the service uses DayMapper for DTO conversions
    And no redundant orchestration or CRUD service layers exist

  Scenario: Create a new day successfully without mesocycle
    Given I have a valid DayResponse DTO without mesocycle ID
    When I call createDay with the DTO
    Then the service should use DayFactory to create the entity
    And the entity should be saved to the repository
    And a DayResponse DTO should be returned
    And the response should contain the created day data
    And proper timestamps should be set on the entity
    And the mesocycle relationship should be null

  Scenario: Create a new day successfully with mesocycle relationship
    Given I have a valid DayResponse DTO with mesocycle ID
    And the mesocycle exists in the system
    When I call createDay with the DTO
    Then the service should use DayFactory to create the entity
    And the factory should establish the mesocycle relationship
    And the entity should be saved to the repository
    And a DayResponse DTO should be returned with mesocycle data

  Scenario: Handle mesocycle not found during day creation
    Given I have a DayResponse DTO with non-existent mesocycle ID
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
    And I have updated day data in a DayResponse DTO
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

  Scenario: Delete a day
    Given a day exists in the system with ID 1
    When I call deleteDay with ID 1
    Then the service should verify the entity exists
    And the entity should be deleted from the repository
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

  Scenario: Service layer architecture validation
    Given the DayService implementation
    Then it should use only the simplified Service + Factory pattern
    And it should directly use DayFactory for entity creation
    And it should directly use DayRepo for data access
    And it should directly use DayMapper for DTO conversions
    And it should NOT have unnecessary orchestration service dependencies
    And the architecture should be clean and maintainable

  Scenario: Eliminate redundant instantiation in service layer
    Given any day operation with mesocycle relationship is performed
    When the service processes the request
    Then no mapper.toEntity() followed by builder reconstruction should occur
    And the factory should handle all entity creation and relationship logic
    And the service should focus on business logic and validation
    And the code should be more maintainable and performant

  Scenario: Handle input validation
    Given I call any service method with null parameters
    When the service processes the request
    Then appropriate IllegalArgumentException should be thrown
    And descriptive error messages should be provided
    And no database operations should be performed

  Scenario: Factory integration eliminates mesocycle repository dependency
    Given the DayService needs to create days with mesocycle relationships
    When any day creation operation is performed
    Then the service should NOT directly access MesocycleRepo
    And the DayFactory should handle mesocycle relationship establishment
    And the service should have cleaner dependencies
    And separation of concerns should be maintained