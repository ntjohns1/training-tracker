import { AppShell, Burger, Group, NavLink, ScrollArea, Text } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import {
  IconCalendarEvent,
  IconFolder,
  IconBarbell,
  IconUser,
} from '@tabler/icons-react';
import { NavLink as RouterLink, Outlet, useLocation } from 'react-router-dom';

const NAV = [
  { to: '/', label: 'Current workout', icon: IconCalendarEvent, end: true },
  { to: '/mesocycles', label: 'Mesocycles', icon: IconFolder, end: false },
  { to: '/exercises', label: 'Exercises', icon: IconBarbell, end: false },
  { to: '/profile', label: 'Profile & Settings', icon: IconUser, end: false },
];

export function RootLayout() {
  const [opened, { toggle, close }] = useDisclosure();
  const location = useLocation();

  return (
    <AppShell
      header={{ height: 56 }}
      navbar={{ width: 260, breakpoint: 'sm', collapsed: { mobile: !opened } }}
      padding="md"
    >
      <AppShell.Header>
        <Group h="100%" px="md" gap="sm">
          <Burger opened={opened} onClick={toggle} hiddenFrom="sm" size="sm" />
          <Text fw={800} size="lg" c="red.5" style={{ letterSpacing: '0.5px' }}>
            TRAINING TRACKER
          </Text>
        </Group>
      </AppShell.Header>

      <AppShell.Navbar p="xs">
        <ScrollArea>
          {NAV.map((item) => {
            const active = item.end
              ? location.pathname === item.to
              : location.pathname.startsWith(item.to);
            return (
              <NavLink
                key={item.to}
                component={RouterLink}
                to={item.to}
                label={item.label}
                leftSection={<item.icon size={18} stroke={1.5} />}
                active={active}
                onClick={close}
              />
            );
          })}
        </ScrollArea>
      </AppShell.Navbar>

      <AppShell.Main>
        <Outlet />
      </AppShell.Main>
    </AppShell>
  );
}
