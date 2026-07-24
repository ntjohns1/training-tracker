import dayjs from 'dayjs';

/** Total days across the meso ÷ weeks = days per week (RP shows "5 WEEKS · 4 DAYS/WEEK"). */
export function daysPerWeek(days: number | null, weeks: number | null): number | null {
  if (!days || !weeks) return null;
  return Math.round(days / weeks);
}

export function formatDate(iso: string | null | undefined): string {
  return iso ? dayjs(iso).format('MMM D, YYYY') : '';
}

/** A meso is "current" if it exists and has not been finished. */
export function isCurrent(finishedAt: string | null | undefined): boolean {
  return !finishedAt;
}
