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
      includeAssets: ['icon.svg'],
      manifest: {
        name: 'Training Tracker',
        short_name: 'Training',
        description: 'Mesocycle-based hypertrophy training tracker',
        theme_color: '#e03131',
        background_color: '#1a1b1e',
        display: 'standalone',
        start_url: '/',
        icons: [
          { src: 'icon.svg', sizes: 'any', type: 'image/svg+xml', purpose: 'any' },
          { src: 'icon.svg', sizes: 'any', type: 'image/svg+xml', purpose: 'maskable' },
        ],
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
