Progression Algorithm High Level Process Flow:

Background:
Each week of a Mesocycle progresses with RIR -= the field, Mesocycle.microRirs is an integer series where each integer represents the RIR for one week of the mesocycle:
32108 for a 5 week Mesocycle would mean week 1: 3 RIR; week 2: 2 RIR; week 3: 1 RIR, week 4: 0 RIR; week 5: 8 RIR (Deload)
note that final week is always Deload

## Shower Thoughts:
- To generate recommendedSets, look back at the previous week of the mesocycle and use feedback to calculate recommended sets - this is how we incorporate the exercise-specific feedback (joint pain)
- Looking up next DayMuscleGroup is a separate concern from calculating recommended sets, we still need to get the next DayMuscleGroup to change the status to "Programmed"
- Should we have a method in DayMuscleGroup: calculateRecommendedSets?

## Note:
- New Method in Day Exercise Repo: findAllByMuscleGroupId - returns List<DayExercise> we can also use length to divide recommended sets

## Setup:
1. User creates Mesocycle - Entire first week  is populated with default number of sets per muscle group (need field for default # of sets?), recommended sets is -1.
2. User completes day - PUT Request updates Day to PendingConfirmation
3. event is triggered: action:workoutComplete
    - get next DayMuscleGroup: findNextWithSameMuscleGroup
    - call ProgressionService to calculate recommended sets
         - get previous week's feedback for corresponding DayMuscleGroup
         - use our new method in DayMuscleGroupRepo: findNextWithSameMuscleGroup to get soreness for NEXT workout, other metrics are from the day
         - call DayMuscleGroupService to update recommended sets taking in the feedback
    - call ProgressionService to calculate targets
    - find Day exercises by DayMuscleGroup
    - call ExerciseSetService to create the ExerciseSets
      - divide recommended sets by count of DayExercises (first exercises in the order get the larger number when rounding)
    - set status for all entities
    - set timestamp for all entities
    - 
## Business logic:
1. Triggering event provides DayId for PendingConfirmation day and meso key
2. For Each DayMuscleGroup in completedDay:
3. Look up Day by DayMuscleGroup.muscleGroup.id
4. Call Progression service to sum Joint Pain, Pump, Soreness (if applicable), and Workload - Method: CalculateRecommendedSets takes in feedback Sum and returns int for recommended sets
   - 
5. PUT - recommended sets for DayMuscleGroup on target day
6. for each updatedDayMuscleGroup:
7. Look up DayExercises by DayMuscleGroup - DayExerciseRepo: List<DayExercise> getDayExercisesByDayMuscleGroupId
8. Divide recommended sets by count of DayExercises (first exercises in the order get the larger number when rounding)
9. Call ProgressionService to calculate targets - separate algorithm, Method: float[] CalculateWeightTargets(float weight, int reps) 
10. Call ExerciseSetService to create the ExerciseSets (using calculated targets)
11. set update status for all entities

Example 1:
Week 1 : user does 5 pullups - (calculate as 3 RIR, recommend 6 reps next)
Week 2 : user does 6 pullups - (calculate as 2 RIR, recommend 7 reps next)
Week 3 : user does 7 pullups - (calculate as 1 RIR, recommend 8 reps next)
Week 4 : user does 8 pullups - (calculate as 0 RIR, recommend "8 RIR" for deload)
Week 5 : deload, give a default value (-1?) frontend will suggest 8 RIR for a deload. 
Future enhancement to allow for special cases where the user changes from bodyweight to assisted bodyweight.


Example 2:
Week 1 : user does 6 reps of Squats at 200 lbs - calculate as 3 RIR, recommend 6 reps @ 205, weightTargetMax= 210 (5 reps), weightTargetMin = 175 (12 reps)
Week 2 : user does 6 reps of Squats at 205 lbs - calculate as 2 RIR, recommend 6 reps @ 210, weightTargetMax= 215 (5 reps), weightTargetMin = 180 (12 reps)
Week 3 : user does 6 reps of Squats at 210 lbs - calculate as 1 RIR, recommend 6 reps @ 215, weightTargetMax= 215 (5 reps), weightTargetMin = 180 (12 reps)
Week 4 : user does 6 reps of Squats at 215lbs - (calculate as 0 RIR, recommend 6 reps @ 190, min = 170.75 (10 reps), max = 195 (5 reps))
Week 5 : user does 6 reps of Squats at 190 (8 RIR)  
                

Example 3:                                                                                                                                                                   
Week 1 : user does 11 reps of Incline Dumbbell Curls at 20 lbs - calculate as 3 RIR, recommend 12 reps @ 20, weightTargetMax= 22.5 (7 reps), weightTargetMin = 17.5 (17 reps)                  
Week 2 : user does 12 reps of Incline Dumbbell Curls at 20 lbs - calculate as 2 RIR, recommend 13 reps @ 20, weightTargetMax= 22.5 (7 reps), weightTargetMin = 17.5 (17 reps)                  
Week 3 : user does 13 reps of Incline Dumbbell Curls at 20 lbs - calculate as 1 RIR, recommend 14 reps @ 20, weightTargetMax= 22.5 (7 reps), weightTargetMin = 17.5 (17 reps)                  
Week 4 : user does 14 reps of Incline Dumbbell Curls at 20 lbs - (calculate as 0 RIR, recommend 8 RIR - 8.75 lbs X 12 reps to 10.5 lbs x 5 reps)                                     
Week 5 : user does 7 reps of Incline Dumbbell Curls at 10lbs (8 RIR)    
*note: dumbbell weight recommendations are more restricted to 2.5 or 5 lb weight increments, especially > 25lbs (only fancy gyms have 27.5 lbs+)
*deload recommendations have their own rules, first half of week is different from second half of week in terms of volume
