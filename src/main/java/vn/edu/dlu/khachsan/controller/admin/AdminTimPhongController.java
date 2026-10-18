package vn.edu.dlu.khachsan.controller.admin;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.edu.dlu.khachsan.dto.TimPhongDto;
import vn.edu.dlu.khachsan.service.PhongService;
@Controller
public class AdminTimPhongController {
    private final PhongService phongService;
    public AdminTimPhongController(PhongService p){phongService=p;}
    @GetMapping("/admin/tim-phong")
    public String timPhong(@Valid @ModelAttribute TimPhongDto timPhongDto,BindingResult loi,Model m){
        m.addAttribute("quanTri",true);m.addAttribute("dsLoaiPhong",phongService.dsLoaiPhong());
        m.addAttribute("ketQua",loi.hasErrors()?List.of():phongService.timPhong(timPhongDto));return "dat-phong/tim-kiem";
    }
}

