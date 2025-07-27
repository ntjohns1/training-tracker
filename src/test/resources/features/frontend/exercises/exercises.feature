Feature: Exercises
    Scenario: Get All Exercises
       Given user is logged in
       When user gets all exercises
       Then exercises are returned
    Scenario: Get Exercise
       Given user is logged in
       When user gets exercise
       Then exercise is returned
