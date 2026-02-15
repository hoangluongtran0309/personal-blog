"use strict";

const nav = document.querySelector(".mobile-nav");
const navMenuBtn = document.querySelector(".nav-menu-btn");
const navCloseBtn = document.querySelector(".nav-close-btn");

const navToggleFunc = function () {
  nav.classList.toggle("translate-x-0");
  nav.classList.toggle("translate-x-full");
};

navMenuBtn.addEventListener("click", navToggleFunc);
navCloseBtn.addEventListener("click", navToggleFunc);

const themeBtnMobile = document.querySelector(".theme-btn-mobile");
const themeBtnDesktop = document.querySelector(".theme-btn-desktop");

const togglePublicTheme = function () {
  document.documentElement.classList.toggle("dark");

  if (document.documentElement.classList.contains("dark")) {
    localStorage.setItem("publicTheme", "dark");
  } else {
    localStorage.setItem("publicTheme", "light");
  }
};

if (themeBtnMobile) {
  themeBtnMobile.addEventListener("click", togglePublicTheme);
}

if (themeBtnDesktop) {
  themeBtnDesktop.addEventListener("click", togglePublicTheme);
}

const savedTheme = localStorage.getItem("publicTheme");
if (savedTheme === "dark") {
  document.documentElement.classList.add("dark");
} else {
  document.documentElement.classList.remove("dark");
}
