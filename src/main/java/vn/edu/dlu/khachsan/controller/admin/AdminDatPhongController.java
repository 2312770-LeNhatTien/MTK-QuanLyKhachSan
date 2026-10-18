package vn.edu.dlu.khachsan.controller.admin;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.edu.dlu.khachsan.dto.LocDonDto;
import vn.edu.dlu.khachsan.entity.TrangThaiDatPhong;
import vn.edu.dlu.khachsan.service.DatPhongService;
@Controller @RequestMapping("/admin/dat-phong")
public class AdminDatPhongController {
 private final DatPhongService s;public AdminDatPhongController(DatPhongService d){s=d;}
 @GetMapping public String danhSach(@Valid @ModelAttribute LocDonDto dto,BindingResult loi,Model m){
  m.addAttribute("trangThai",TrangThaiDatPhong.values());m.addAttribute("dsDon",loi.hasErrors()?java.util.List.of():s.quanLy(dto.trangThai(),dto.tuNgay(),dto.denNgay(),dto.ma(),dto.email()));return "admin/dat-phong/danh-sach";
 }
 @GetMapping("/{ma}") public String chiTiet(@PathVariable String ma,Model m){m.addAttribute("ct",s.chiTietQuanTri(ma));return "admin/dat-phong/chi-tiet";}
}
