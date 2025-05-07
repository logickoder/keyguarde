import type { Config } from 'tailwindcss';
import typography from '@tailwindcss/typography';

export default {
    content: ['./src/**/*.{js,jsx,ts,tsx}'],
    theme: {
        extend: {
            colors: {
                primary: '#1F4D7A',       // Deep blue for brand highlights
                secondary: '#7AB2E2',     // Light accent blue
                background: '#F8FAFC',    // Soft background
                surface: '#FFFFFF',       // Cards and base surface
                muted: '#94A3B8',         // For subtitles and placeholders
                danger: '#EF4444',        // For alerts (if needed)
                success: '#10B981',       // For confirmations
            },
            fontFamily: {
                sans: ['Inter', 'sans-serif'],
            },
        },
    },
    plugins: [
        typography,
    ],
} satisfies Config;
