package vn.edu.dlu.khachsan.controller.admin;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.dlu.khachsan.dto.ThamSoDto;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.service.ThamSoService;
@Controller @RequestMapping("/admin/tham-so")
public class ThamSoController {
 private final ThamSoService s;public ThamSoController(ThamSoService t){s=t;}
 @GetMapping public String danhSach(Model m){m.addAttribute("dsThamSo",s.layTatCa());return "admin/tham-so";}
 @PostMapping("/{ma}") public String luu(@PathVariable String ma,@Valid @ModelAttribute ThamSoDto dto,BindingResult loi,RedirectAttributes r){
  if(loi.hasErrors())r.addFlashAttribute("thongBaoLoi",loi.getAllErrors().getFirst().getDefaultMessage());else try{s.capNhat(ma,dto.giaTri());r.addFlashAttribute("thanhCong","Tham số đã có hiệu lực.");}catch(NghiepVuException e){r.addFlashAttribute("thongBaoLoi",e.getMessage());}return "redirect:/admin/tham-so";
 }
}
