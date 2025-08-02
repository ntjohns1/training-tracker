Feature: Day Factory
  As a developer working with the training tracker system
  I want a factory class that creates Day entities consistently
  So that I can eliminate redundant instantiation patterns and handle mesocycle relationships properly

  Background:
    Given the DayFactory is properly configured with dependencies
    And the system has access to DayMapper for DTO conversions
    And the system has access to MesocycleRepo for relationship handling

  Scenario: Create a new day successfully with relationships
    Given I have a valid DayRequest DTO with mesocycle ID
    