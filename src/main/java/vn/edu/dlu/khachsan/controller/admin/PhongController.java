package vn.edu.dlu.khachsan.controller.admin;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.dlu.khachsan.dto.*;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.pattern.state.ThaoTacPhong;
import vn.edu.dlu.khachsan.service.PhongService;
@Controller @RequestMapping("/admin/phong")
public class PhongController {
 private final PhongService s;public PhongController(PhongService p){s=p;}
 @GetMapping public String danhSach(Model m){m.addAttribute("dsPhong",s.dsPhong());m.addAttribute("dsLoai",s.dsLoaiPhong());return "admin/phong/danh-sach";}
 @GetMapping("/tinh-trang") public String tinhTrang(Model m){m.addAttribute("theoTang",s.theoTang());m.addAttribute("canDon",s.canDon());
  m.addAttribute("bieuTuong",java.util.Map.of("TRONG","bi-door-open","CO_KHACH","bi-person-fill","DANG_DON","bi-bucket","BAO_TRI","bi-tools"));
  m.addAttribute("tenThaoTac",java.util.Map.of("DON_XONG","Dọn xong","BAO_TRI","Bảo trì","KET_THUC_BAO_TRI","Kết thúc bảo trì"));return "admin/phong/tinh-trang";}
 @PostMapping("/tinh-trang/{ma}") public String thaoTac(@PathVariable String ma,@RequestParam ThaoTacPhong thaoTac,RedirectAttributes r){try{s.thaoTac(ma,thaoTac);r.addFlashAttribute("thanhCong","Đã cập nhật tình trạng phòng.");}catch(NghiepVuException e){r.addFlashAttribute("thongBaoLoi",e.getMessage());}return "redirect:/admin/phong/tinh-trang";}
 @GetMapping({"/them","/{ma}/sua"}) public String formPhong(@PathVariable(required=false) String ma,Model m){
  PhongDto dto=new PhongDto();if(ma!=null){var p=s.layPhong(ma);dto=new PhongDto(p.getSoPhong(),p.getTang(),p.getLoaiPhong().getMaLoaiPhong());}
  m.addAttribute("phongDto",dto);return moPhong(ma,m);
 }
 private String moPhong(String ma,Model m){m.addAttribute("ma",ma);m.addAttribute("dsLoai",s.dsLoaiPhong());return "admin/phong/form-phong";}
 @PostMapping({"/them","/{ma}/sua"}) public String luuPhong(@PathVariable(required=false) String ma,@Valid @ModelAttribute PhongDto dto,BindingResult loi,Model m,RedirectAttributes r){
  if(!loi.hasErrors())try{s.luuPhong(ma,dto);r.addFlashAttribute("thanhCong","Đã lưu phòng.");return "redirect:/admin/phong";}catch(NghiepVuException e){m.addAttribute("thongBaoLoi",e.getMessage());}return moPhong(ma,m);
 }
 @GetMapping({"/loai/them","/loai/{ma}/sua"}) public String formLoai(@PathVariable(required=false) String ma,Model m){
  LoaiPhongDto dto=new LoaiPhongDto();if(ma!=null){var l=s.layLoai(ma);dto=new LoaiPhongDto(l.getTenLoaiPhong(),l.getGiaMoiDem(),l.getSucChua(),l.getMoTa());}m.addAttribute("loaiPhongDto",dto);m.addAttribute("ma",ma);return "admin/phong/form-loai";
 }
 @PostMapping({"/loai/them","/loai/{ma}/sua"}) public String luuLoai(@PathVariable(required=false) String ma,@Valid @ModelAttribute LoaiPhongDto dto,BindingResult loi,Model m,RedirectAttributes r){
  if(!loi.hasErrors())try{s.luuLoai(ma,dto);r.addFlashAttribute("thanhCong","Đã lưu loại phòng.");return "redirect:/admin/phong";}catch(NghiepVuException e){m.addAttribute("thongBaoLoi",e.getMessage());}m.addAttribute("ma",ma);return "admin/phong/form-loai";
 }
 @PostMapping("/{ma}/xoa") public String xoaPhong(@PathVariable String ma,RedirectAttributes r){try{s.xoaPhong(ma);r.addFlashAttribute("thanhCong","Đã xóa phòng.");}catch(NghiepVuException e){r.addFlashAttribute("thongBaoLoi",e.getMessage());}return "redirect:/admin/phong";}
 @PostMapping("/loai/{ma}/xoa") public String xoaLoai(@PathVariable String ma,RedirectAttributes r){try{s.xoaLoai(ma);r.addFlashAttribute("thanhCong","Đã xóa loại phòng.");}catch(NghiepVuException e){r.addFlashAttribute("thongBaoLoi",e.getMessage());}return "redirect:/admin/phong";}
}
