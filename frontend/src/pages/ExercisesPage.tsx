import {
  Badge,
  Group,
  Loader,
  Select,
  Table,
  Text,
  TextInput,
  Title,
} from '@mantine/core';
import { useMemo, useState } from 'react';
import { useBootstrap } from '../api/hooks';
import { PageContainer, PageHeader } from '../components/PageContainer';
import { muscleColor } from '../lib/muscleColors';

// Read-only exercise library with search + muscle-group and equipment filters.
export function ExercisesPage() {
  const { data, isLoading } = useBootstrap();
  const [q, setQ] = useState('');
  const [mg, setMg] = useState<string | null>(null);
  const [type, setType] = useState<string | null>(null);

  const mgById = useMemo(
    () => new Map((data?.muscleGroups ?? []).map((m) => [m.id, m.name])),
    [data],
  );
  const mgOptions = useMemo(
    () =>
      (data?.muscleGroups ?? [])
        .map((m) => ({ value: String(m.id), label: m.name }))
        .sort((a, b) => a.label.localeCompare(b.label)),
    [data],
  );
  const typeOptions = useMemo(() => {
    const types = new Set<string>();
    for (const e of data?.exercises ?? []) if (e.exerciseType) types.add(e.exerciseType);
    return Array.from(types)
      .sort()
      .map((t) => ({ value: t, label: t }));
  }, [data]);

  const rows = useMemo(() => {
    const term = q.trim().toLowerCase();
    return (data?.exercises ?? [])
      .filter((e) => !e.deletedAt)
      .filter((e) => !term || e.name.toLowerCase().includes(term))
      .filter((e) => !mg || String(e.muscleGroupId) === mg)
      .filter((e) => !type || e.exerciseType === type)
      .sort((a, b) => a.name.localeCompare(b.name));
  }, [data, q, mg, type]);

  if (isLoading) return <Loader />;

  return (
    <PageContainer>
      <PageHeader>
        <Title order={2}>Exercises</Title>
      </PageHeader>
      <Group align="flex-end" wrap="wrap">
        <TextInput
          label="Search"
          placeholder="Search exercises"
          value={q}
          onChange={(e) => setQ(e.currentTarget.value)}
          w={220}
        />
        <Select
          label="Muscle group"
          placeholder="All"
          data={mgOptions}
          value={mg}
          onChange={setMg}
          clearable
          w={180}
        />
        <Select
          label="Equipment"
          placeholder="All"
          data={typeOptions}
          value={type}
          onChange={setType}
          clearable
          w={180}
        />
      </Group>
      <Text size="sm" c="dimmed">
        {rows.length} exercises
      </Text>
      <Table.ScrollContainer minWidth={420}>
        <Table striped highlightOnHover>
          <Table.Thead>
            <Table.Tr>
              <Table.Th>Name</Table.Th>
              <Table.Th>Muscle group</Table.Th>
              <Table.Th>Equipment</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {rows.map((e) => {
              const name = e.muscleGroupId ? mgById.get(e.muscleGroupId) : undefined;
              return (
                <Table.Tr key={e.id}>
                  <Table.Td>{e.name}</Table.Td>
                  <Table.Td>
                    {name ? (
                      <Badge color={muscleColor(name)} variant="light" size="sm">
                        {name}
                      </Badge>
                    ) : (
                      '—'
                    )}
                  </Table.Td>
                  <Table.Td>{e.exerciseType ?? '—'}</Table.Td>
                </Table.Tr>
              );
            })}
          </Table.Tbody>
        </Table>
      </Table.ScrollContainer>
    </PageContainer>
  );
}
