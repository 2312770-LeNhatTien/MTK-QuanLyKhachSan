package vn.edu.dlu.khachsan.controller.admin;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.edu.dlu.khachsan.dto.KhoangNgayDto;
import vn.edu.dlu.khachsan.service.BaoCaoService;
@Controller @RequestMapping("/admin/bao-cao")
public class BaoCaoController {
 private final BaoCaoService s;public BaoCaoController(BaoCaoService b){s=b;}
 @GetMapping public String xem(@Valid @ModelAttribute KhoangNgayDto dto,BindingResult loi,Model m){
  if(!loi.hasErrors())m.addAttribute("bc",s.baoCao(dto.getTuNgay(),dto.getDenNgay()));return "admin/bao-cao";
 }
}
