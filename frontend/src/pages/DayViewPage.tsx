import {
  Badge,
  Button,
  Card,
  Checkbox,
  Group,
  Loader,
  NumberInput,
  Stack,
  Text,
  Title,
} from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useBootstrap, useDay, useUpdateSet } from '../api/hooks';
import type { ExerciseSet } from '../api/types';
import { FinishWorkoutModal } from '../components/FinishWorkoutModal';
import { muscleColor } from '../lib/muscleColors';

// E2: current-workout day view — exercises grouped by muscle group, editable weight/reps,
// and a LOG checkbox that persists the logged state (PATCH /api/sets).
export function DayViewPage() {
  const { id } = useParams();
  const dayId = id ? Number(id) : undefined;
  const { data: day, isLoading, isError, error } = useDay(dayId);
  const { data: boot } = useBootstrap();
  const updateSet = useUpdateSet(dayId);
  const [finishOpen, finishHandlers] = useDisclosure(false);

  const mgName = useMemo(
    () => new Map((boot?.muscleGroups ?? []).map((mg) => [mg.id, mg.name])),
    [boot],
  );
  const exName = useMemo(
    () => new Map((boot?.exercises ?? []).map((e) => [e.id, e.name])),
    [boot],
  );

  if (isLoading) return <Loader />;
  if (isError) return <Text c="red">Failed to load day: {(error as Error).message}</Text>;
  if (!day) return null;

  const pinned = day.notes?.filter((n) => n.pinned) ?? [];
  const sortedExercises = day.exercises
    .slice()
    .sort((a, b) => (a.position ?? 0) - (b.position ?? 0));

  return (
    <Stack>
      <Group justify="space-between">
        <div>
          <Title order={2}>
            Week {day.week} · Day {day.position}
          </Title>
          {day.label && (
            <Text c="dimmed" size="sm">
              {day.label}
            </Text>
          )}
        </div>
        {day.finishedAt ? (
          <Badge color="green" variant="light" size="lg">
            Finished
          </Badge>
        ) : (
          <Button color="red" onClick={finishHandlers.open}>
            Finish workout
          </Button>
        )}
      </Group>

      <FinishWorkoutModal
        day={day}
        mgName={mgName}
        exName={exName}
        opened={finishOpen}
        onClose={finishHandlers.close}
      />

      {pinned.map((n) => (
        <Card key={n.id} withBorder padding="xs" bg="yellow.9">
          <Text size="sm">📌 {n.text}</Text>
        </Card>
      ))}

      {sortedExercises.map((ex) => {
        const mg = mgName.get(ex.muscleGroupId ?? -1);
        return (
          <Card key={ex.id} withBorder padding="md">
            <Group gap="xs" mb={4}>
              <Badge color={muscleColor(mg)} variant="light">
                {mg ?? '—'}
              </Badge>
            </Group>
            <Text fw={600} mb="sm">
              {exName.get(ex.exerciseId ?? -1) ?? `Exercise ${ex.exerciseId}`}
            </Text>

            {ex.sets.length === 0 ? (
              <Text size="sm" c="dimmed">
                Exercise not programmed yet
              </Text>
            ) : (
              <Stack gap={6}>
                <Group gap="md" px={4}>
                  <Text size="xs" c="dimmed" w={120}>
                    WEIGHT{day.unit ? ` (${day.unit})` : ''}
                  </Text>
                  <Text size="xs" c="dimmed" w={100}>
                    REPS
                  </Text>
                  <Text size="xs" c="dimmed">
                    LOG
                  </Text>
                </Group>
                {ex.sets
                  .slice()
                  .sort((a, b) => (a.position ?? 0) - (b.position ?? 0))
                  .map((s) => (
                    <SetRow
                      key={s.id}
                      set={s}
                      pending={updateSet.isPending}
                      onSave={(body) => updateSet.mutate({ id: s.id, body })}
                    />
                  ))}
              </Stack>
            )}
          </Card>
        );
      })}
    </Stack>
  );
}

type NumOrEmpty = number | '';

function SetRow({
  set,
  pending,
  onSave,
}: {
  set: ExerciseSet;
  pending: boolean;
  onSave: (body: { weight?: number | null; reps?: number | null; status?: string }) => void;
}) {
  const logged = !!set.finishedAt;
  const [weight, setWeight] = useState<NumOrEmpty>(set.weight ?? '');
  const [reps, setReps] = useState<NumOrEmpty>(set.reps ?? '');

  // Re-seed from server after refetch (e.g. progression rewrote targets).
  useEffect(() => {
    setWeight(set.weight ?? '');
    setReps(set.reps ?? '');
  }, [set.weight, set.reps]);

  const commitIfChanged = () => {
    const w = weight === '' ? null : weight;
    const r = reps === '' ? null : reps;
    if (w !== (set.weight ?? null) || r !== (set.reps ?? null)) {
      onSave({ weight: w, reps: r });
    }
  };

  const toggleLog = (checked: boolean) => {
    onSave({
      weight: weight === '' ? null : weight,
      reps: reps === '' ? null : reps,
      status: checked ? 'complete' : 'pending',
    });
  };

  return (
    <Group gap="md" px={4} wrap="nowrap">
      <NumberInput
        w={120}
        size="sm"
        hideControls
        value={weight}
        placeholder={set.weightTarget != null ? String(set.weightTarget) : undefined}
        onChange={(v) => setWeight(v === '' ? '' : Number(v))}
        onBlur={commitIfChanged}
        disabled={pending}
      />
      <NumberInput
        w={100}
        size="sm"
        hideControls
        value={reps}
        placeholder={set.repsTarget != null ? String(set.repsTarget) : undefined}
        onChange={(v) => setReps(v === '' ? '' : Number(v))}
        onBlur={commitIfChanged}
        disabled={pending}
      />
      <Checkbox
        checked={logged}
        onChange={(e) => toggleLog(e.currentTarget.checked)}
        disabled={pending}
      />
    </Group>
  );
}
