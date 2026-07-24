import { ActionIcon, Button, Group, Loader, Menu, Stack, Text } from '@mantine/core';
import { IconCalendar, IconCheck } from '@tabler/icons-react';
import { useDisclosure } from '@mantine/hooks';
import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { useBootstrap, useDay, useMesocycle } from '../api/hooks';
import { DayView } from '../components/DayView';
import { FinishWorkoutModal } from '../components/FinishWorkoutModal';

/**
 * Default landing screen. The server designates which day to open via
 * CurrentMeso.currentDayId (earliest unfinished); the calendar lets you jump to any other.
 */
export function CurrentWorkoutPage() {
  const { data: boot, isLoading: bootLoading } = useBootstrap();

  // The active mesocycle: not deleted, not finished; newest first if several.
  const currentMesoId = useMemo(() => {
    const open = (boot?.mesocycles ?? [])
      .filter((m) => !m.deletedAt && !m.finishedAt)
      .sort((a, b) => (b.createdAt ?? '').localeCompare(a.createdAt ?? ''));
    return open[0]?.id;
  }, [boot]);

  const { data: meso, isLoading: mesoLoading } = useMesocycle(currentMesoId);
  const [selectedDayId, setSelectedDayId] = useState<number | undefined>();

  // Follow the server's pointer until the user picks a different day.
  useEffect(() => {
    if (meso?.currentDayId != null) setSelectedDayId((prev) => prev ?? meso.currentDayId ?? undefined);
  }, [meso?.currentDayId]);

  const dayId = selectedDayId ?? meso?.currentDayId ?? undefined;
  const { data: day } = useDay(dayId);
  const [finishOpen, finishHandlers] = useDisclosure(false);

  const mgName = useMemo(
    () => new Map((boot?.muscleGroups ?? []).map((m) => [m.id, m.name])),
    [boot],
  );
  const exName = useMemo(
    () => new Map((boot?.exercises ?? []).map((e) => [e.id, e.name])),
    [boot],
  );

  if (bootLoading || mesoLoading) return <Loader />;

  if (!currentMesoId || !meso) {
    return (
      <Stack>
        <Text fw={700} size="lg">
          No active mesocycle
        </Text>
        <Text c="dimmed" size="sm">
          Create one to start training.
        </Text>
        <Button component={Link} to="/mesocycles/new" w="fit-content">
          New mesocycle
        </Button>
      </Stack>
    );
  }

  if (!dayId) {
    return (
      <Stack>
        <Text fw={700} size="lg">
          {meso.name} is complete
        </Text>
        <Text c="dimmed" size="sm">
          Every day in this mesocycle is finished.
        </Text>
      </Stack>
    );
  }

  const dayPicker = (
    <Menu position="bottom-end" width={260}>
      <Menu.Target>
        <ActionIcon variant="subtle" color="gray" aria-label="Jump to day">
          <IconCalendar size={20} />
        </ActionIcon>
      </Menu.Target>
      <Menu.Dropdown mah={420} style={{ overflowY: 'auto' }}>
        {meso.weeks.map((week, wi) => (
          <div key={wi}>
            <Menu.Label>Week {wi + 1}</Menu.Label>
            {week.days.map((d) => (
              <Menu.Item
                key={d.id}
                onClick={() => setSelectedDayId(d.id)}
                rightSection={d.finishedAt ? <IconCheck size={14} color="teal" /> : null}
                bg={d.id === dayId ? 'dark.5' : undefined}
              >
                Day {d.position}
                {d.label ? ` · ${d.label}` : ''}
              </Menu.Item>
            ))}
          </div>
        ))}
      </Menu.Dropdown>
    </Menu>
  );

  return (
    <Stack>
      <DayView
        dayId={dayId}
        mesoName={meso.name}
        headerRight={
          <Group gap={4} wrap="nowrap">
            {dayPicker}
          </Group>
        }
      />

      {day && !day.finishedAt && (
        <Group justify="flex-end">
          <Button color="red" onClick={finishHandlers.open}>
            Finish workout
          </Button>
        </Group>
      )}
      {day && day.finishedAt && (
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
  );
}
