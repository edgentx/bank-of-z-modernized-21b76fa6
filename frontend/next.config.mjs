/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  poweredByHeader: false,
  // S-40: emit a self-contained `.next/standalone` bundle (server.js + the
  // minimal node_modules subset) so the Docker runtime image doesn't need to
  // ship full node_modules. nginx fronts this server in the container.
  output: 'standalone',
  experimental: {
    typedRoutes: true,
  },
  env: {
    NEXT_PUBLIC_APP_NAME: process.env.NEXT_PUBLIC_APP_NAME ?? 'Bank-of-Z Teller',
  },
};

export default nextConfig;
