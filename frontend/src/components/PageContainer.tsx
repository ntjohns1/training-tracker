import { Box } from '@mantine/core';
import type { ReactNode } from 'react';
import classes from './PageContainer.module.css';

/**
 * Main-content column: max 736px, min 556px on desktop, 16px horizontal padding, centred.
 * Board views (meso builder / saved-meso board) deliberately do NOT use this - they scroll
 * horizontally and need the full available width.
 */
export function PageContainer({ children }: { children: ReactNode }) {
  return <Box className={classes.container}>{children}</Box>;
}

/** Page header strip inside the content column: 60px tall (min), 16px padding all round. */
export function PageHeader({
  children,
  right,
}: {
  children: ReactNode;
  right?: ReactNode;
}) {
  return (
    <Box className={classes.header}>
      <Box style={{ minWidth: 0 }}>{children}</Box>
      {right ? <Box style={{ flexShrink: 0 }}>{right}</Box> : null}
    </Box>
  );
}
