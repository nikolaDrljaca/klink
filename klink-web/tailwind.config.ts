import type { Config } from 'tailwindcss';

const config: Config = {
    content: [
        './index.html',
        './src/**/*.{js,ts,jsx,tsx,css,md,mdx,html,json,scss}',
    ],
    darkMode: 'class',
    theme: {
        extend: {
            fontFamily: {
                inter: ["Inter", "sans-serif"]
            }
        },
    },
    plugins: [
        require('daisyui')
    ],
};

export default config;
