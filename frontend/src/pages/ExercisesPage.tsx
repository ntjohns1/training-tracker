import { Loader, Stack, Table, Text, TextInput, Title } from '@mantine/core';
import { useMemo, useState } from 'react';
import { useBootstrap } from '../api/hooks';

// Read-only exercise library. Filter/polish arrives in E5; this is a functional first cut.
export function ExercisesPage() {
  const { data, isLoading } = useBootstrap();
  const [q, setQ] = useState('');

  const mgById = useMemo(
    () => new Map((data?.muscleGroups ?? []).map((mg) => [mg.id, mg.name])),
    [data],
  );
  const rows = useMemo(() => {
    const term = q.trim().toLowerCase();
    return (data?.exercises ?? [])
      .filter((e) => !e.deletedAt)
      .filter((e) => !term || e.name.toLowerCase().includes(term))
      .sort((a, b) => a.name.localeCompare(b.name));
  }, [data, q]);

  if (isLoading) return <Loader />;

  return (
    <Stack>
      <Title order={2}>Exercises</Title>
      <TextInput
        placeholder="Search exercises"
        value={q}
        onChange={(e) => setQ(e.currentTarget.value)}
      />
      <Text size="sm" c="dimmed">
        {rows.length} exercises
      </Text>
      <Table striped highlightOnHover>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>Name</Table.Th>
            <Table.Th>Muscle group</Table.Th>
            <Table.Th>Type</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {rows.map((e) => (
            <Table.Tr key={e.id}>
              <Table.Td>{e.name}</Table.Td>
              <Table.Td>{e.muscleGroupId ? mgById.get(e.muscleGroupId) ?? '—' : '—'}</Table.Td>
              <Table.Td>{e.exerciseType ?? '—'}</Table.Td>
            </Table.Tr>
          ))}
        </Table.Tbody>
      </Table>
    </Stack>
  );
}
