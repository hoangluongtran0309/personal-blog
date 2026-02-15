"use strict";

const adminSidebar = document.querySelector(".admin-sidebar");
const adminSidebarMenuBtn = document.querySelector(".admin-sidebar-menu-btn");
const adminSidebarCloseBtn = document.querySelector(".admin-sidebar-close-btn");

const toggleAdminSidebar = function () {
  adminSidebar.classList.toggle("translate-x-0");
  adminSidebar.classList.toggle("-translate-x-full");
};

adminSidebarMenuBtn.addEventListener("click", toggleAdminSidebar);
adminSidebarCloseBtn.addEventListener("click", toggleAdminSidebar);

const adminThemeBtnMobile = document.querySelector(".admin-theme-btn-mobile");
const adminThemeBtnDesktop = document.querySelector(".admin-theme-btn-desktop");

const toggleAdminTheme = function () {
  document.documentElement.classList.toggle("dark");

  if (document.documentElement.classList.contains("dark")) {
    localStorage.setItem("adminTheme", "dark");
  } else {
    localStorage.setItem("adminTheme", "light");
  }
};

if (adminThemeBtnMobile) {
  adminThemeBtnMobile.addEventListener("click", toggleAdminTheme);
}

if (adminThemeBtnDesktop) {
  adminThemeBtnDesktop.addEventListener("click", toggleAdminTheme);
}

const savedAdminTheme = localStorage.getItem("adminTheme");
if (savedAdminTheme === "dark") {
  document.documentElement.classList.add("dark");
} else {
  document.documentElement.classList.remove("dark");
}
