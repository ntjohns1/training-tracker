import {
  ActionIcon,
  Badge,
  Box,
  Button,
  Card,
  Group,
  Loader,
  Modal,
  SegmentedControl,
  Select,
  Stack,
  Text,
  TextInput,
  Title,
} from '@mantine/core';
import { DragDropContext, Draggable, Droppable, type DropResult } from '@hello-pangea/dnd';
import { IconGripVertical, IconPencil, IconTrash } from '@tabler/icons-react';
import { useDisclosure } from '@mantine/hooks';
import { notifications } from '@mantine/notifications';
import { useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useBootstrap, useDayExerciseMutations, useMesocycle, useUpdateMeso } from '../api/hooks';
import type { Day } from '../api/types';
import { muscleColor } from '../lib/muscleColors';

/**
 * Planning board for a saved mesocycle: one column per day of the selected week.
 * Days are fixed once a meso is built, so only exercises reorder (within a day) - and that
 * persists via day-exercise position PATCHes.
 */
export function MesoBoardPage() {
  const { id } = useParams();
  const mesoId = id ? Number(id) : undefined;
  const { data: meso, isLoading, isError, error } = useMesocycle(mesoId);
  const { data: boot } = useBootstrap();
  const { add, reorder, remove } = useDayExerciseMutations(mesoId);
  const updateMeso = useUpdateMeso();

  const [week, setWeek] = useState('0');
  const [renameOpen, renameHandlers] = useDisclosure(false);
  const [newName, setNewName] = useState('');

  const mgName = useMemo(
    () => new Map((boot?.muscleGroups ?? []).map((m) => [m.id, m.name])),
    [boot],
  );
  const exById = useMemo(
    () => new Map((boot?.exercises ?? []).map((e) => [e.id, e])),
    [boot],
  );
  const exerciseOptions = useMemo(() => {
    const groups = new Map<string, { value: string; label: string }[]>();
    for (const e of boot?.exercises ?? []) {
      if (e.deletedAt) continue;
      const g = e.muscleGroupId ? mgName.get(e.muscleGroupId) ?? 'Other' : 'Other';
      if (!groups.has(g)) groups.set(g, []);
      groups.get(g)!.push({ value: String(e.id), label: e.name });
    }
    return Array.from(groups.entries())
      .sort((a, b) => a[0].localeCompare(b[0]))
      .map(([group, items]) => ({ group, items: items.sort((a, b) => a.label.localeCompare(b.label)) }));
  }, [boot, mgName]);

  if (isLoading) return <Loader />;
  if (isError) return <Text c="red">Failed to load: {(error as Error).message}</Text>;
  if (!meso) return null;

  const weekIndex = Number(week);
  const days: Day[] = meso.weeks[weekIndex]?.days ?? [];

  const onDragEnd = (result: DropResult) => {
    const { source, destination } = result;
    // Days are fixed; only reordering within the same day is allowed.
    if (!destination || destination.droppableId !== source.droppableId) return;
    if (destination.index === source.index) return;

    const day = days.find((d) => String(d.id) === source.droppableId);
    if (!day) return;

    const ordered = [...day.exercises].sort((a, b) => (a.position ?? 0) - (b.position ?? 0));
    const [moved] = ordered.splice(source.index, 1);
    ordered.splice(destination.index, 0, moved);
    reorder.mutate(ordered.map((ex, i) => ({ id: ex.id, position: i + 1 })));
  };

  const saveRename = () =>
    updateMeso.mutate(
      { id: meso.id, body: { id: meso.id, name: newName.trim() } },
      {
        onSuccess: () => {
          notifications.show({ message: 'Renamed.', color: 'green' });
          renameHandlers.close();
        },
      },
    );

  return (
    <Stack>
      <Group justify="space-between" align="flex-start">
        <div>
          <Group gap="xs">
            <Title order={2}>{meso.name}</Title>
            <ActionIcon
              variant="subtle"
              onClick={() => {
                setNewName(meso.name);
                renameHandlers.open();
              }}
              aria-label="Rename"
            >
              <IconPencil size={18} />
            </ActionIcon>
          </Group>
          <Text size="sm" c="dimmed">
            {meso.weeks.length} weeks · {meso.unit}
          </Text>
        </div>
        <SegmentedControl
          value={week}
          onChange={setWeek}
          data={meso.weeks.map((_, i) => ({ value: String(i), label: `Week ${i + 1}` }))}
        />
      </Group>

      <Modal opened={renameOpen} onClose={renameHandlers.close} title="Rename mesocycle">
        <Stack>
          <TextInput value={newName} onChange={(e) => setNewName(e.currentTarget.value)} data-autofocus />
          <Group justify="flex-end">
            <Button variant="default" onClick={renameHandlers.close}>
              Cancel
            </Button>
            <Button onClick={saveRename} loading={updateMeso.isPending} disabled={!newName.trim()}>
              Save
            </Button>
          </Group>
        </Stack>
      </Modal>

      <DragDropContext onDragEnd={onDragEnd}>
        {/* Native overflow (not Mantine ScrollArea): the DnD library tracks scroll on real
            overflow containers, and a custom viewport breaks drop positioning. */}
        <Box style={{ overflowX: 'auto' }}>
          <Group align="flex-start" wrap="nowrap" gap="md" pb="sm">
            {days.map((day) => {
              const ordered = [...day.exercises].sort((a, b) => (a.position ?? 0) - (b.position ?? 0));
              return (
                <Card key={day.id} withBorder padding="sm" w={300} style={{ flexShrink: 0 }}>
                  <Group justify="space-between" mb="xs">
                    <Text fw={600}>{day.label || `Day ${day.position}`}</Text>
                    {day.finishedAt && (
                      <Badge color="green" variant="light" size="sm">
                        done
                      </Badge>
                    )}
                  </Group>

                  <Droppable droppableId={String(day.id)}>
                    {(dropProvided) => (
                      <Stack gap={6} ref={dropProvided.innerRef} {...dropProvided.droppableProps} mih={20}>
                        {ordered.map((ex, index) => {
                          const catalog = ex.exerciseId != null ? exById.get(ex.exerciseId) : undefined;
                          const mg = ex.muscleGroupId != null ? mgName.get(ex.muscleGroupId) : undefined;
                          return (
                            <Draggable key={ex.id} draggableId={String(ex.id)} index={index}>
                              {(dragProvided, snapshot) => (
                                <Card
                                  withBorder
                                  padding="xs"
                                  radius="sm"
                                  ref={dragProvided.innerRef}
                                  {...dragProvided.draggableProps}
                                  style={{
                                    ...dragProvided.draggableProps.style,
                                    opacity: snapshot.isDragging ? 0.85 : 1,
                                  }}
                                >
                                  <Group gap={6} wrap="nowrap" justify="space-between">
                                    <Group gap={6} wrap="nowrap" style={{ minWidth: 0 }}>
                                      <Box {...dragProvided.dragHandleProps} style={{ cursor: 'grab' }}>
                                        <IconGripVertical size={14} opacity={0.5} />
                                      </Box>
                                      <div style={{ minWidth: 0 }}>
                                        <Text size="xs" fw={700} c={muscleColor(mg)}>
                                          ||| {mg ?? '—'}
                                        </Text>
                                        <Text size="sm" truncate>
                                          {catalog?.name ?? `Exercise ${ex.exerciseId}`}
                                        </Text>
                                      </div>
                                    </Group>
                                    <ActionIcon
                                      size="sm"
                                      variant="subtle"
                                      color="gray"
                                      onClick={() => remove.mutate(ex.id)}
                                      aria-label="Remove exercise"
                                    >
                                      <IconTrash size={14} />
                                    </ActionIcon>
                                  </Group>
                                </Card>
                              )}
                            </Draggable>
                          );
                        })}
                        {dropProvided.placeholder}
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
                    onChange={(v) => {
                      if (!v) return;
                      const cat = exById.get(Number(v));
                      if (!cat?.muscleGroupId) return;
                      add.mutate({
                        dayId: day.id,
                        exerciseId: Number(v),
                        position: ordered.length + 1,
                        muscleGroupId: cat.muscleGroupId,
                      });
                    }}
                  />
                </Card>
              );
            })}
          </Group>
        </Box>
      </DragDropContext>
    </Stack>
  );
}
