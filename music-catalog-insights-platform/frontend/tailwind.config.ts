import type { Config } from "tailwindcss";

const config: Config = {
  darkMode: ["class"],
  content: [
    "./src/app/**/*.{ts,tsx}",
    "./src/components/**/*.{ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "#0F1115",
        surface: "#171A21",
        surfaceMuted: "#1E222B",
        border: "#2A2F3A",
        foreground: "#EDEEF2",
        muted: "#9096A4",
        primary: {
          DEFAULT: "#E8B34C",
          foreground: "#14161B",
        },
        accent: {
          DEFAULT: "#2DD4BF",
          foreground: "#0B1512",
        },
        danger: {
          DEFAULT: "#E5484D",
          foreground: "#FFFFFF",
        },
      },
      fontFamily: {
        display: ["var(--font-space-grotesk)", "sans-serif"],
        sans: ["var(--font-inter)", "sans-serif"],
      },
      borderRadius: {
        lg: "0.75rem",
        md: "0.5rem",
        sm: "0.375rem",
      },
      keyframes: {
        "fade-in": {
          "0%": { opacity: "0", transform: "translateY(4px)" },
          "100%": { opacity: "1", transform: "translateY(0)" },
        },
      },
      animation: {
        "fade-in": "fade-in 0.25s ease-out",
      },
    },
  },
  plugins: [require("tailwindcss-animate")],
};

export default config;
