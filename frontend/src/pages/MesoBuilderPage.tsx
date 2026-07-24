import {
  ActionIcon,
  Button,
  Card,
  Group,
  Loader,
  Select,
  Stack,
  Text,
  TextInput,
  Title,
} from '@mantine/core';
import { IconGripVertical, IconTrash, IconX } from '@tabler/icons-react';
import { notifications } from '@mantine/notifications';
import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useBootstrap, useCreateMeso } from '../api/hooks';
import type { CreateMesoBody } from '../api/hooks';

interface DayDraft {
  label: string;
  exerciseIds: number[];
}

const WEEK_OPTIONS = ['4', '5', '6', '7', '8'].map((w) => ({ value: w, label: `${w} weeks` }));

export function MesoBuilderPage() {
  const { data: boot, isLoading } = useBootstrap();
  const createMeso = useCreateMeso();
  const navigate = useNavigate();

  const [name, setName] = useState('');
  const [weeks, setWeeks] = useState('5');
  const [unit, setUnit] = useState('lb');
  const [days, setDays] = useState<DayDraft[]>([
    { label: 'Day 1', exerciseIds: [] },
    { label: 'Day 2', exerciseIds: [] },
  ]);

  const exById = useMemo(
    () => new Map((boot?.exercises ?? []).map((e) => [e.id, e])),
    [boot],
  );
  const mgById = useMemo(
    () => new Map((boot?.muscleGroups ?? []).map((mg) => [mg.id, mg.name])),
    [boot],
  );

  // Exercise picker options, grouped by muscle group.
  const exerciseOptions = useMemo(() => {
    const groups = new Map<string, { value: string; label: string }[]>();
    for (const e of boot?.exercises ?? []) {
      if (e.deletedAt) continue;
      const g = e.muscleGroupId ? mgById.get(e.muscleGroupId) ?? 'Other' : 'Other';
      if (!groups.has(g)) groups.set(g, []);
      groups.get(g)!.push({ value: String(e.id), label: e.name });
    }
    return Array.from(groups.entries())
      .sort((a, b) => a[0].localeCompare(b[0]))
      .map(([group, items]) => ({
        group,
        items: items.sort((a, b) => a.label.localeCompare(b.label)),
      }));
  }, [boot, mgById]);

  if (isLoading) return <Loader />;

  const setDay = (i: number, patch: Partial<DayDraft>) =>
    setDays((prev) => prev.map((d, idx) => (idx === i ? { ...d, ...patch } : d)));
  const addDay = () =>
    setDays((prev) => [...prev, { label: `Day ${prev.length + 1}`, exerciseIds: [] }]);
  const removeDay = (i: number) => setDays((prev) => prev.filter((_, idx) => idx !== i));
  const addExercise = (i: number, exId: number) =>
    setDay(i, { exerciseIds: [...days[i].exerciseIds, exId] });
  const removeExercise = (i: number, pos: number) =>
    setDay(i, { exerciseIds: days[i].exerciseIds.filter((_, idx) => idx !== pos) });

  const problems: string[] = [];
  if (!name.trim()) problems.push('name');
  if (days.length < 2) problems.push('at least 2 days');
  if (days.some((d) => d.exerciseIds.length === 0)) problems.push('each day needs ≥1 exercise');
  const valid = problems.length === 0;

  const submit = () => {
    // Derive progressions from the distinct muscle groups across all chosen exercises.
    const progressions: CreateMesoBody['progressions'] = {};
    for (const d of days) {
      for (const exId of d.exerciseIds) {
        const mgId = exById.get(exId)?.muscleGroupId;
        if (mgId != null) {
          const key = mgById.get(mgId) ?? String(mgId);
          progressions[key] = { mgProgressionType: 'regular', muscleGroupId: mgId };
        }
      }
    }
    const body: CreateMesoBody = {
      name: name.trim(),
      weeks: Number(weeks),
      unit,
      days: days.map((d) => ({
        label: d.label,
        exercises: d.exerciseIds.map((exerciseId) => ({ exerciseId })),
      })),
      progressions,
    };

    createMeso.mutate(body, {
      onSuccess: (created) => {
        notifications.show({ message: `Created "${name}".`, color: 'green' });
        navigate(`/mesocycles/${created.id}`);
      },
      onError: (e) =>
        notifications.show({ message: `Create failed: ${(e as Error).message}`, color: 'red' }),
    });
  };

  return (
    <Stack>
      <Title order={2}>New mesocycle</Title>

      <Group align="flex-end" wrap="wrap">
        <TextInput
          label="Name"
          placeholder="e.g. 2026_P6"
          value={name}
          onChange={(e) => setName(e.currentTarget.value)}
          w={240}
        />
        <Select label="Weeks" data={WEEK_OPTIONS} value={weeks} onChange={(v) => setWeeks(v ?? '5')} w={140} />
        <Select
          label="Unit"
          data={[
            { value: 'lb', label: 'lb' },
            { value: 'kg', label: 'kg' },
          ]}
          value={unit}
          onChange={(v) => setUnit(v ?? 'lb')}
          w={100}
        />
      </Group>

      <Group align="flex-start" wrap="wrap">
        {days.map((day, i) => (
          <Card key={i} withBorder padding="sm" w={300}>
            <Group justify="space-between" mb="xs">
              <TextInput
                variant="unstyled"
                fw={600}
                value={day.label}
                onChange={(e) => setDay(i, { label: e.currentTarget.value })}
              />
              <ActionIcon color="red" variant="subtle" onClick={() => removeDay(i)}>
                <IconTrash size={16} />
              </ActionIcon>
            </Group>

            <Stack gap={4} mb="xs">
              {day.exerciseIds.map((exId, pos) => {
                const ex = exById.get(exId);
                return (
                  <Group key={pos} gap={4} wrap="nowrap" justify="space-between">
                    <Group gap={4} wrap="nowrap">
                      <IconGripVertical size={14} opacity={0.4} />
                      <div>
                        <Text size="sm">{ex?.name ?? exId}</Text>
                        <Text size="xs" c="dimmed">
                          {ex?.muscleGroupId ? mgById.get(ex.muscleGroupId) : ''}
                        </Text>
                      </div>
                    </Group>
                    <ActionIcon size="sm" variant="subtle" color="gray" onClick={() => removeExercise(i, pos)}>
                      <IconX size={14} />
                    </ActionIcon>
                  </Group>
                );
              })}
            </Stack>

            <Select
              placeholder="Add exercise"
              searchable
              clearable
              data={exerciseOptions}
              value={null}
              onChange={(v) => v && addExercise(i, Number(v))}
              nothingFoundMessage="No match"
            />
          </Card>
        ))}
        <Button variant="light" onClick={addDay} h={60} w={140}>
          + Add day
        </Button>
      </Group>

      <Group>
        <Button color="red" disabled={!valid} loading={createMeso.isPending} onClick={submit}>
          Create mesocycle
        </Button>
        {!valid && (
          <Text size="sm" c="dimmed">
            Needs: {problems.join(', ')}
          </Text>
        )}
      </Group>
    </Stack>
  );
}
