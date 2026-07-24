import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { api } from './client';
import type { Bootstrap, CurrentMeso, Day, ExerciseSet } from './types';

export interface UpdateSetBody {
  weight?: number | null;
  reps?: number | null;
  finishedAt?: string | null;
  status?: string | null;
}

export const qk = {
  bootstrap: ['bootstrap'] as const,
  mesocycle: (id: number) => ['mesocycle', id] as const,
  day: (id: number) => ['day', id] as const,
};

export interface CreateMesoBody {
  name: string;
  weeks: number;
  unit: string;
  days: { label: string; exercises: { exerciseId: number }[] }[];
  progressions: Record<string, { mgProgressionType: string; muscleGroupId: number }>;
}

/** Aggregate startup payload: the user's mesocycles + exercise catalog + muscle groups. */
export function useBootstrap() {
  return useQuery({
    queryKey: qk.bootstrap,
    queryFn: async () => (await api.get<Bootstrap>('/bootstrap')).data,
  });
}

export function useMesocycle(id: number | undefined) {
  return useQuery({
    queryKey: qk.mesocycle(id ?? -1),
    enabled: id != null,
    queryFn: async () => (await api.get<CurrentMeso>(`/mesocycles/${id}`)).data,
  });
}

export function useDay(id: number | undefined) {
  return useQuery({
    queryKey: qk.day(id ?? -1),
    enabled: id != null,
    queryFn: async () => (await api.get<Day>(`/days/${id}`)).data,
  });
}

/** POST /api/mesocycles — create from scratch. Returns the created meso. */
export function useCreateMeso() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (body: CreateMesoBody) =>
      (await api.post<{ id: number }>('/mesocycles', body)).data,
    onSuccess: () => qc.invalidateQueries({ queryKey: qk.bootstrap }),
  });
}

/** PATCH /api/mesocycles/{id} — rename (and unit/status). */
export function useUpdateMeso() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({
      id,
      body,
    }: {
      id: number;
      body: { id: number; name?: string; unit?: string; status?: string };
    }) => (await api.patch(`/mesocycles/${id}`, body)).data,
    onSuccess: (_d, { id }) => {
      qc.invalidateQueries({ queryKey: qk.bootstrap });
      qc.invalidateQueries({ queryKey: qk.mesocycle(id) });
    },
  });
}

/** Body for PUT /api/days/{id}/finish — mirrors the backend FinishDayRequest. */
export interface FinishSet {
  id: number;
  dayExerciseId: number | null;
  position: number | null;
  setType: string | null;
  weight: number | null;
  reps: number | null;
  unit: string | null;
  finishedAt: string | null;
  status: string | null;
}
export interface FinishExercise {
  id: number;
  dayId: number | null;
  exerciseId: number | null;
  position: number | null;
  jointPain: number | null;
  muscleGroupId: number | null;
  sets: FinishSet[];
  status: string | null;
}
export interface FinishMuscleGroup {
  id: number;
  dayId: number | null;
  muscleGroupId: number | null;
  pump: number | null;
  soreness: number | null;
  workload: number | null;
  status: string | null;
}
export interface FinishDayBody {
  id: number;
  mesoId: number | null;
  week: number | null;
  position: number | null;
  unit: string | null;
  finishedAt: string | null;
  label: string | null;
  exercises: FinishExercise[];
  muscleGroups: FinishMuscleGroup[];
  status: string | null;
}

/** PUT /api/days/{id}/finish — persists feedback, stamps the day, triggers progression. */
export function useFinishDay() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, body }: { id: number; body: FinishDayBody }) =>
      (await api.put<Day>(`/days/${id}/finish`, body)).data,
    onSuccess: (_data, { id }) => {
      qc.invalidateQueries({ queryKey: qk.day(id) });
      qc.invalidateQueries({ queryKey: ['mesocycle'] });
      qc.invalidateQueries({ queryKey: qk.bootstrap });
    },
  });
}

/** PATCH /api/sets/{id}. Refreshes the parent day so logged badges/values stay in sync. */
export function useUpdateSet(dayId: number | undefined) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, body }: { id: number; body: UpdateSetBody }) =>
      (await api.patch<ExerciseSet>(`/sets/${id}`, body)).data,
    onSuccess: () => {
      if (dayId != null) qc.invalidateQueries({ queryKey: qk.day(dayId) });
    },
  });
}
