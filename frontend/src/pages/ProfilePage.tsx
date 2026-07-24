import { Stack, Text, Title } from '@mantine/core';

export function ProfilePage() {
  return (
    <Stack>
      <Title order={2}>Profile & Settings</Title>
      <Text c="dimmed">
        Running against the dev-profile stub user — real Keycloak login is a later enhancement.
      </Text>
    </Stack>
  );
}
