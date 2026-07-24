// Colored pills per muscle group (RP shows a colored bar + label). Mantine color names;
// falls back to a stable hash-picked color for anything not explicitly mapped.
const MAP: Record<string, string> = {
  chest: 'grape',
  back: 'cyan',
  shoulders: 'violet',
  biceps: 'teal',
  triceps: 'pink',
  quads: 'green',
  hamstrings: 'lime',
  glutes: 'orange',
  calves: 'indigo',
  traps: 'blue',
  forearms: 'yellow',
  abs: 'red',
};

const FALLBACK = ['blue', 'grape', 'teal', 'orange', 'cyan', 'pink', 'lime', 'violet'];

export function muscleColor(name: string | undefined | null): string {
  if (!name) return 'gray';
  const key = name.trim().toLowerCase();
  if (MAP[key]) return MAP[key];
  let h = 0;
  for (let i = 0; i < key.length; i++) h = (h * 31 + key.charCodeAt(i)) >>> 0;
  return FALLBACK[h % FALLBACK.length];
}
