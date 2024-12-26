import { defineConfig } from 'vite';
import path from 'path';
import solidPlugin from 'vite-plugin-solid';
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
            '/api': 'http://localhost:8080',
            '/ws': 'ws://localhost:8081'
        }
    },
    build: {
        target: 'esnext',
    },
    resolve: {
        alias: {
            '~': path.resolve(__dirname, 'src')
        }
    }
});
