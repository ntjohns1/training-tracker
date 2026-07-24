import { Stack, Text, Title } from '@mantine/core';
import { PageContainer, PageHeader } from '../components/PageContainer';

export function ProfilePage() {
  return (
    <PageContainer>
      <PageHeader>
        <Title order={2}>Profile & Settings</Title>
      </PageHeader>
      <Stack>
        <Text c="dimmed">
          Running against the dev-profile stub user — real Keycloak login is a later enhancement.
        </Text>
      </Stack>
    </PageContainer>
  );
}
