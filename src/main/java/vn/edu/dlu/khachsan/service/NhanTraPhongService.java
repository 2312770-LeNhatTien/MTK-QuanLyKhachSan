package vn.edu.dlu.khachsan.service;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import vn.edu.dlu.khachsan.dto.ChiTietDonDto;
import vn.edu.dlu.khachsan.entity.*;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.repository.*;
@Service @Transactional(readOnly=true)
public class NhanTraPhongService {
 private final DatPhongService don;private final DatPhongRepository dp;private final PhongRepository phong;
 private final HoaDonRepository hoaDon;private final NhanVienRepository nv;private final ThanhToanService thanhToan;
 public NhanTraPhongService(DatPhongService d,DatPhongRepository r,PhongRepository p,HoaDonRepository h,NhanVienRepository n,ThanhToanService t){don=d;dp=r;phong=p;hoaDon=h;nv=n;thanhToan=t;}
 public ChiTietDonDto traCuu(String tuKhoa,boolean choPhepSoPhong){
  String ma=tuKhoa.trim();
  if(choPhepSoPhong&&!ma.startsWith("DP")){
   var ds=dp.timDonDangO(ma,TrangThaiDatPhong.DA_XAC_NHAN);
   if(ds.size()!=1)throw new NghiepVuException("Không tìm thấy một đơn đang ở tương ứng với số phòng.");
   ma=ds.getFirst().getMaDatPhong();
  }
  return don.chiTietQuanTri(ma);
 }
 private void khoaPhong(DatPhong d){
  // Khóa phòng theo cùng thứ tự với lúc đặt phòng để tránh deadlock.
  d.getDsChiTietDatPhong().stream().map(c->c.getPhong().getMaPhong()).sorted().forEach(ma->phong.khoaTheoMa(ma).orElseThrow());
 }
 @Transactional public void checkIn(String ma){
  DatPhong d=don.khoaDon(ma);LocalDate homNay=LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
  if(d.getTrangThai()!=TrangThaiDatPhong.DA_XAC_NHAN||homNay.isBefore(d.getNgayNhanPhong())||!homNay.isBefore(d.getNgayTraPhong()))throw new NghiepVuException("Chỉ check-in đơn đã xác nhận trong khoảng ngày lưu trú.");
  khoaPhong(d);d.getDsChiTietDatPhong().forEach(c->c.getPhong().checkIn());
 }
 @Transactional public String checkOut(String ma){
  DatPhong d=don.khoaDon(ma);
  if(d.getTrangThai()!=TrangThaiDatPhong.DA_XAC_NHAN)throw new NghiepVuException("Chỉ trả phòng cho đơn đã check-in.");
  khoaPhong(d);
  HoaDon h=hoaDon.findByDatPhongMaDatPhong(ma).orElseThrow(()->new NghiepVuException("Đơn chưa có hóa đơn."));
  if(thanhToan.daTra(h).compareTo(h.getTongTien())<0)throw new NghiepVuException("Cần thu đủ số tiền còn lại trước khi trả phòng.");
  d.getDsChiTietDatPhong().forEach(c->c.getPhong().checkOut());
  thanhToan.capNhatHoaDon(h);d.setTrangThai(TrangThaiDatPhong.HOAN_TAT);return h.getMaHoaDon();
 }
 public ChiTietDonDto hoaDon(String ma){return don.chiTietQuanTri(hoaDon.findById(ma).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND)).getDatPhong().getMaDatPhong());}
 public List<DatPhong> canNhanPhuTrach(){
  LocalDate ngay=LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
  var ds=dp.findByTrangThaiAndNhanVienIsNullAndNgayNhanPhongBetweenOrderByNgayNhanPhongAsc(TrangThaiDatPhong.DA_XAC_NHAN,ngay,ngay.plusDays(6));ds.forEach(don::napChiTiet);return ds;
 }
 @Transactional public void nhanPhuTrach(String ma,String email){
  DatPhong d=don.khoaDon(ma);LocalDate ngay=LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
  if(d.getTrangThai()!=TrangThaiDatPhong.DA_XAC_NHAN||d.getNhanVien()!=null||d.getNgayNhanPhong().isBefore(ngay)||d.getNgayNhanPhong().isAfter(ngay.plusDays(6)))throw new NghiepVuException("Đơn không còn trong danh sách cần nhận phụ trách.");
  NhanVien n=nv.findByEmail(email).orElseThrow(()->new NghiepVuException("Không tìm thấy nhân viên."));
  if(!n.getVaiTro().getMaVaiTro().equals("VT01"))throw new org.springframework.security.access.AccessDeniedException("Chỉ lễ tân được nhận phụ trách.");
  d.setNhanVien(n);
 }
}
