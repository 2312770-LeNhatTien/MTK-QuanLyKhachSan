package vn.edu.dlu.khachsan.integration;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.dlu.khachsan.dto.*;
import vn.edu.dlu.khachsan.entity.*;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.pattern.strategy.DuLieuThanhToan;
import vn.edu.dlu.khachsan.repository.*;
import vn.edu.dlu.khachsan.service.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:mysql://localhost:3307/quan_ly_khach_san_kiem_thu?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Ho_Chi_Minh","app.email-log-dir=.tools/email-kiem-thu","app.scheduler.enabled=false"})
@AutoConfigureMockMvc
class Buoc8LeTanIT {
 @Autowired NhanTraPhongService service;@Autowired ThanhToanService thanhToan;@Autowired DatPhongService don;
 @Autowired DatPhongRepository dp;@Autowired PhongRepository phong;@Autowired MockMvc mvc;@Autowired PlatformTransactionManager tm;
 @Test @Transactional void checkInDonMauVaNhanPhuTrach(){
  var d=dp.findById("DP00000002").orElseThrow();d.setNgayNhanPhong(LocalDate.now());d.setNgayTraPhong(LocalDate.now().plusDays(2));
  assertThat(service.canNhanPhuTrach()).extracting(DatPhong::getMaDatPhong).contains(d.getMaDatPhong());
  service.nhanPhuTrach(d.getMaDatPhong(),"letan@khachsan.vn");assertThat(d.getNhanVien().getMaNV()).isEqualTo("NV0001");
  service.checkIn(d.getMaDatPhong());assertThat(phong.findById("P201").orElseThrow().getTrangThai()).isEqualTo("CO_KHACH");
 }
 @Test @Transactional void thuTienMatDuHienTienThoiVaHoanTat(){
  var ct=service.traCuu("202",true);assertThat(ct.don().getMaDatPhong()).isEqualTo("DP00000003");
  assertThatThrownBy(()->service.checkOut(ct.don().getMaDatPhong())).isInstanceOf(NghiepVuException.class);
  var k=thanhToan.thanhToan(ct.hoaDon().getMaHoaDon(),ct.conLai(),"TIEN_MAT",new DuLieuThanhToan(ct.conLai().add(new BigDecimal("70000")),null));
  assertThat(k.thongBao()).contains("70.000 đ");String ma=service.checkOut(ct.don().getMaDatPhong());
  assertThat(service.hoaDon(ma).conLai()).isEqualByComparingTo("0");assertThat(ct.don().getTrangThai()).isEqualTo(TrangThaiDatPhong.HOAN_TAT);
  assertThat(phong.findById("P202").orElseThrow().getTrangThai()).isEqualTo("DANG_DON");
 }
 @Test @Transactional void thieuTienVaMaTheSaiBiTuChoi(){
  var ct=service.traCuu("DP00000003",false);
  assertThatThrownBy(()->thanhToan.thanhToan(ct.hoaDon().getMaHoaDon(),ct.conLai(),"TIEN_MAT",new DuLieuThanhToan(BigDecimal.ONE,null))).isInstanceOf(NghiepVuException.class).hasMessageContaining("không đủ");
  assertThatThrownBy(()->thanhToan.thanhToan(ct.hoaDon().getMaHoaDon(),ct.conLai(),"THE",new DuLieuThanhToan(null,"abc"))).isInstanceOf(NghiepVuException.class).hasMessageContaining("6 chữ số");
 }
 @Test @Transactional void thuTheVaTuChoiThuLap(){
  var ct=service.traCuu("DP00000003",false);thanhToan.thanhToan(ct.hoaDon().getMaHoaDon(),ct.conLai(),"THE",new DuLieuThanhToan(null,"123456"));
  assertThatThrownBy(()->thanhToan.thanhToan(ct.hoaDon().getMaHoaDon(),ct.conLai(),"THE",new DuLieuThanhToan(null,"123456"))).isInstanceOf(NghiepVuException.class);
 }
 @Test @Transactional void cacTrangLeTanRender() throws Exception{
  for(String url:new String[]{"/admin/dat-phong/can-xac-nhan","/admin/nhan-tra-phong/check-in?tuKhoa=DP00000002","/admin/nhan-tra-phong/check-out?tuKhoa=202","/admin/hoa-don/HD00000003"})
   mvc.perform(get(url).with(user("letan@khachsan.vn").roles("VT01"))).andExpect(status().isOk());
 }
 @Test void checkInNhieuPhongLoiPhaiRollbackTatCa(){
  var tx=new TransactionTemplate(tm);
  String ma=tx.execute(s->{var dto=new DatPhongDto();dto.setNgayNhanPhong(LocalDate.now());dto.setNgayTraPhong(LocalDate.now().plusDays(1));dto.getPhong().put("P103",1);dto.getPhong().put("P104",1);dto.setDongYQuyDinh(true);var d=don.taoDonTam("khach@gmail.com",dto);d.setTrangThai(TrangThaiDatPhong.DA_XAC_NHAN);return d.getMaDatPhong();});
  try{assertThatThrownBy(()->service.checkIn(ma)).isInstanceOf(NghiepVuException.class).hasMessage("Phòng 104 đang ở trạng thái Đang dọn, không thể check-in.");
   String trangThai=tx.execute(s->phong.findById("P103").orElseThrow().getTrangThai());assertThat(trangThai).isEqualTo("TRONG");
  }finally{tx.executeWithoutResult(s->dp.findById(ma).orElseThrow().setTrangThai(TrangThaiDatPhong.DA_HUY));}
 }
}

