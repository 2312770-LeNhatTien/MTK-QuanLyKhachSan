package vn.edu.dlu.khachsan.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import vn.edu.dlu.khachsan.dto.DangKyDto;
import vn.edu.dlu.khachsan.dto.DoiMatKhauDto;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.service.TaiKhoanService;

@Controller
public class TaiKhoanController {

    private final TaiKhoanService taiKhoanService;

    public TaiKhoanController(TaiKhoanService taiKhoanService) {
        this.taiKhoanService = taiKhoanService;
    }

    @GetMapping("/dang-nhap")
    public String dangNhap() {
        return "tai-khoan/dang-nhap";
    }

    @GetMapping("/dang-ky")
    public String dangKy(Model model) {
        model.addAttribute("dangKyDto", new DangKyDto());
        return "tai-khoan/dang-ky";
    }

    @PostMapping("/dang-ky")
    public String dangKy(@Valid @ModelAttribute DangKyDto dangKyDto, BindingResult loi, Model model) {
        if (!loi.hasErrors()) {
            try {
                taiKhoanService.dangKy(dangKyDto);
                return "redirect:/dang-nhap?dang-ky-thanh-cong";
            } catch (NghiepVuException ex) {
                model.addAttribute("thongBaoLoi", ex.getMessage());
            }
        }
        dangKyDto.setMatKhau(null);
        dangKyDto.setXacNhanMatKhau(null);
        return "tai-khoan/dang-ky";
    }

    @GetMapping("/tai-khoan/doi-mat-khau")
    public String doiMatKhau(Model model) {
        model.addAttribute("doiMatKhauDto", new DoiMatKhauDto());
        return "tai-khoan/doi-mat-khau";
    }

    @PostMapping("/tai-khoan/doi-mat-khau")
    public String doiMatKhau(@Valid @ModelAttribute DoiMatKhauDto doiMatKhauDto, BindingResult loi,
            Model model, Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        if (!loi.hasErrors()) {
            try {
                taiKhoanService.doiMatKhau(authentication.getName(), doiMatKhauDto);
                new SecurityContextLogoutHandler().logout(request, response, authentication);
                return "redirect:/dang-nhap?doi-mat-khau-thanh-cong";
            } catch (NghiepVuException ex) {
                model.addAttribute("thongBaoLoi", ex.getMessage());
            }
        }
        doiMatKhauDto.setMatKhauHienTai(null);
        doiMatKhauDto.setMatKhauMoi(null);
        doiMatKhauDto.setXacNhanMatKhau(null);
        return "tai-khoan/doi-mat-khau";
    }
}
