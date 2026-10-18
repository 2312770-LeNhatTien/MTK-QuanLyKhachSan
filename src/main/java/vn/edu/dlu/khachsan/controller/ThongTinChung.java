package vn.edu.dlu.khachsan.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.edu.dlu.khachsan.security.TaiKhoanDangNhap;

@ControllerAdvice
public class ThongTinChung {

    @ModelAttribute("tenNguoiDung")
    public String tenNguoiDung(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof TaiKhoanDangNhap taiKhoan) {
            return taiKhoan.getHoTen();
        }
        return authentication == null ? "" : authentication.getName();
    }
}
