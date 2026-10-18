package vn.edu.dlu.khachsan.controller.admin;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.service.NhanTraPhongService;
@Controller @RequestMapping("/admin/dat-phong/can-xac-nhan")
public class NhanPhuTrachController {
 private final NhanTraPhongService service;public NhanPhuTrachController(NhanTraPhongService s){service=s;}
 @GetMapping public String danhSach(Model m){m.addAttribute("dsDon",service.canNhanPhuTrach());return "admin/can-xac-nhan";}
 @PostMapping("/{ma}") public String nhan(@PathVariable String ma,Authentication a,RedirectAttributes r){
  try{service.nhanPhuTrach(ma,a.getName());r.addFlashAttribute("thanhCong","Đã nhận phụ trách đơn.");}catch(NghiepVuException ex){r.addFlashAttribute("thongBaoLoi",ex.getMessage());}return "redirect:/admin/dat-phong/can-xac-nhan";
 }
}
