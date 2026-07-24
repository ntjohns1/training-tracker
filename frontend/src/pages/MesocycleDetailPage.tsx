import {
  ActionIcon,
  Badge,
  Button,
  Card,
  Group,
  Loader,
  Modal,
  Stack,
  Text,
  TextInput,
  Title,
} from '@mantine/core';
import { IconPencil } from '@tabler/icons-react';
import { useDisclosure } from '@mantine/hooks';
import { notifications } from '@mantine/notifications';
import { useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useBootstrap, useMesocycle, useUpdateMeso } from '../api/hooks';
import { muscleColor } from '../lib/muscleColors';

export function MesocycleDetailPage() {
  const { id } = useParams();
  const mesoId = id ? Number(id) : undefined;
  const { data: meso, isLoading, isError, error } = useMesocycle(mesoId);
  const { data: boot } = useBootstrap();
  const navigate = useNavigate();
  const updateMeso = useUpdateMeso();
  const [renameOpen, renameHandlers] = useDisclosure(false);
  const [newName, setNewName] = useState('');

  const mgName = useMemo(
    () => new Map((boot?.muscleGroups ?? []).map((mg) => [mg.id, mg.name])),
    [boot],
  );

  if (isLoading) return <Loader />;
  if (isError) return <Text c="red">Failed to load: {(error as Error).message}</Text>;
  if (!meso) return null;

  const openRename = () => {
    setNewName(meso.name);
    renameHandlers.open();
  };
  const saveRename = () => {
    updateMeso.mutate(
      { id: meso.id, body: { id: meso.id, name: newName.trim() } },
      {
        onSuccess: () => {
          notifications.show({ message: 'Renamed.', color: 'green' });
          renameHandlers.close();
        },
        onError: (e) =>
          notifications.show({ message: `Rename failed: ${(e as Error).message}`, color: 'red' }),
      },
    );
  };

  return (
    <Stack>
      <Group gap="xs">
        <Title order={2}>{meso.name}</Title>
        <ActionIcon variant="subtle" onClick={openRename} aria-label="Rename">
          <IconPencil size={18} />
        </ActionIcon>
      </Group>
      <Text c="dimmed" size="sm">
        {meso.weeks.length} weeks · {meso.unit}
      </Text>

      <Modal opened={renameOpen} onClose={renameHandlers.close} title="Rename mesocycle">
        <Stack>
          <TextInput
            value={newName}
            onChange={(e) => setNewName(e.currentTarget.value)}
            data-autofocus
          />
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

      {meso.weeks.map((week, wi) => (
        <Stack key={wi} gap="xs">
          <Text fw={700} tt="uppercase" size="sm">
            Week {wi + 1}
          </Text>
          <Group align="stretch" wrap="wrap">
            {week.days.map((day) => {
              const groups = Array.from(
                new Set(
                  day.exercises
                    .map((e) => e.muscleGroupId)
                    .filter((x): x is number => x != null),
                ),
              );
              return (
                <Card
                  key={day.id}
                  withBorder
                  padding="sm"
                  w={220}
                  onClick={() => navigate(`/days/${day.id}`)}
                  style={{ cursor: 'pointer' }}
                >
                  <Text fw={600} size="sm">
                    {day.label || `Day ${day.position}`}
                  </Text>
                  <Group gap={4} mt="xs">
                    {groups.map((g) => (
                      <Badge key={g} size="xs" color={muscleColor(mgName.get(g))} variant="light">
                        {mgName.get(g) ?? g}
                      </Badge>
                    ))}
                  </Group>
                  {day.finishedAt && (
                    <Badge mt="xs" size="xs" color="green" variant="light">
                      done
                    </Badge>
                  )}
                </Card>
              );
            })}
          </Group>
        </Stack>
      ))}
    </Stack>
  );
}
