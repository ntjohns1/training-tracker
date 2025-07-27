Feature: Mesocycle Service
  As a user of the training tracker system
  I want to manage mesocycles through a clean service layer
  So that I can create, read, update, and delete mesocycles with proper business logic

  Background:
    Given the MesocycleService is configured with simplified architecture
    And the service uses MesocycleFactory for entity creation
    And the service uses MesocycleRepo for data persistence
    And the service uses MesocycleMapper for DTO conversions
    And no redundant orchestration or CRUD service layers exist

  Scenario: Create a new mesocycle successfully
    Given I have a valid MesocycleResponse DTO with mesocycle data
    When I call createMesocycle with the DTO
    Then the service should use MesocycleFactory to create the entity
    And the entity should be saved to the repository
    And a MesocycleResponse DTO should be returned
    And the response should contain the created mesocycle data
    And proper timestamps should be set on the entity

  Scenario: Retrieve an existing mesocycle by ID
    Given a mesocycle exists in the system with ID 1
    When I call getMesocycle with ID 1
    Then the service should retrieve the entity from the repository
    And the entity should be converted to MesocycleResponse DTO
    And the response should contain all mesocycle data
    And the response should be returned successfully

  Scenario: Handle mesocycle not found during retrieval
    Given no mesocycle exists with ID 999
    When I call getMesocycle with ID 999
    Then a RuntimeException should be thrown
    And the exception message should indicate "Mesocycle not found with id: 999"
    And no DTO should be returned

  Scenario: Update an existing mesocycle
    Given a mesocycle exists in the system with ID 1
    And I have updated mesocycle data in a MesocycleResponse DTO
    When I call updateMesocycle with ID 1 and the updated DTO
    Then the service should find the existing entity
    And the entity should be updated using the mapper's merge functionality
    And the updated entity should be saved to the repository
    And a MesocycleResponse DTO should be returned with updated data

  Scenario: Handle mesocycle not found during update
    Given no mesocycle exists with ID 999
    And I have mesocycle data in a MesocycleResponse DTO
    When I call updateMesocycle with ID 999 and the DTO
    Then a RuntimeException should be thrown
    And the exception message should indicate "Mesocycle not found with id: 999"
    And no update should be performed

  Scenario: Soft delete a mesocycle
    Given a mesocycle exists in the system with ID 1
    When I call deleteMesocycle with ID 1
    Then the service should find the existing entity
    And the service should use MesocycleFactory to create a soft-deleted version
    And the soft-deleted entity should be saved to the repository
    And the deletedAt timestamp should be set

  Scenario: Handle mesocycle not found during deletion
    Given no mesocycle exists with ID 999
    When I call deleteMesocycle with ID 999
    Then a RuntimeException should be thrown
    And the exception message should indicate "Mesocycle not found with id: 999"
    And no deletion should be performed

  Scenario: Retrieve all mesocycles
    Given multiple mesocycles exist in the system
    When I call getAllMesocycles
    Then the service should retrieve all entities from the repository
    And each entity should be converted to MesocycleResponse DTO
    And a list of MesocycleResponse DTOs should be returned

  Scenario: Retrieve mesocycles by user ID
    Given multiple mesocycles exist for user ID 100
    When I call getMesocyclesByUserId with user ID 100
    Then the service should retrieve entities for that user from the repository
    And each entity should be converted to MesocycleResponse DTO
    And a list of MesocycleResponse DTOs should be returned

  Scenario: Retrieve only active mesocycles
    Given mesocycles exist with some soft-deleted
    When I call getAllActiveMesocycles
    Then the service should retrieve only non-deleted entities
    And each entity should be converted to MesocycleResponse DTO
    And only active mesocycles should be returned

  Scenario: Retrieve active mesocycles by user ID
    Given mesocycles exist for user ID 100 with some soft-deleted
    When I call getActiveMesocyclesByUserId with user ID 100
    Then the service should retrieve only non-deleted entities for that user
    And each entity should be converted to MesocycleResponse DTO
    And only active mesocycles for the user should be returned

  Scenario: Finish a mesocycle
    Given a mesocycle exists in the system with ID 1 that is not finished
    When I call finishMesocycle with ID 1
    Then the service should find the existing entity
    And the service should use MesocycleFactory to create a finished version
    And the finished entity should be saved to the repository
    And the finishedAt timestamp should be set
    And a MesocycleResponse DTO should be returned

  Scenario: Handle mesocycle not found during finish
    Given no mesocycle exists with ID 999
    When I call finishMesocycle with ID 999
    Then a RuntimeException should be thrown
    And the exception message should indicate "Mesocycle not found with id: 999"
    And no finish operation should be performed

  Scenario: Service layer architecture validation
    Given the MesocycleService implementation
    Then it should use only the simplified Service + Factory pattern
    And it should NOT have orchestration service dependencies
    And it should NOT have CRUD service dependencies
    And it should directly use MesocycleFactory for entity creation
    And it should directly use MesocycleRepo for data access
    And it should directly use MesocycleMapper for DTO conversions
    And the architecture should be clean and maintainable

  Scenario: Eliminate redundant instantiation in service layer
    Given any mesocycle operation is performed
    When the service processes the request
    Then no mapper.toEntity() followed by builder reconstruction should occur
    And the factory should handle all entity creation logic
    And the service should focus on business logic and orchestration
    And the code should be more maintainable and performant