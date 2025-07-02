import type { Config } from 'tailwindcss';
import typography from '@tailwindcss/typography';

export default {
  content: ['./src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: '#3B82F6', // Blue 500
        'primary-container': '#E0F2FE', // Light Blue container
        'on-primary': '#FFFFFF',
        secondary: '#14B8A6', // Teal 500
        'secondary-container': '#CCFBF1', // Light teal container
        'on-secondary': '#FFFFFF',
        background: '#FFFFFF', // Clean white background
        surface: '#F8FAFC', // Subtle surface differentiation
        'on-background': '#1F2937', // Gray 800 for text
        'on-surface': '#1F2937',
        muted: '#6B7280', // Gray 500 for muted text
        danger: '#EF4444', // Red 500 for errors
        'danger-container': '#FEE2E2',
        'on-danger': '#FFFFFF',
        success: '#10B981' // Emerald 500 for success states
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif']
      },
      boxShadow: {
        soft: '0 2px 8px -2px rgba(0, 0, 0, 0.08)',
        medium: '0 4px 16px -4px rgba(0, 0, 0, 0.12)'
      },
      borderRadius: {
        xl: '12px',
        '2xl': '16px'
      }
    }
  },
  plugins: [typography]
} satisfies Config;
