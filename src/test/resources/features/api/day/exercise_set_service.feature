Feature: Exercise Set Service
    As a user of the training tracker system
    I want to manage exercise sets through a clean service layer
    So that I can create, read, update, and delete exercise sets with proper relationships

    Background:
      Given the ExerciseSetService will be configured with simplified architecture
      And the service will use ExerciseSetRepo for data persistence
      And the service will use ExerciseSetMapper for DTO conversions
      And no redundant orchestration or CRUD service layers will exist


    Scenario: Marking first set complete should require soreness parameter
    Scenario: Marking last set complete should update day exercise status to pendingFeedback
    