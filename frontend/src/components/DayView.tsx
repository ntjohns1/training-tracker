import {
  ActionIcon,
  Badge,
  Box,
  Card,
  Checkbox,
  Divider,
  Group,
  Loader,
  Menu,
  NumberInput,
  Stack,
  Text,
} from '@mantine/core';
import {
  IconDotsVertical,
  IconHistory,
  IconInfoCircle,
  IconPlayerPlay,
  IconTrendingUp,
} from '@tabler/icons-react';
import { useEffect, useMemo, useState } from 'react';
import { useBootstrap, useDay, useUpdateSet } from '../api/hooks';
import type { Day, DayExercise, ExerciseSet } from '../api/types';
import { muscleColor } from '../lib/muscleColors';

/** "machine-assistance" -> "Machine Assistance" */
function prettyEquipment(type: string | null | undefined): string {
  if (!type) return '';
  return type
    .split('-')
    .map((w) => w.charAt(0).toUpperCase() + w.slice(1))
    .join(' ');
}

/** Exercises grouped by muscle group, preserving first-appearance order (RP renders one card per group). */
function groupByMuscle(exercises: DayExercise[]): { muscleGroupId: number | null; items: DayExercise[] }[] {
  const order: (number | null)[] = [];
  const byMg = new Map<number | null, DayExercise[]>();
  for (const ex of [...exercises].sort((a, b) => (a.position ?? 0) - (b.position ?? 0))) {
    const key = ex.muscleGroupId ?? null;
    if (!byMg.has(key)) {
      byMg.set(key, []);
      order.push(key);
    }
    byMg.get(key)!.push(ex);
  }
  return order.map((muscleGroupId) => ({ muscleGroupId, items: byMg.get(muscleGroupId)! }));
}

export function DayView({
  dayId,
  mesoName,
  headerRight,
}: {
  dayId: number | undefined;
  mesoName?: string | null;
  headerRight?: React.ReactNode;
}) {
  const { data: day, isLoading, isError, error } = useDay(dayId);
  const { data: boot } = useBootstrap();
  const updateSet = useUpdateSet(dayId);

  const mgName = useMemo(
    () => new Map((boot?.muscleGroups ?? []).map((m) => [m.id, m.name])),
    [boot],
  );
  const exById = useMemo(
    () => new Map((boot?.exercises ?? []).map((e) => [e.id, e])),
    [boot],
  );

  if (isLoading) return <Loader />;
  if (isError) return <Text c="red">Failed to load day: {(error as Error).message}</Text>;
  if (!day) return null;

  const groups = groupByMuscle(day.exercises);
  const pinnedByExercise = new Map<number, string>();
  for (const n of day.notes ?? []) {
    if (n.pinned && n.text) pinnedByExercise.set(n.id, n.text);
  }

  return (
    /* Width is owned by PageContainer, which wraps this view. */
    <Stack gap="md">
      <DayHeader day={day} mesoName={mesoName} right={headerRight} />

      {groups.map((group) => {
        const name = group.muscleGroupId != null ? mgName.get(group.muscleGroupId) : undefined;
        return (
          <Box key={group.muscleGroupId ?? 'none'}>
            <Badge
              color={muscleColor(name)}
              variant="filled"
              radius="sm"
              mb={6}
              leftSection={<IconMenuBars />}
            >
              {(name ?? 'OTHER').toUpperCase()}
            </Badge>

            <Card withBorder padding="md" radius="md">
              <Stack gap="lg">
                {group.items.map((ex, idx) => {
                  const catalog = ex.exerciseId != null ? exById.get(ex.exerciseId) : undefined;
                  return (
                    <Box key={ex.id}>
                      {idx > 0 && <Divider mb="lg" />}
                      <ExerciseBlock
                        dayExercise={ex}
                        title={catalog?.name ?? `Exercise ${ex.exerciseId}`}
                        equipment={prettyEquipment(catalog?.exerciseType)}
                        unit={day.unit}
                        pending={updateSet.isPending}
                        onSaveSet={(setId, body) => updateSet.mutate({ id: setId, body })}
                      />
                    </Box>
                  );
                })}
              </Stack>
            </Card>
          </Box>
        );
      })}
    </Stack>
  );
}

/** Small three-bar glyph RP shows inside the muscle-group pill. */
function IconMenuBars() {
  return (
    <span style={{ letterSpacing: '-1px', fontWeight: 900 }} aria-hidden>
      |||
    </span>
  );
}

function DayHeader({
  day,
  mesoName,
  right,
}: {
  day: Day;
  mesoName?: string | null;
  right?: React.ReactNode;
}) {
  const subtitleParts = [day.label, mesoName].filter(Boolean);
  return (
    // Header strip spec: 60px tall, 16px padding all round (min-height so the two-line
    // WEEK/DAY + meso subtitle can grow rather than clip).
    <Card p={16} mih={60} radius="md" bg="dark.6">
      <Group justify="space-between" align="flex-start" wrap="nowrap" h="100%">
        <div>
          <Group gap={8} align="baseline">
            <Text fw={800} size="xl">
              WEEK {day.week}
            </Text>
            <Text fw={800} size="xl" c="dimmed">
              DAY {day.position}
            </Text>
          </Group>
          {subtitleParts.length > 0 && (
            <Text size="sm" c="dimmed">
              {subtitleParts.join(' · ')}
            </Text>
          )}
        </div>
        <Group gap={4} wrap="nowrap">
          {right}
        </Group>
      </Group>
    </Card>
  );
}

