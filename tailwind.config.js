/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/main/resources/templates/**/*.html",
    "./src/main/resources/templates/**/*.svg",
  ],
  darkMode: "class",
  theme: {
    extend: {
      colors: {
        "light-bg-primary": "hsl(0, 0%, 100%)",
        "light-bg-secondary": "hsl(0, 0%, 97%)",
        "light-action-primary": "hsl(214, 32%, 91%)",
        "light-action-secondary": "hsl(210, 38%, 95%)",
        "light-fg-primary": "hsl(218, 23%, 23%)",
        "light-fg-secondary": "hsl(216, 15%, 52%)",
        "light-fg-tertiary": "hsl(214, 20%, 69%)",
        "dark-bg-primary": "hsl(218, 23%, 23%)",
        "dark-bg-secondary": "hsl(220, 26%, 14%)",
        "dark-action-primary": "hsl(216, 15%, 52%)",
        "dark-action-secondary": "hsl(218, 23%, 23%)",
        "dark-fg-primary": "hsl(210, 38%, 95%)",
        "dark-fg-secondary": "hsl(211, 25%, 84%)",
        "dark-fg-tertiary": "hsl(214, 20%, 69%)",
        accent: "hsl(229, 76%, 66%)",
      },
      fontFamily: {
        inter: ["Inter", "sans-serif"],
      },
    },
  },
  plugins: [],
};
