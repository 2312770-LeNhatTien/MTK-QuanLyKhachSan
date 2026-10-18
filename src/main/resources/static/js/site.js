"use strict";
// Ảnh giữ chỗ 1 px để tránh lỗi tải; thay bằng ảnh thật là tự hiển thị.
document.querySelectorAll("img[data-hotel-image]").forEach(img => {
    const media = img.closest("[data-hotel-media]");
    const hienThi = () => media.classList.toggle("has-image", img.naturalWidth > 1 && img.naturalHeight > 1);
    img.addEventListener("load", hienThi);
    img.addEventListener("error", () => media.classList.remove("has-image"));
    if (img.complete) hienThi();
});
document.querySelectorAll(".stay-header .nav-link").forEach(link => {
    const url = new URL(link.href);
    if (url.pathname === window.location.pathname && !url.hash) link.setAttribute("aria-current", "page");
});
