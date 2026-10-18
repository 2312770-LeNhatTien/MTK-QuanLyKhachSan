"use strict";
document.querySelectorAll('form[data-xac-nhan-huy]').forEach(form=>form.addEventListener('submit',event=>{
    if(!window.confirm('Bạn có chắc muốn hủy đơn đặt phòng này?'))event.preventDefault();
}));

document.querySelectorAll("form[data-kiem-tra]").forEach((form) => {
    form.addEventListener("invalid", (event) => {
        const input = event.target;
        if (input.validity.customError) return;
        if (input.validity.valueMissing) input.setCustomValidity("Vui lòng nhập thông tin này.");
        else if (input.validity.typeMismatch) input.setCustomValidity("Vui lòng nhập email đúng định dạng.");
        else if (input.validity.tooShort) input.setCustomValidity("Thông tin chưa đủ độ dài yêu cầu.");
        else if (input.validity.tooLong) input.setCustomValidity("Thông tin vượt quá độ dài cho phép.");
    }, true);
    form.querySelectorAll("input").forEach((input) => {
        input.addEventListener("input", () => input.setCustomValidity(""));
    });
    form.querySelectorAll("[data-xac-nhan]").forEach((xacNhan) => {
        const matKhau = document.getElementById(xacNhan.dataset.xacNhan);
        const kiemTra = () => xacNhan.setCustomValidity(
            xacNhan.value && xacNhan.value !== matKhau.value ? "Mật khẩu xác nhận không khớp." : "");
        xacNhan.addEventListener("input", kiemTra);
        matKhau.addEventListener("input", kiemTra);
    });
});

document.querySelectorAll(".sidebar-nav a").forEach((link) => {
    if (new URL(link.href).pathname === window.location.pathname) link.setAttribute("aria-current", "page");
});
document.querySelectorAll('form[data-xac-nhan-xoa]').forEach(form => form.addEventListener('submit',event => {
 if (!window.confirm('Bạn có chắc muốn xóa bản ghi này?')) event.preventDefault();
}));
