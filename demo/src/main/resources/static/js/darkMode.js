document.addEventListener("DOMContentLoaded", function () {
  const themeSwitch = document.getElementById("themeSwitch");
  const htmlEl = document.documentElement;
  const saved = localStorage.getItem("theme") || "light";
  htmlEl.setAttribute("data-bs-theme", saved);
  if (themeSwitch) themeSwitch.checked = saved === "dark";

  themeSwitch?.addEventListener("change", function () {
    const theme = this.checked ? "dark" : "light";
    htmlEl.setAttribute("data-bs-theme", theme);
    localStorage.setItem("theme", theme);
  });
});