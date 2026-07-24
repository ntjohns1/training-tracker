import { AppShell, Group, NavLink, ScrollArea, Text, UnstyledButton } from '@mantine/core';
import { useMediaQuery } from '@mantine/hooks';
import {
  IconCalendarEvent,
  IconFolder,
  IconBarbell,
  IconUser,
} from '@tabler/icons-react';
import { NavLink as RouterLink, Outlet, useLocation, useNavigate } from 'react-router-dom';

const NAV = [
  { to: '/', label: 'Current workout', short: 'Workout', icon: IconCalendarEvent, end: true },
  { to: '/mesocycles', label: 'Mesocycles', short: 'Mesos', icon: IconFolder, end: false },
  { to: '/exercises', label: 'Exercises', short: 'Exercises', icon: IconBarbell, end: false },
  { to: '/profile', label: 'Profile & Settings', short: 'Profile', icon: IconUser, end: false },
];

function isActive(pathname: string, to: string, end: boolean) {
  return end ? pathname === to : pathname.startsWith(to);
}

export function RootLayout() {
  const location = useLocation();
  const navigate = useNavigate();
  const isMobile = useMediaQuery('(max-width: 48em)');

  return (
    <AppShell
      header={{ height: 56 }}
      navbar={{ width: 260, breakpoint: 'sm', collapsed: { mobile: true, desktop: false } }}
      footer={{ height: isMobile ? 60 : 0 }}
      padding="md"
    >
      <AppShell.Header>
        <Group h="100%" px="md">
          <Text fw={800} size="lg" c="red.5" style={{ letterSpacing: '0.5px' }}>
            TRAINING TRACKER
          </Text>
        </Group>
      </AppShell.Header>

      <AppShell.Navbar p="xs">
        <ScrollArea>
          {NAV.map((item) => (
            <NavLink
              key={item.to}
              component={RouterLink}
              to={item.to}
              label={item.label}
              leftSection={<item.icon size={18} stroke={1.5} />}
              active={isActive(location.pathname, item.to, item.end)}
            />
          ))}
        </ScrollArea>
      </AppShell.Navbar>

      <AppShell.Main>
        <Outlet />
      </AppShell.Main>

      {isMobile && (
        <AppShell.Footer>
          <Group h="100%" gap={0} grow>
            {NAV.map((item) => {
              const active = isActive(location.pathname, item.to, item.end);
              return (
                <UnstyledButton
                  key={item.to}
                  onClick={() => navigate(item.to)}
                  style={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: 2,
                    height: '100%',
                    color: active ? 'var(--mantine-color-red-5)' : 'var(--mantine-color-dimmed)',
                  }}
                >
                  <item.icon size={20} stroke={1.6} />
                  <Text size="10px">{item.short}</Text>
                </UnstyledButton>
              );
            })}
          </Group>
        </AppShell.Footer>
      )}
    </AppShell>
  );
}
