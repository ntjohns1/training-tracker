import { Stack, Text, Title } from '@mantine/core';

// E2 will render the current day (exercises grouped by muscle group, per-set weight/reps/LOG).
export function CurrentWorkoutPage() {
  return (
    <Stack>
      <Title order={2}>Current workout</Title>
      <Text c="dimmed">Day view + set logging — coming in the next step (E2).</Text>
    </Stack>
  );
}
