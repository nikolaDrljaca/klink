import { defineConfig } from "vite";
import path from "path";
import solidPlugin from "vite-plugin-solid";
// import devtools from 'solid-devtools/vite';

export default defineConfig({
  plugins: [
    /*
        Uncomment the following line to enable solid-devtools.
        For more info see https://github.com/thetarnav/solid-devtools/tree/main/packages/extension#readme
        */
    // devtools(),
    solidPlugin(),
  ],
  server: {
    port: 3000,
    proxy: {
      // Requests from the LOCAL vite server to localhost:3000/api will be proxied to localhost:8080
      // avoiding a CORS issue
      // "/api": "http://localhost:8080",
      "/api": {
        target: "http://localhost:8080",
        configure: (proxy, options) => {
          proxy.on("proxyReq", (proxyReq, req, res) => {
            proxyReq.setHeader(
              "X-Api-Key",
              "f6add007-0f29-4a8e-ba15-320545b7a6c3",
            );
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
