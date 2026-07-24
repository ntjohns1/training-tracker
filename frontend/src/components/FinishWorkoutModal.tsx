import {
  Badge,
  Button,
  Divider,
  Group,
  Modal,
  SegmentedControl,
  Stack,
  Text,
} from '@mantine/core';
import { notifications } from '@mantine/notifications';
import { useState } from 'react';
import { useFinishDay } from '../api/hooks';
import type { FinishDayBody } from '../api/hooks';
import type { Day } from '../api/types';
import { muscleColor } from '../lib/muscleColors';

// Feedback scales (integers the progression engine expects — see ProgressionCalculator):
// jointPain/soreness/workload 0..3, pump 0..2.
const JOINT_PAIN = [
  { value: '0', label: 'None' },
  { value: '1', label: 'Low' },
  { value: '2', label: 'Moderate' },
  { value: '3', label: 'A lot' },
];
const SORENESS = [
  { value: '0', label: 'Never got sore' },
  { value: '1', label: 'Healed a while ago' },
  { value: '2', label: 'Healed just in time' },
  { value: '3', label: 'Still sore' },
];
const PUMP = [
  { value: '0', label: 'Low' },
  { value: '1', label: 'Medium' },
  { value: '2', label: 'High' },
];
const WORKLOAD = [
  { value: '0', label: 'Easy' },
  { value: '1', label: 'Pretty good' },
  { value: '2', label: 'Pushed my limits' },
  { value: '3', label: 'Too much' },
];

type Ratings = Record<number, string>;

export function FinishWorkoutModal({
  day,
  mgName,
  exName,
  opened,
  onClose,
}: {
  day: Day;
  mgName: Map<number, string>;
  exName: Map<number, string>;
  opened: boolean;
  onClose: () => void;
}) {
  const finishDay = useFinishDay();
  const [jointPain, setJointPain] = useState<Ratings>({});
  const [pump, setPump] = useState<Ratings>({});
  const [soreness, setSoreness] = useState<Ratings>({});
  const [workload, setWorkload] = useState<Ratings>({});

  const num = (r: Ratings, id: number, fallback: number | null) =>
    r[id] != null ? Number(r[id]) : fallback;

  const submit = () => {
    const body: FinishDayBody = {
      id: day.id,
      mesoId: day.mesoId,
      week: day.week,
      position: day.position,
      unit: day.unit,
      finishedAt: new Date().toISOString(),
      label: day.label,
      status: 'complete',
      exercises: day.exercises.map((ex) => ({
        id: ex.id,
        dayId: ex.dayId,
        exerciseId: ex.exerciseId,
        position: ex.position,
        jointPain: num(jointPain, ex.id, ex.jointPain),
        muscleGroupId: ex.muscleGroupId,
        sets: ex.sets.map((s) => ({
          id: s.id,
          dayExerciseId: s.dayExerciseId,
          position: s.position,
          setType: s.setType,
          weight: s.weight,
          reps: s.reps,
          unit: s.unit,
          finishedAt: s.finishedAt,
          status: s.status,
        })),
        status: 'complete',
      })),
      muscleGroups: day.muscleGroups.map((mg) => ({
        id: mg.id,
        dayId: mg.dayId,
        muscleGroupId: mg.muscleGroupId,
        pump: num(pump, mg.id, mg.pump),
        soreness: num(soreness, mg.id, mg.soreness),
        workload: num(workload, mg.id, mg.workload),
        status: 'complete',
      })),
    };

    finishDay.mutate(
      { id: day.id, body },
      {
        onSuccess: () => {
          notifications.show({ message: 'Workout finished — next week programmed.', color: 'green' });
          onClose();
        },
        onError: (e) =>
          notifications.show({ message: `Finish failed: ${(e as Error).message}`, color: 'red' }),
      },
    );
  };

  return (
    <Modal opened={opened} onClose={onClose} title="Finish workout" size="lg">
      <Stack>
        <Text fw={700} size="sm" tt="uppercase" c="dimmed">
          Per muscle group
        </Text>
        {day.muscleGroups.map((mg) => {
          const name = mgName.get(mg.muscleGroupId ?? -1);
          return (
            <Stack key={mg.id} gap={6}>
              <Badge color={muscleColor(name)} variant="light" w="fit-content">
                {name ?? '—'}
              </Badge>
              <Rating label="Soreness (since last time)" data={SORENESS}
                value={soreness[mg.id]} onChange={(v) => setSoreness((p) => ({ ...p, [mg.id]: v }))} />
              <Rating label="Pump" data={PUMP}
                value={pump[mg.id]} onChange={(v) => setPump((p) => ({ ...p, [mg.id]: v }))} />
              <Rating label="Workload" data={WORKLOAD}
                value={workload[mg.id]} onChange={(v) => setWorkload((p) => ({ ...p, [mg.id]: v }))} />
              <Divider my={4} />
            </Stack>
          );
        })}

        <Text fw={700} size="sm" tt="uppercase" c="dimmed">
          Joint pain per exercise
        </Text>
        {day.exercises.map((ex) => (
          <Rating
            key={ex.id}
            label={exName.get(ex.exerciseId ?? -1) ?? `Exercise ${ex.exerciseId}`}
            data={JOINT_PAIN}
            value={jointPain[ex.id]}
            onChange={(v) => setJointPain((p) => ({ ...p, [ex.id]: v }))}
          />
        ))}

        <Group justify="flex-end" mt="sm">
          <Button variant="default" onClick={onClose}>
            Cancel
          </Button>
          <Button color="red" onClick={submit} loading={finishDay.isPending}>
            Finish workout
          </Button>
        </Group>
      </Stack>
    </Modal>
  );
}

function Rating({
  label,
  data,
  value,
  onChange,
}: {
  label: string;
  data: { value: string; label: string }[];
  value: string | undefined;
  onChange: (v: string) => void;
}) {
  return (
    <div>
      <Text size="sm" mb={2}>
        {label}
      </Text>
      <SegmentedControl
        fullWidth
        size="xs"
        data={data}
        value={value ?? ''}
        onChange={onChange}
      />
    </div>
  );
}
