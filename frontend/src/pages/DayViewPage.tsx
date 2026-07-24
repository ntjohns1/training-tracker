import { Button, Group, Stack, Text } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { useMemo } from 'react';
import { useParams } from 'react-router-dom';
import { useBootstrap, useDay } from '../api/hooks';
import { DayView } from '../components/DayView';
import { FinishWorkoutModal } from '../components/FinishWorkoutModal';
import { PageContainer } from '../components/PageContainer';

/** Direct link to a specific day (/days/:id). The primary entry point is Current workout. */
export function DayViewPage() {
  const { id } = useParams();
  const dayId = id ? Number(id) : undefined;
  const { data: day } = useDay(dayId);
  const { data: boot } = useBootstrap();
  const [finishOpen, finishHandlers] = useDisclosure(false);

  const mgName = useMemo(
    () => new Map((boot?.muscleGroups ?? []).map((m) => [m.id, m.name])),
    [boot],
  );
  const exName = useMemo(
    () => new Map((boot?.exercises ?? []).map((e) => [e.id, e.name])),
    [boot],
  );
  const mesoName = useMemo(
    () => (boot?.mesocycles ?? []).find((m) => m.id === day?.mesoId)?.name ?? null,
    [boot, day?.mesoId],
  );

  return (
    <PageContainer>
      <Stack>
        <DayView dayId={dayId} mesoName={mesoName} />

        {day && !day.finishedAt && (
          <Group justify="flex-end">
            <Button color="red" onClick={finishHandlers.open}>
              Finish workout
            </Button>
          </Group>
        )}
        {day?.finishedAt && (
          <Group justify="flex-end">
            <Text c="teal" fw={600} size="sm">
              Workout finished
            </Text>
          </Group>
        )}

        {day && (
          <FinishWorkoutModal
            day={day}
            mgName={mgName}
            exName={exName}
            opened={finishOpen}
            onClose={finishHandlers.close}
          />
        )}
      </Stack>
    </PageContainer>
  );
}
