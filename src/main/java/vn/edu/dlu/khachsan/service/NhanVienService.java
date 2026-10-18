package vn.edu.dlu.khachsan.service;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.dlu.khachsan.dto.*;
import vn.edu.dlu.khachsan.entity.*;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.repository.*;
@Service @Transactional(readOnly=true)
public class NhanVienService {
 private final NhanVienRepository nv;private final VaiTroRepository vaiTro;private final DatPhongRepository don;private final MaSinhService ma;private final PasswordEncoder encoder;
 public NhanVienService(NhanVienRepository n,VaiTroRepository v,DatPhongRepository d,MaSinhService m,PasswordEncoder e){nv=n;vaiTro=v;don=d;ma=m;encoder=e;}
 public List<NhanVien> danhSach(){return nv.findAllByOrderByMaNVAsc();}
 public List<VaiTro> vaiTro(){return vaiTro.findAll(org.springframework.data.domain.Sort.by("maVaiTro"));}
 public NhanVien lay(String id){NhanVien n=nv.findById(id).orElseThrow(()->new NghiepVuException("Không tìm thấy nhân viên."));n.getVaiTro().getTenVaiTro();return n;}
 private String bam(String mk){if(mk==null||mk.length()<6||mk.getBytes(java.nio.charset.StandardCharsets.UTF_8).length>72)throw new NghiepVuException("Mật khẩu cần ít nhất 6 ký tự, tối đa 72 byte UTF-8.");return encoder.encode(mk);}
 @Transactional public void luu(String id,NhanVienDto dto,String matKhau){
  ma.khoaGiaoDich();String email=dto.email().trim().toLowerCase(Locale.ROOT);
  if(nv.existsByEmailAndMaNVNot(email,id==null?"":id))throw new NghiepVuException("Email nhân viên đã tồn tại.");
  NhanVien n=id==null?new NhanVien():lay(id);if(id==null){n.setMaNV(ma.sinhMaNhanVien());n.setMatKhau(bam(matKhau));}
  n.setHoTen(dto.hoTen().trim());n.setEmail(email);n.setSdt(dto.sdt());n.setVaiTro(vaiTro.findById(dto.maVaiTro()).orElseThrow(()->new NghiepVuException("Vai trò không hợp lệ.")));nv.saveAndFlush(n);
 }
 @Transactional public void datLaiMatKhau(String id,String mk){lay(id).setMatKhau(bam(mk));}
 @Transactional public void xoa(String id,String emailDangNhap){
  NhanVien n=lay(id);if(n.getEmail().equalsIgnoreCase(emailDangNhap))throw new NghiepVuException("Bạn không thể tự xóa tài khoản đang đăng nhập.");
  if(don.existsByNhanVienMaNV(id))throw new NghiepVuException("Không thể xóa nhân viên đã phụ trách đơn.");nv.delete(n);nv.flush();
 }
}
