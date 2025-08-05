Progression Algorithm High Level Process Flow:

Background:
Each week of a Mesocycle progresses with RIR -= the field, Mesocycle.microRirs is an integer series where each integer represents the RIR for one week of the mesocycle:
3210 for a 5 week Mesocycle would mean week 1: 3 RIR; week 2: 2 RIR; week 3: 1 RIR, week 4: 0 RIR; week 5: Deload
note that final week is always Deload

1. user completes a workout and gives feedback
2. Day status is set to "complete"
3. workout:complete event triggers CalculateProgression (initially, use the CompleteDayRequest for non-event-driven impl)
4. Calculate Progression:
   - Day nextDay = look up the day in the corresponding position for the following week (nextDay.week == thisDay.week + 1 && nextDay.position == thisDay.position)
   - For each DayMuscleGroup in nextDay:
     - update recommendedSets using pump, soreness, workload, and joint pain.
       - there will be some threshold number that we use to determine whether to add one set, keep it the same, or subtract one set by adding the feedback values and comparing them to said threshold
       - the threshold and starting number of sets will differ depending on DayMuscleGroup.MuscleGroup (calves and biceps heal more quickly than hamstrings, for example)
       - I need to work out the exact implementation details for each muscle group, and test scenarios for each one
   - For Each DayExercise (sorted by DayMuscleGroup):
     - Create ExerciseSets for each DayExercise using recommendedSets
     - create new ExerciseSets:
       - we take previous week's DayExercise.ExerciseSet.getWeight() and .getReps();
       - using the values for weight and reps we set weightTarget, weightTargetMin, weightTargetMax, and repsTarget, 
       - if exercise is bodyWeight, or repsTarget is in the higher range (11 - 15 repetitions) default to progress by adding one rep
       - if repsTarget is in the lower range, the default is to progress by weight, but the user can progress by reps alternatively:
         - weight target will be += .5 lb if < 21 lbs;
         - weight target will be += 1 lb if < 71 lbs;
         - weight target will be += 2.5 lb if < 141 lbs;
         - weight target will be += 5 lb if >= 141 lbs;
         - these are estimates, exact values can be determined as needed.
       - frontend will handle converting the repsTarget if the user changes weight
       - frontend will handle converting the weightTarget if the user changes reps
       - we will use something like the Epley Formula to keep the targetReps within a certain range (if user starts with 6 reps, recommend 5 to 12; starts with 11 recommend 9 - 15) need to find out the exact figures 


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
