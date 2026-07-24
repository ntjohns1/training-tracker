import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { VitePWA } from 'vite-plugin-pwa';

// Dev: proxy /api to the Spring backend (run with the `dev` profile — permissive security,
// stub user, no Keycloak). See application-dev.yml.
export default defineConfig({
  plugins: [
    react(),
    VitePWA({
      registerType: 'autoUpdate',
      manifest: {
        name: 'Training Tracker',
        short_name: 'Training',
        theme_color: '#e03131',
        background_color: '#1a1b1e',
        display: 'standalone',
      },
    }),
  ],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        // Override with BACKEND_PORT when 8080 is taken (e.g. Jenkins).
        target: `http://localhost:${process.env.BACKEND_PORT ?? '8080'}`,
        changeOrigin: true,
      },
    },
  },
});
