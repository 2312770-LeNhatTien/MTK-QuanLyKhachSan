package vn.edu.dlu.khachsan.controller.admin;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.dlu.khachsan.dto.ThuTienDto;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.pattern.strategy.DuLieuThanhToan;
import vn.edu.dlu.khachsan.service.*;
@Controller @RequestMapping("/admin/nhan-tra-phong")
public class NhanTraPhongController {
 private final NhanTraPhongService nhanTra;private final ThanhToanService thanhToan;
 public NhanTraPhongController(NhanTraPhongService n,ThanhToanService t){nhanTra=n;thanhToan=t;}
 @GetMapping({"/check-in","/check-out"})
 public String form(jakarta.servlet.http.HttpServletRequest req,@RequestParam(required=false) String tuKhoa,Model m){
  boolean tra=req.getRequestURI().endsWith("check-out");m.addAttribute("traPhong",tra);m.addAttribute("tuKhoa",tuKhoa);
  if(tuKhoa!=null&&!tuKhoa.isBlank())try{m.addAttribute("ct",nhanTra.traCuu(tuKhoa,tra));}catch(NghiepVuException ex){m.addAttribute("thongBaoLoi",ex.getMessage());}
  if(!m.containsAttribute("thuTienDto"))m.addAttribute("thuTienDto",new ThuTienDto());
  m.addAttribute("phuongThuc",thanhToan.phuongThucTaiQuay());return "admin/nhan-tra-phong";
 }
 @PostMapping("/check-in/{ma}") public String nhan(@PathVariable String ma,RedirectAttributes r){
  try{nhanTra.checkIn(ma);r.addFlashAttribute("thanhCong","Check-in thành công.");}catch(NghiepVuException ex){r.addFlashAttribute("thongBaoLoi",ex.getMessage());}
  return "redirect:/admin/nhan-tra-phong/check-in?tuKhoa="+ma;
 }
 @PostMapping("/check-out/{ma}/thu-tien") public String thu(@PathVariable String ma,@Valid @ModelAttribute ThuTienDto dto,BindingResult loi,RedirectAttributes r){
  if(loi.hasErrors())r.addFlashAttribute("thongBaoLoi",loi.getAllErrors().getFirst().getDefaultMessage());
  else try{var ct=nhanTra.traCuu(ma,false);if(ct.hoaDon()==null)throw new NghiepVuException("Đơn chưa có hóa đơn.");
   var k=thanhToan.thanhToan(ct.hoaDon().getMaHoaDon(),ct.conLai(),dto.getMaPhuongThuc(),new DuLieuThanhToan(dto.getTienKhachDua(),dto.getMaChuanChi()));r.addFlashAttribute("thanhCong",k.thongBao());
  }catch(NghiepVuException ex){r.addFlashAttribute("thongBaoLoi",ex.getMessage());}
  return "redirect:/admin/nhan-tra-phong/check-out?tuKhoa="+ma;
 }
 @PostMapping("/check-out/{ma}") public String tra(@PathVariable String ma,RedirectAttributes r){
  try{return "redirect:/admin/hoa-don/"+nhanTra.checkOut(ma);}catch(NghiepVuException ex){r.addFlashAttribute("thongBaoLoi",ex.getMessage());return "redirect:/admin/nhan-tra-phong/check-out?tuKhoa="+ma;}
 }
}
