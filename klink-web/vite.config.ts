import { defineConfig } from "vite";
import path from "path";
import solidPlugin from "vite-plugin-solid";
import { VitePWA } from 'vite-plugin-pwa';
// import devtools from 'solid-devtools/vite';

export default defineConfig({
  plugins: [
    /*
        Uncomment the following line to enable solid-devtools.
        For more info see https://github.com/thetarnav/solid-devtools/tree/main/packages/extension#readme
        */
    // devtools(),
    solidPlugin(),
    VitePWA({
      strategies: 'generateSW',
      registerType: 'autoUpdate',
      injectRegister: 'auto',
      workbox: {
        globPatterns: ['**/*.{js,css,html,ico,png,ttf}'],
        runtimeCaching: []
      },
      manifest: false
    })
  ],
  server: {
    port: 3000,
    proxy: {
      // Requests from the LOCAL vite server to localhost:3000/api will be proxied to localhost:8080
      // avoiding a CORS issue
      "/api/events": {
        target: "ws://localhost:8080",
        ws: true,
      },
      "/api": {
        target: "http://localhost:8080",
        configure: (proxy, options) => {
          proxy.on("proxyReq", (proxyReq, req, res) => {
          });
        },
      },
    },
  },
  build: {
    target: "esnext",
  },
  resolve: {
    alias: {
      "~": path.resolve(__dirname, "src"),
    },
  },
});
