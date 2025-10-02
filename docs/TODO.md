[ ] Verify validation logic for all entities  
[ ] Verify JPA logic for all entities  
[ ] Verify mapping logic for all entities  
[ ] Verify recent changes to security context  

---

[ ] Progression: design and implement algorithm for progression   
[ ] look for *Response classes passed as parameters that should be *Request   
[ ] unit tests for all new service methods   
    [ ] DayMuscleGroupService   
    [ ] DayExerciseService   
    [ ] ExerciseSetService   
    [ ] DayService   


Concrete Implementation Plan   
Create WorkoutProgressionService:   
[] Move programNextDay() from DayService   
[] Move updateRecommendedSetsForNext() from DayMuscleGroupService   
[] Centralize the orchestration logic   
[] Extract ProgressionCalculator:   
[] Move calculation logic from DayMuscleGroup entity   
[] Create a stateless calculator component   
[] Refactor Service Interfaces:   
    [] Reduce cross-service dependencies   
    [] Make each service responsible for its own domain only  
[] Implement Domain Events for key state transitions to decouple the services
    (day completion, progression updates)


