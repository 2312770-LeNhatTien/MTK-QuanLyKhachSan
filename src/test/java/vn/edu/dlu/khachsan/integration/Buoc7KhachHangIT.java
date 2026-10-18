package vn.edu.dlu.khachsan.integration;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.access.AccessDeniedException;
import vn.edu.dlu.khachsan.dto.*;
import vn.edu.dlu.khachsan.entity.*;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.repository.*;
import vn.edu.dlu.khachsan.service.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:mysql://localhost:3307/quan_ly_khach_san_kiem_thu?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Ho_Chi_Minh","app.email-log-dir=.tools/email-kiem-thu","app.scheduler.enabled=false"})
@AutoConfigureMockMvc @Transactional
class Buoc7KhachHangIT {
 @Autowired DatPhongService don; @Autowired DanhGiaService danhGia; @Autowired DatPhongRepository repo;
 @Autowired DanhGiaRepository dgRepo; @Autowired MockMvc mvc;
 @Test void huyDonChoVaKhongDuocHuyDonNguoiKhac(){
  DatPhongDto d=new DatPhongDto();d.setNgayNhanPhong(LocalDate.now().plusDays(180));d.setNgayTraPhong(d.getNgayNhanPhong().plusDays(1));d.getPhong().put("P103",1);d.setDongYQuyDinh(true);
  var dp=don.taoDonTam("khach@gmail.com",d);
  assertThatThrownBy(()->don.huyDon(dp.getMaDatPhong(),"nguoi-khac@gmail.com")).isInstanceOf(AccessDeniedException.class);
  assertThat(don.huyDon(dp.getMaDatPhong(),"khach@gmail.com")).isFalse();assertThat(dp.getTrangThai()).isEqualTo(TrangThaiDatPhong.DA_HUY);
 }
 @Test void hanHuyDonDaCocDungNgayBien(){
  var d=repo.findById("DP00000002").orElseThrow();d.setNgayNhanPhong(LocalDate.now().plusDays(1));
  assertThat(don.coTheHuy(d)).isTrue();d.setNgayNhanPhong(LocalDate.now());assertThat(don.coTheHuy(d)).isFalse();
  d.setNgayNhanPhong(LocalDate.now().plusDays(1));assertThat(don.huyDon(d.getMaDatPhong(),"khach@gmail.com")).isTrue();
 }
 @Test void danhGiaDungChuHoanTatMotLanVaHienTrangChu() throws Exception {
  var dto=new DanhGiaDto();dto.setSoSao(5);dto.setNoiDung("Kỳ nghỉ rất thoải mái.");
  danhGia.danhGia("DP00000001","khach@gmail.com",dto);
  assertThat(dgRepo.existsByDatPhongMaDatPhong("DP00000001")).isTrue();
  mvc.perform(get("/")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("Kỳ nghỉ rất thoải mái.")));
  assertThatThrownBy(()->danhGia.danhGia("DP00000001","khach@gmail.com",dto)).isInstanceOf(NghiepVuException.class);
 }
 @Test void tuChoiDanhGiaSaiDonSaiSao(){
  var dto=new DanhGiaDto();assertThatThrownBy(()->danhGia.danhGia("DP00000002","khach@gmail.com",dto)).isInstanceOf(NghiepVuException.class);
  assertThatThrownBy(()->danhGia.danhGia("DP00000001","khac@gmail.com",dto)).isInstanceOf(AccessDeniedException.class);
  dto.setSoSao(6);assertThatThrownBy(()->danhGia.danhGia("DP00000001","khach@gmail.com",dto)).isInstanceOf(NghiepVuException.class);
 }
 @Test void cacTrangVaPhanQuyenDuLieu() throws Exception {
  for(String url:new String[]{"/dat-phong/lich-su","/dat-phong/DP00000001","/dat-phong/DP00000002","/danh-gia/DP00000001"})
   mvc.perform(get(url).with(user("khach@gmail.com").roles("KHACH"))).andExpect(status().isOk());
  mvc.perform(get("/dat-phong/lich-su").param("trangThai","DA_HUY").with(user("khach@gmail.com").roles("KHACH"))).andExpect(status().isOk());
  mvc.perform(get("/dat-phong/DP00000001").with(user("khac@gmail.com").roles("KHACH"))).andExpect(status().isForbidden());
 }
}
