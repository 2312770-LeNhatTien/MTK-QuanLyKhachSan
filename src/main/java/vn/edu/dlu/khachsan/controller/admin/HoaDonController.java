package vn.edu.dlu.khachsan.controller.admin;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.dlu.khachsan.service.NhanTraPhongService;
@Controller @RequestMapping("/admin/hoa-don")
public class HoaDonController {
 private final NhanTraPhongService service;public HoaDonController(NhanTraPhongService s){service=s;}
 @GetMapping("/{ma}") public String xem(@PathVariable String ma,Model m){m.addAttribute("ct",service.hoaDon(ma));return "admin/hoa-don";}
}
