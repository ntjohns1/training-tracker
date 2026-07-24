import { Badge, Button, Card, Group, Loader, Stack, Text, Title } from '@mantine/core';
import { IconPlus } from '@tabler/icons-react';
import { useNavigate } from 'react-router-dom';
import { useBootstrap } from '../api/hooks';
import { daysPerWeek, formatDate, isCurrent } from '../lib/format';

export function MesocyclesPage() {
  const { data, isLoading, isError, error } = useBootstrap();
  const navigate = useNavigate();

  if (isLoading) return <Loader />;
  if (isError)
    return <Text c="red">Failed to load mesocycles: {(error as Error).message}</Text>;

  const mesocycles = (data?.mesocycles ?? []).filter((m) => !m.deletedAt);

  return (
    <Stack>
      <Group justify="space-between">
        <Title order={2}>Mesocycles</Title>
        <Button leftSection={<IconPlus size={16} />} onClick={() => navigate('/mesocycles/new')}>
          New mesocycle
        </Button>
      </Group>
      {mesocycles.length === 0 && <Text c="dimmed">No mesocycles yet.</Text>}
      <Stack gap="sm">
        {mesocycles.map((m) => {
          const dpw = daysPerWeek(m.days, m.weeks);
          const current = isCurrent(m.finishedAt);
          return (
            <Card
              key={m.id}
              withBorder
              padding="md"
              onClick={() => navigate(`/mesocycles/${m.id}`)}
              style={{ cursor: 'pointer' }}
            >
              <Group justify="space-between" wrap="nowrap">
                <div>
                  <Text fw={600}>{m.name}</Text>
                  <Text size="sm" c="dimmed">
                    {m.weeks ?? '?'} WEEKS
                    {dpw ? ` · ${dpw} DAYS/WEEK` : ''}
                  </Text>
                </div>
                {current ? (
                  <Badge color="red" variant="light">
                    CURRENT
                  </Badge>
                ) : (
                  m.finishedAt && (
                    <Badge color="green" variant="light">
                      {formatDate(m.finishedAt)}
                    </Badge>
                  )
                )}
              </Group>
            </Card>
          );
        })}
      </Stack>
    </Stack>
  );
}
