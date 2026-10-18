package vn.edu.dlu.khachsan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import vn.edu.dlu.khachsan.dto.TimPhongDto;
import vn.edu.dlu.khachsan.service.PhongService;

@Controller
public class TrangChuController {
    private final PhongService phongService;
    private final vn.edu.dlu.khachsan.service.DatPhongService datPhongService;
    private final vn.edu.dlu.khachsan.service.DanhGiaService danhGiaService;
    public TrangChuController(PhongService p,vn.edu.dlu.khachsan.service.DatPhongService d,vn.edu.dlu.khachsan.service.DanhGiaService dg){phongService=p;datPhongService=d;danhGiaService=dg;}

    @GetMapping("/")
    public String trangChu(Model model) {
        model.addAttribute("timPhongDto",new TimPhongDto());
        model.addAttribute("dsLoaiPhong",phongService.dsLoaiPhong());
        model.addAttribute("dsDichVu",datPhongService.dsDichVu());
        model.addAttribute("dsDanhGia",danhGiaService.moiNhat());
        return "trang-chu";
    }
}
