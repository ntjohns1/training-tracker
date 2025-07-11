# Training Tracker Models

This document provides an overview of all models in the Training Tracker application, organized by domain package.

## Day Domain (`model.day`)
1. **Day** - Represents a training day with exercises and muscle groups
2. **DayExercise** - Represents an exercise performed on a specific day
3. **DayMuscleGroup** - Represents muscle groups targeted on a specific day
4. **DayNote** - Contains notes related to a specific training day
5. **ExerciseSet** - Represents a set of an exercise with reps, weight, etc.

## Exercise Domain (`model.exercise`)
1. **Exercise** - Represents an exercise definition
2. **ExerciseNote** - Contains notes related to specific exercises

## Mesocycle Domain (`model.mesocycle`)
1. **CurrentMeso** - Represents the current active mesocycle
2. **MesoNote** - Contains notes related to a mesocycle
3. **MesoTemplate** - Template for creating new mesocycles
4. **Mesocycle** - Represents a complete mesocycle training block

## Muscle Group Domain (`model.muscle_group`)
1. **MuscleGroup** - Represents a muscle group definition
2. **Progression** - Represents progression tracking for muscle groups
