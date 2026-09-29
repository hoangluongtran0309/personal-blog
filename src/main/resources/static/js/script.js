"use strict";

// Progressive enhancement only: every page works without this file.

const nav = document.querySelector(".mobile-nav");
const navMenuBtn = document.querySelector(".nav-menu-btn");
const navCloseBtn = document.querySelector(".nav-close-btn");

// the client menu slides in from the right, the admin sidebar from the left
const hiddenClass = nav && nav.classList.contains("-translate-x-full") ? "-translate-x-full" : "translate-x-full";

const navToggleFunc = function () {
  nav.classList.toggle("translate-x-0");
  nav.classList.toggle(hiddenClass);
};

if (nav && navMenuBtn) {
  navMenuBtn.addEventListener("click", navToggleFunc);
}

if (nav && navCloseBtn) {
  navCloseBtn.addEventListener("click", navToggleFunc);
}

const toggleTheme = function () {
  const dark = document.documentElement.classList.toggle("dark");
  try {
    localStorage.setItem("theme", dark ? "dark" : "light");
  } catch (e) {
    // storage unavailable: the choice lasts for this page only
  }
};

document.querySelectorAll(".theme-btn-mobile, .theme-btn-desktop").forEach(function (button) {
  button.addEventListener("click", toggleTheme);
});
