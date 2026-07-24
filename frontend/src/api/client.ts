import axios from 'axios';

// Same-origin '/api' — Vite proxies to the backend in dev. In prod the SPA is served
// behind the same host, so the relative base works there too.
export const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});
