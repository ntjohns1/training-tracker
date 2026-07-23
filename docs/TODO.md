[ ] Verify validation logic for all entities  
[ ] Verify JPA logic for all entities  
[ ] Refactor mapping logic for all entities, remove mappers  
[ ] Verify recent changes to security context  

---

[ ] Progression: design and implement algorithm for progression   
[ ] look for *Response classes passed as parameters that should be *Request   
    [x] DayMuscleGroupService.createDayMuscleGroup()  
    [ ] DayExerciseService.createDayExercise()  
    [ ] ExerciseSetService.createExerciseSet()  
    [ ] ExerciseSetService.updateExerciseSet()
    [x] DayFactory.createFromResponse()

[ ] unit tests for all new service methods   
    [ ] DayMuscleGroupService   
    [ ] DayExerciseService   
    [ ] ExerciseSetService   
    [ ] DayService   
    [x] MesocycleProgressionService (3 behavior-defining tests @Disabled pending implementation)  
    [x] ProgressionCalculator  

Concrete Implementation Plan   
[] Create WorkoutProgressionService:   
    [x] Move programNextDay() from DayService
        [] complete logic in programNextDay()  
    [x] Move updateRecommendedSetsForNext() from DayMuscleGroupService   
[x] Centralize the orchestration logic   
    [x] Extract ProgressionCalculator:   
        [x] Move calculation logic from DayMuscleGroup entity   
        [x] Create a stateless calculator component
    [] Refactor Service Interfaces:   
        [] Reduce cross-service dependencies   
        [] Make each service responsible for its own domain only  
    [] Implement Domain Events for key state transitions to decouple the services
        (day completion, progression updates)

[] Refactor relevant service methods to validate mesocycle ownership at repository level / security layer
    [ ] Look for areas where we are calling MesocycleService directly for validation (fix these!)  
    [ ] filter in the repository where possible, use custom annotations otherwise

[] Remove all Mapper classes
[] DayExerciseService.createDayExercise() - need to handle case where DayMuscleGroup has not yet been created for this day



