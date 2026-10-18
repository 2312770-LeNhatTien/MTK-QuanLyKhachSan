package vn.edu.dlu.khachsan.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminTrangChuController {

    @GetMapping("/admin/dang-nhap")
    public String dangNhap() {
        return "admin/dang-nhap";
    }

    @GetMapping({"/admin", "/admin/"})
    public String trangChu() {
        return "admin/trang-chu";
    }
}
