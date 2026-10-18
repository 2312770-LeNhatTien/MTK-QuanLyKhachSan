package vn.edu.dlu.khachsan.controller;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.dlu.khachsan.dto.DanhGiaDto;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.service.*;
@Controller
public class DanhGiaController {
 private final DatPhongService datPhongService;private final DanhGiaService danhGiaService;
 public DanhGiaController(DatPhongService d,DanhGiaService dg){datPhongService=d;danhGiaService=dg;}
 @GetMapping("/danh-gia/{ma}")
 public String form(@PathVariable String ma,Authentication a,Model m){
  var ct=datPhongService.chiTietCuaKhach(ma,a.getName());
  if(!ct.coTheDanhGia())throw new NghiepVuException("Đơn này chưa hoàn tất hoặc đã được đánh giá.");
  m.addAttribute("ct",ct);m.addAttribute("danhGiaDto",new DanhGiaDto());return "dat-phong/danh-gia";
 }
 @PostMapping("/danh-gia/{ma}")
 public String luu(@PathVariable String ma,@Valid @ModelAttribute DanhGiaDto dto,BindingResult loi,Authentication a,Model m,RedirectAttributes r){
  if(!loi.hasErrors())try{danhGiaService.danhGia(ma,a.getName(),dto);r.addFlashAttribute("thanhCong","Cảm ơn bạn đã đánh giá.");return "redirect:/dat-phong/"+ma;}
   catch(NghiepVuException ex){m.addAttribute("thongBaoLoi",ex.getMessage());}
  m.addAttribute("ct",datPhongService.chiTietCuaKhach(ma,a.getName()));return "dat-phong/danh-gia";
 }
}
