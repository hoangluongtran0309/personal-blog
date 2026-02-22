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

function previewAvatar(event) {
  const file = event.target.files[0];
  if (!file) return;

  const reader = new FileReader();
  reader.onload = function (e) {
    document.getElementById("avatarPreview").src = e.target.result;
  };
  reader.readAsDataURL(file);
}

function togglePassword(inputId, btn) {
  const input = document.getElementById(inputId);
  const eyeIcon = btn.querySelector(".eye-icon");
  const eyeOffIcon = btn.querySelector(".eye-off-icon");
  if (input.type === "password") {
    input.type = "text";
    eyeIcon.classList.add("hidden");
    eyeOffIcon.classList.remove("hidden");
  } else {
    input.type = "password";
    eyeIcon.classList.remove("hidden");
    eyeOffIcon.classList.add("hidden");
  }
}

function toggleUserDropdown() {
  var menu = document.getElementById("userDropdownMenu");
  var chevron = document.getElementById("userDropdownChevron");
  var isHidden = menu.style.display === "none" || menu.style.display === "";
  menu.style.display = isHidden ? "block" : "none";
  chevron.style.transform = isHidden ? "rotate(180deg)" : "rotate(0deg)";
}

document.addEventListener("click", function (e) {
  var wrapper = document.getElementById("userDropdownWrapper");
  var menu = document.getElementById("userDropdownMenu");
  var chevron = document.getElementById("userDropdownChevron");
  if (wrapper && !wrapper.contains(e.target)) {
    menu.style.display = "none";
    chevron.style.transform = "rotate(0deg)";
  }
});