function ExerciseBlock({
  dayExercise,
  title,
  equipment,
  unit,
  pending,
  onSaveSet,
}: {
  dayExercise: DayExercise;
  title: string;
  equipment: string;
  unit: string | null;
  pending: boolean;
  onSaveSet: (setId: number, body: { weight?: number | null; reps?: number | null; status?: string }) => void;
}) {
  const sets = [...dayExercise.sets].sort((a, b) => (a.position ?? 0) - (b.position ?? 0));

  return (
    <>
      <Group justify="space-between" align="flex-start" wrap="nowrap">
        <div>
          <Text fw={700} size="lg">
            {title}
          </Text>
          {equipment && (
            <Text size="sm" c="dimmed">
              {equipment}
            </Text>
          )}
        </div>
        <Group gap={2} wrap="nowrap">
          <ActionIcon variant="subtle" color="gray" aria-label="Demo video">
            <IconPlayerPlay size={18} />
          </ActionIcon>
          <ActionIcon variant="subtle" color="gray" aria-label="History">
            <IconHistory size={18} />
          </ActionIcon>
          <Menu position="bottom-end">
            <Menu.Target>
              <ActionIcon variant="subtle" color="gray" aria-label="Exercise options">
                <IconDotsVertical size={18} />
              </ActionIcon>
            </Menu.Target>
            <Menu.Dropdown>
              <Menu.Item disabled>Replace exercise</Menu.Item>
              <Menu.Item disabled>Add note</Menu.Item>
            </Menu.Dropdown>
          </Menu>
        </Group>
      </Group>

      {sets.length === 0 ? (
        <Group
          gap={8}
          mt="sm"
          p="xs"
          wrap="nowrap"
          style={{ background: 'var(--mantine-color-indigo-9)', borderRadius: 6 }}
        >
          <IconInfoCircle size={16} />
          <Text size="sm">Exercise not programmed yet</Text>
        </Group>
      ) : (
        <>
          <Group gap={0} mt="md" px={4} wrap="nowrap">
            <Box w={28} />
            <Text size="xs" c="dimmed" fw={600} style={{ flex: 1, textAlign: 'center' }}>
              WEIGHT{unit ? ` (${unit})` : ''}
            </Text>
            <Group gap={4} style={{ flex: 1 }} justify="center" wrap="nowrap">
              <Text size="xs" c="dimmed" fw={600}>
                REPS
              </Text>
              <IconInfoCircle size={12} opacity={0.6} />
            </Group>
            <Text size="xs" c="dimmed" fw={600} w={70} ta="center">
              LOG
            </Text>
          </Group>

          <Stack gap={2} mt={4}>
            {sets.map((s) => (
              <SetRow
                key={s.id}
                set={s}
                pending={pending}
                onSave={(body) => onSaveSet(s.id, body)}
              />
            ))}
          </Stack>
        </>
      )}
    </>
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

  useEffect(() => {
    setWeight(set.weight ?? '');
    setReps(set.reps ?? '');
  }, [set.weight, set.reps]);

  const commitIfChanged = () => {
    const w = weight === '' ? null : weight;
    const r = reps === '' ? null : reps;
    if (w !== (set.weight ?? null) || r !== (set.reps ?? null)) onSave({ weight: w, reps: r });
  };

  return (
    <Group gap={0} px={4} py={2} wrap="nowrap">
      <Menu position="bottom-start">
        <Menu.Target>
          <ActionIcon variant="subtle" color="gray" w={28} aria-label="Set options">
            <IconDotsVertical size={16} />
          </ActionIcon>
        </Menu.Target>
        <Menu.Dropdown>
          <Menu.Item disabled>Add set</Menu.Item>
          <Menu.Item disabled>Remove set</Menu.Item>
        </Menu.Dropdown>
      </Menu>

      <Box style={{ flex: 1 }} px={6}>
        <NumberInput
          size="sm"
          hideControls
          styles={{ input: { textAlign: 'center' } }}
          value={weight}
          placeholder={set.weightTarget != null ? String(set.weightTarget) : undefined}
          onChange={(v) => setWeight(v === '' ? '' : Number(v))}
          onBlur={commitIfChanged}
          disabled={pending}
        />
      </Box>
      <Box style={{ flex: 1 }} px={6}>
        <NumberInput
          size="sm"
          hideControls
          styles={{ input: { textAlign: 'center' } }}
          value={reps}
          placeholder={set.repsTarget != null ? String(set.repsTarget) : undefined}
          onChange={(v) => setReps(v === '' ? '' : Number(v))}
          onBlur={commitIfChanged}
          disabled={pending}
        />
      </Box>

      <Group w={70} justify="center" gap={6} wrap="nowrap">
        {logged && <IconTrendingUp size={16} color="var(--mantine-color-teal-5)" />}
        <Checkbox
          color="teal"
          checked={logged}
          onChange={(e) =>
            onSave({
              weight: weight === '' ? null : weight,
              reps: reps === '' ? null : reps,
              status: e.currentTarget.checked ? 'complete' : 'pending',
            })
          }
          disabled={pending}
        />
      </Group>
    </Group>
  );
}
