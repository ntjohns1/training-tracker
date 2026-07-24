import { createTheme } from '@mantine/core';

// RP Hypertrophy reads as a dark Mantine app with a red accent. Functional parity, not pixel-perfect.
export const theme = createTheme({
  primaryColor: 'red',
  defaultRadius: 'md',
  fontFamily:
    '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif',
});
