### When we create a mesocycle we create a Progression, not there is an ID for this
```json

        {
        "5": {
            "id": 6547583,
            "muscleGroupId": 5,
            "mgProgressionType": "regular"
        }   
```
### PUT request from finishing day, the exercise object has the joint pain 
```json

"exercises": [
      {
        "id": 126278734,
        "dayId": 20574310,
        "exerciseId": 243,
        "position": 0,
        "jointPain": 0,
        "createdAt": "2025-07-10T20:40:03.006Z",
        "updatedAt": "2025-08-04T16:24:37.599Z",
        "sourceDayExerciseId": 126278704,
        "muscleGroupId": 5,
        "sets": [
```


### also from PUT request from finishing day, we have the other metrics here.
```json
{
  "muscleGroups": [
    {
      "id": 92826761,
      "dayId": 20574310,
      "muscleGroupId": 5,
      "pump": 2,
      "soreness": 1,
      "workload": 1,
      "createdAt": "2025-07-10T20:40:03.006Z",
      "updatedAt": "2025-08-04T16:54:30.862Z",
      "recommendedSets": 6,
      "status": "complete"
    },
    {
      "id": 92826762,
      "dayId": 20574310,
      "muscleGroupId": 2,
      "pump": 2,
      "soreness": 0,
      "workload": 2,
      "createdAt": "2025-07-10T20:40:03.006Z",
      "updatedAt": "2025-08-04T17:10:25.445Z",
      "recommendedSets": 5,
      "status": "complete"
    },
    {
      "id": 92826763,
      "dayId": 20574310,
      "muscleGroupId": 4,
      "pump": 1,
      "soreness": 0,
      "workload": 2,
      "createdAt": "2025-07-10T20:40:03.006Z",
      "updatedAt": "2025-08-04T17:20:14.308Z",
      "recommendedSets": 3,
      "status": "complete"
    },
    {
      "id": 92826764,
      "dayId": 20574310,
      "muscleGroupId": 3,
      "pump": 2,
      "soreness": 1,
      "workload": 2,
      "createdAt": "2025-07-10T20:40:03.006Z",
      "updatedAt": "2025-08-04T17:20:00.485Z",
      "recommendedSets": 3,
      "status": "complete"
    }
  ]
}
```