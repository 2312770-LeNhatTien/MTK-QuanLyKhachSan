package vn.edu.dlu.khachsan.controller;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.dlu.khachsan.dto.ThanhToanDto;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.service.*;
@Controller
@RequestMapping("/thanh-toan")
public class ThanhToanController {
    private final DatPhongService datPhongService;
    private final ThanhToanService thanhToanService;
    public ThanhToanController(DatPhongService d,ThanhToanService t){datPhongService=d;thanhToanService=t;}
    @GetMapping("/{ma}")
    public String thanhToan(@PathVariable String ma,Authentication a,Model m){
        var ct=datPhongService.chiTietCuaKhach(ma,a.getName());
        m.addAttribute("ct",ct);m.addAttribute("dsPhuongThuc",thanhToanService.phuongThucTrucTuyen());
        m.addAttribute("cocToiThieu",thanhToanService.cocToiThieu(ct.don().getTongTien()));
        m.addAttribute("thanhToanDto",new ThanhToanDto());return "thanh-toan/thanh-toan";
    }
    @PostMapping("/{ma}")
    public String thanhToan(@PathVariable String ma,@Valid @ModelAttribute ThanhToanDto dto,BindingResult loi,
            Authentication a,RedirectAttributes r){
        if(loi.hasErrors())r.addFlashAttribute("thongBaoLoi",loi.getAllErrors().getFirst().getDefaultMessage());
        else try{return "redirect:"+thanhToanService.batDauTrucTuyen(ma,a.getName(),dto).urlChuyenHuong();}
        catch(NghiepVuException ex){r.addFlashAttribute("thongBaoLoi",ex.getMessage());}
        return "redirect:/thanh-toan/"+ma;
    }
    @GetMapping("/gia-lap/{ma}")
    public String giaLap(@PathVariable String ma,Authentication a,Model m){
        m.addAttribute("giaoDich",thanhToanService.giaoDichCuaKhach(ma,a.getName()));return "thanh-toan/gia-lap";
    }
    @PostMapping("/gia-lap/{ma}")
    public String xacNhan(@PathVariable String ma,@RequestParam boolean thanhCong,Authentication a){
        thanhToanService.xacNhanTrucTuyen(ma,thanhCong,a.getName());return "redirect:/thanh-toan/ket-qua/"+ma;
    }
    @GetMapping("/ket-qua/{ma}")
    public String ketQua(@PathVariable String ma,Authentication a,Model m){
        m.addAttribute("giaoDich",thanhToanService.giaoDichCuaKhach(ma,a.getName()));return "thanh-toan/ket-qua";
    }
}

