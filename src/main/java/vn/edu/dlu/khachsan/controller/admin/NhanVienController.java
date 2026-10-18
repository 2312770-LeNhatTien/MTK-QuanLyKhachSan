package vn.edu.dlu.khachsan.controller.admin;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.dlu.khachsan.dto.*;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.service.NhanVienService;
@Controller @RequestMapping("/admin/nhan-vien")
public class NhanVienController {
 private final NhanVienService s;public NhanVienController(NhanVienService v){s=v;}
 @GetMapping public String danhSach(Model m){m.addAttribute("dsNhanVien",s.danhSach());return "admin/nhan-vien/danh-sach";}
 private String mo(String ma,Model m){m.addAttribute("ma",ma);m.addAttribute("dsVaiTro",s.vaiTro());if(!m.containsAttribute("matKhauMoiDto"))m.addAttribute("matKhauMoiDto",new MatKhauMoiDto());return "admin/nhan-vien/form";}
 @GetMapping({"/them","/{ma}/sua"}) public String form(@PathVariable(required=false) String ma,Model m){NhanVienDto d=new NhanVienDto();if(ma!=null){var n=s.lay(ma);d=new NhanVienDto(n.getHoTen(),n.getEmail(),n.getSdt(),n.getVaiTro().getMaVaiTro());}m.addAttribute("nhanVienDto",d);return mo(ma,m);}
 @PostMapping({"/them","/{ma}/sua"}) public String luu(@PathVariable(required=false) String ma,@Valid @ModelAttribute NhanVienDto dto,BindingResult loi,@RequestParam(required=false) String matKhau,Model m,RedirectAttributes r){
  if(!loi.hasErrors())try{s.luu(ma,dto,matKhau);r.addFlashAttribute("thanhCong","Đã lưu nhân viên.");return "redirect:/admin/nhan-vien";}catch(NghiepVuException e){m.addAttribute("thongBaoLoi",e.getMessage());}return mo(ma,m);
 }
 @PostMapping("/{ma}/mat-khau") public String matKhau(@PathVariable String ma,@Valid @ModelAttribute MatKhauMoiDto dto,BindingResult loi,RedirectAttributes r){
  if(loi.hasErrors())r.addFlashAttribute("thongBaoLoi",loi.getAllErrors().getFirst().getDefaultMessage());else try{s.datLaiMatKhau(ma,dto.matKhau());r.addFlashAttribute("thanhCong","Đã đặt lại mật khẩu.");}catch(NghiepVuException e){r.addFlashAttribute("thongBaoLoi",e.getMessage());}return "redirect:/admin/nhan-vien/"+ma+"/sua";
 }
 @PostMapping("/{ma}/xoa") public String xoa(@PathVariable String ma,Authentication a,RedirectAttributes r){try{s.xoa(ma,a.getName());r.addFlashAttribute("thanhCong","Đã xóa nhân viên.");}catch(NghiepVuException e){r.addFlashAttribute("thongBaoLoi",e.getMessage());}return "redirect:/admin/nhan-vien";}
}
