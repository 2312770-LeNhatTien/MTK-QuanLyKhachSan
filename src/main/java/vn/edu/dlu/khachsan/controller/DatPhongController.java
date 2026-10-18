package vn.edu.dlu.khachsan.controller;
import java.util.*;
import java.time.LocalDate;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.dlu.khachsan.dto.*;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.service.*;
@Controller
@RequestMapping("/dat-phong")
public class DatPhongController {
    private final PhongService phongService;
    private final DatPhongService datPhongService;
    public DatPhongController(PhongService p,DatPhongService d){phongService=p;datPhongService=d;}
    @GetMapping("/lich-su")
    public String lichSu(@RequestParam(required=false) vn.edu.dlu.khachsan.entity.TrangThaiDatPhong trangThai,Authentication a,Model m){
        m.addAttribute("dsDon",datPhongService.lichSu(a.getName(),trangThai));
        m.addAttribute("trangThai",trangThai);m.addAttribute("dsTrangThai",vn.edu.dlu.khachsan.entity.TrangThaiDatPhong.values());
        return "dat-phong/lich-su";
    }
    @GetMapping("/{ma}")
    public String chiTiet(@PathVariable String ma,Authentication a,Model m){
        m.addAttribute("ct",datPhongService.chiTietCuaKhach(ma,a.getName()));return "dat-phong/chi-tiet";
    }
    @PostMapping("/{ma}/huy")
    public String huy(@PathVariable String ma,Authentication a,RedirectAttributes r){
        try{boolean daTra=datPhongService.huyDon(ma,a.getName());r.addFlashAttribute("thanhCong",daTra?"Đã hủy đơn. Khách sạn sẽ liên hệ với bạn về việc hoàn tiền.":"Đã hủy đơn đặt phòng.");}
        catch(NghiepVuException ex){r.addFlashAttribute("thongBaoLoi",ex.getMessage());}
        return "redirect:/dat-phong/"+ma;
    }
    @GetMapping("/tim-kiem")
    public String timKiem(@Valid @ModelAttribute TimPhongDto timPhongDto,BindingResult loi,Model m){
        m.addAttribute("dsLoaiPhong",phongService.dsLoaiPhong());
        m.addAttribute("ketQua",loi.hasErrors()?List.of():phongService.timPhong(timPhongDto));m.addAttribute("quanTri",false);
        return "dat-phong/tim-kiem";
    }
    @GetMapping("/xac-nhan")
    public String xacNhan(@RequestParam LocalDate ngayNhanPhong,@RequestParam LocalDate ngayTraPhong,
            @RequestParam List<String> maPhong,@RequestParam(defaultValue="1") int soKhach,Authentication a,Model m){
        DatPhongDto dto=new DatPhongDto();dto.setNgayNhanPhong(ngayNhanPhong);dto.setNgayTraPhong(ngayTraPhong);
        maPhong.forEach(ma->dto.getPhong().put(ma,soKhach));
        var don=datPhongService.tamTinh(a.getName(),dto);
        m.addAttribute("datPhongDto",dto);m.addAttribute("don",don);
        m.addAttribute("chiPhi",datPhongService.chiPhi(don));
        m.addAttribute("dsDichVu",datPhongService.dsDichVu());return "dat-phong/xac-nhan";
    }
    @PostMapping("/tam-tinh")
    @ResponseBody
    public Map<String,Object> tamTinh(@Valid @RequestBody DatPhongDto dto,Authentication a){
        return datPhongService.tamTinhHienThi(a.getName(),dto);
    }
    @PostMapping("/xac-nhan")
    public String taoDon(@Valid @ModelAttribute DatPhongDto dto,BindingResult loi,Authentication a,RedirectAttributes r){
        if(loi.hasErrors())r.addFlashAttribute("thongBaoLoi",loi.getAllErrors().getFirst().getDefaultMessage());
        else try{return "redirect:/thanh-toan/"+datPhongService.taoDonTam(a.getName(),dto).getMaDatPhong();}
        catch(NghiepVuException ex){r.addFlashAttribute("thongBaoLoi",ex.getMessage());}
        r.addAttribute("ngayNhanPhong",dto.getNgayNhanPhong());r.addAttribute("ngayTraPhong",dto.getNgayTraPhong());
        return "redirect:/dat-phong/tim-kiem";
    }
}

