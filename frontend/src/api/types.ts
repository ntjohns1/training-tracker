// Types mirror the backend response/request DTOs. Instants are ISO strings over the wire.

export type Iso = string;

export interface MesoNote {
  id: number;
  mesoId: number;
  noteId: number | null;
  createdAt: Iso | null;
  updatedAt: Iso | null;
  text: string;
}

/** Summary shape returned in the bootstrap list (weeks/days are counts, not structures). */
export interface Mesocycle {
  id: number;
  key: string | null;
  userId: number | null;
  name: string;
  days: number | null;
  unit: string;
  sourceTemplateId: number | null;
  sourceMesoId: number | null;
  microRirs: number | null;
  createdAt: Iso | null;
  updatedAt: Iso | null;
  finishedAt: Iso | null;
  deletedAt: Iso | null;
  weeks: number | null;
  notes: MesoNote[] | null;
}

export interface ExerciseSet {
  id: number;
  dayExerciseId: number | null;
  position: number | null;
  setType: string | null;
  weight: number | null;
  weightTarget: number | null;
  weightTargetMin: number | null;
  weightTargetMax: number | null;
  reps: number | null;
  repsTarget: number | null;
  bodyweight: number | null;
  unit: string | null;
  createdAt: Iso | null;
  finishedAt: Iso | null;
  status: string | null;
}

export interface DayExercise {
  id: number;
  dayId: number | null;
  exerciseId: number | null;
  position: number | null;
  jointPain: number | null;
  createdAt: Iso | null;
  updatedAt: Iso | null;
  sourceDayExerciseId: number | null;
  muscleGroupId: number | null;
  sets: ExerciseSet[];
  status: string | null;
}

export interface DayMuscleGroup {
  id: number;
  dayId: number | null;
  muscleGroupId: number | null;
  pump: number | null;
  soreness: number | null;
  workload: number | null;
  createdAt: Iso | null;
  updatedAt: Iso | null;
  recommendedSets: number | null;
  status: string | null;
}

export interface DayNote {
  id: number;
  dayId: number | null;
  noteId: number | null;
  pinned: boolean | null;
  createdAt: Iso | null;
  updatedAt: Iso | null;
  text: string;
}

export interface Day {
  id: number;
  mesoId: number | null;
  week: number | null;
  position: number | null;
  createdAt: Iso | null;
  updatedAt: Iso | null;
  bodyweight: number | null;
  bodyweightAt: Iso | null;
  unit: string | null;
  finishedAt: Iso | null;
  label: string | null;
  notes: DayNote[];
  exercises: DayExercise[];
  muscleGroups: DayMuscleGroup[];
  status: string | null;
}

export interface Week {
  days: Day[];
}

/** Full detail returned by GET /api/mesocycles/{id}. */
export interface CurrentMeso extends Omit<Mesocycle, 'weeks' | 'notes'> {
  weeks: Week[];
  notes: MesoNote[];
  status: string | null;
  generatedFrom: string | null;
}

export interface Exercise {
  id: number;
  name: string;
  muscleGroupId: number | null;
  youtubeId: string | null;
  exerciseType: string | null;
  userId: number | null;
  createdAt: Iso | null;
  updatedAt: Iso | null;
  deletedAt: Iso | null;
  mgSubType: string | null;
  notes: unknown[] | null;
}

export interface MuscleGroup {
  id: number;
  name: string;
  createdAt: Iso | null;
  updatedAt: Iso | null;
}

export interface Bootstrap {
  mesocycles: Mesocycle[];
  exercises: Exercise[];
  muscleGroups: MuscleGroup[];
}
