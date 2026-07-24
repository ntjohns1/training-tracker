import {
  ActionIcon,
  Box,
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
import { DragDropContext, Draggable, Droppable, type DropResult } from '@hello-pangea/dnd';
import { IconGripVertical, IconTrash, IconX } from '@tabler/icons-react';
import { notifications } from '@mantine/notifications';
import { useMemo, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useBootstrap, useCreateMeso } from '../api/hooks';
import type { CreateMesoBody } from '../api/hooks';
import { muscleColor } from '../lib/muscleColors';

interface DayDraft {
  key: string;
  label: string;
  exerciseIds: number[];
}

const WEEK_OPTIONS = ['4', '5', '6', '7', '8'].map((w) => ({ value: w, label: `${w} weeks` }));
const WEEKDAYS = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'].map(
  (d) => ({ value: d, label: d }),
);

/**
 * Planning board for a NEW mesocycle. Day columns and exercise cells both drag to reorder;
 * it is all local state until Create, so nothing needs persisting here.
 */
export function MesoBuilderPage() {
  const { data: boot, isLoading } = useBootstrap();
  const createMeso = useCreateMeso();
  const navigate = useNavigate();
  const nextKey = useRef(2);

  const [name, setName] = useState('');
  const [weeks, setWeeks] = useState('5');
  const [unit, setUnit] = useState('lb');
  const [days, setDays] = useState<DayDraft[]>([
    { key: 'd0', label: 'Monday', exerciseIds: [] },
    { key: 'd1', label: 'Tuesday', exerciseIds: [] },
  ]);

  const exById = useMemo(() => new Map((boot?.exercises ?? []).map((e) => [e.id, e])), [boot]);
  const mgById = useMemo(
    () => new Map((boot?.muscleGroups ?? []).map((mg) => [mg.id, mg.name])),
    [boot],
  );
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
      .map(([group, items]) => ({ group, items: items.sort((a, b) => a.label.localeCompare(b.label)) }));
  }, [boot, mgById]);

  if (isLoading) return <Loader />;

  const setDay = (key: string, patch: Partial<DayDraft>) =>
    setDays((prev) => prev.map((d) => (d.key === key ? { ...d, ...patch } : d)));
  const addDay = () =>
    setDays((prev) => [
      ...prev,
      { key: `d${nextKey.current++}`, label: WEEKDAYS[prev.length % 7].value, exerciseIds: [] },
    ]);
  const removeDay = (key: string) => setDays((prev) => prev.filter((d) => d.key !== key));

  const onDragEnd = (result: DropResult) => {
    const { source, destination, type } = result;
    if (!destination) return;

    if (type === 'COLUMN') {
      if (destination.index === source.index) return;
      setDays((prev) => {
        const next = [...prev];
        const [moved] = next.splice(source.index, 1);
        next.splice(destination.index, 0, moved);
        return next;
      });
      return;
    }

    // Exercise cell: reorder within a day, or move between days.
    const fromKey = source.droppableId;
    const toKey = destination.droppableId;
    if (fromKey === toKey && destination.index === source.index) return;

    setDays((prev) => {
      const next = prev.map((d) => ({ ...d, exerciseIds: [...d.exerciseIds] }));
      const from = next.find((d) => d.key === fromKey);
      const to = next.find((d) => d.key === toKey);
      if (!from || !to) return prev;
      const [movedId] = from.exerciseIds.splice(source.index, 1);
      to.exerciseIds.splice(destination.index, 0, movedId);
      return next;
    });
  };

  const problems: string[] = [];
  if (!name.trim()) problems.push('name');
  if (days.length < 2) problems.push('at least 2 days');
  if (days.some((d) => d.exerciseIds.length === 0)) problems.push('each day needs ≥1 exercise');
  const valid = problems.length === 0;

  const submit = () => {
    const progressions: CreateMesoBody['progressions'] = {};
    for (const d of days) {
      for (const exId of d.exerciseIds) {
        const mgId = exById.get(exId)?.muscleGroupId;
        if (mgId != null) {
          progressions[mgById.get(mgId) ?? String(mgId)] = {
            mgProgressionType: 'regular',
            muscleGroupId: mgId,
          };
        }
      }
    }
    createMeso.mutate(
      {
        name: name.trim(),
        weeks: Number(weeks),
        unit,
        days: days.map((d) => ({
          label: d.label,
          exercises: d.exerciseIds.map((exerciseId) => ({ exerciseId })),
        })),
        progressions,
      },
      {
        onSuccess: (created) => {
          notifications.show({ message: `Created "${name}".`, color: 'green' });
          navigate(`/mesocycles/${created.id}`);
        },
        onError: (e) =>
          notifications.show({ message: `Create failed: ${(e as Error).message}`, color: 'red' }),
      },
    );
  };

  return (
    <Stack>
      <Group justify="space-between" align="flex-start">
        <Title order={2}>New mesocycle</Title>
        <Button color="red" disabled={!valid} loading={createMeso.isPending} onClick={submit}>
          Create mesocycle
        </Button>
      </Group>

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

      {!valid && (
        <Text size="sm" c="dimmed">
          Needs: {problems.join(', ')}
        </Text>
      )}

      <DragDropContext onDragEnd={onDragEnd}>
        <Droppable droppableId="board" direction="horizontal" type="COLUMN">
          {(boardProvided) => (
            /* Native overflow (not Mantine ScrollArea) so the DnD library can track scroll. */
            <Box style={{ overflowX: 'auto' }}>
              <Group
                align="flex-start"
                wrap="nowrap"
                gap="md"
                pb="sm"
                ref={boardProvided.innerRef}
                {...boardProvided.droppableProps}
              >
                {days.map((day, colIndex) => (
                  <Draggable key={day.key} draggableId={`col-${day.key}`} index={colIndex}>
                    {(colProvided, colSnapshot) => (
                      <Card
                        withBorder
                        padding="sm"
                        w={300}
                        style={{
                          ...colProvided.draggableProps.style,
                          flexShrink: 0,
                          opacity: colSnapshot.isDragging ? 0.9 : 1,
                        }}
                        ref={colProvided.innerRef}
                        {...colProvided.draggableProps}
                      >
                        <Group justify="space-between" mb="xs" wrap="nowrap">
                          <Group gap={4} wrap="nowrap" style={{ flex: 1 }}>
                            <Box {...colProvided.dragHandleProps} style={{ cursor: 'grab' }}>
                              <IconGripVertical size={16} opacity={0.5} />
                            </Box>
                            <Select
                              size="xs"
                              data={WEEKDAYS}
                              value={day.label}
                              onChange={(v) => setDay(day.key, { label: v ?? day.label })}
                              allowDeselect={false}
                              style={{ flex: 1 }}
                            />
                          </Group>
                          <ActionIcon
                            color="red"
                            variant="subtle"
                            onClick={() => removeDay(day.key)}
                            aria-label="Remove day"
                          >
                            <IconTrash size={16} />
                          </ActionIcon>
                        </Group>

                        <Droppable droppableId={day.key} type="EXERCISE">
                          {(listProvided) => (
                            <Stack
                              gap={6}
                              mih={24}
                              ref={listProvided.innerRef}
                              {...listProvided.droppableProps}
                            >
                              {day.exerciseIds.map((exId, index) => {
                                const ex = exById.get(exId);
                                const mg = ex?.muscleGroupId ? mgById.get(ex.muscleGroupId) : undefined;
                                return (
                                  <Draggable
                                    key={`${day.key}-${exId}-${index}`}
                                    draggableId={`${day.key}-${exId}-${index}`}
                                    index={index}
                                  >
                                    {(cellProvided, cellSnapshot) => (
                                      <Card
                                        withBorder
                                        padding="xs"
                                        radius="sm"
                                        ref={cellProvided.innerRef}
                                        {...cellProvided.draggableProps}
                                        style={{
                                          ...cellProvided.draggableProps.style,
                                          opacity: cellSnapshot.isDragging ? 0.85 : 1,
                                        }}
                                      >
                                        <Group gap={6} wrap="nowrap" justify="space-between">
                                          <Group gap={6} wrap="nowrap" style={{ minWidth: 0 }}>
                                            <Box
                                              {...cellProvided.dragHandleProps}
                                              style={{ cursor: 'grab' }}
                                            >
                                              <IconGripVertical size={14} opacity={0.5} />
                                            </Box>
                                            <div style={{ minWidth: 0 }}>
                                              <Text size="xs" fw={700} c={muscleColor(mg)}>
                                                ||| {mg ?? '—'}
                                              </Text>
                                              <Text size="sm" truncate>
                                                {ex?.name ?? exId}
                                              </Text>
                                            </div>
                                          </Group>
                                          <ActionIcon
                                            size="sm"
                                            variant="subtle"
                                            color="gray"
                                            onClick={() =>
                                              setDay(day.key, {
                                                exerciseIds: day.exerciseIds.filter(
                                                  (_, i) => i !== index,
                                                ),
                                              })
                                            }
                                            aria-label="Remove exercise"
                                          >
                                            <IconX size={14} />
                                          </ActionIcon>
                                        </Group>
                                      </Card>
                                    )}
                                  </Draggable>
                                );
                              })}
                              {listProvided.placeholder}
                            </Stack>
                          )}
                        </Droppable>

                        <Select
                          mt="xs"
                          size="xs"
                          placeholder="Add exercise"
                          searchable
                          data={exerciseOptions}
                          value={null}
                          nothingFoundMessage="No match"
                          onChange={(v) =>
                            v && setDay(day.key, { exerciseIds: [...day.exerciseIds, Number(v)] })
                          }
                        />
                      </Card>
                    )}
                  </Draggable>
                ))}
                {boardProvided.placeholder}
                <Button variant="light" onClick={addDay} h={60} w={140} style={{ flexShrink: 0 }}>
                  + Add day
                </Button>
              </Group>
            </Box>
          )}
        </Droppable>
      </DragDropContext>
    </Stack>
  );
}
