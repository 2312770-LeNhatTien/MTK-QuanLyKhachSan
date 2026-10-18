package vn.edu.dlu.khachsan.integration;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.dlu.khachsan.dto.*;
import vn.edu.dlu.khachsan.entity.*;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.pattern.state.ThaoTacPhong;
import vn.edu.dlu.khachsan.repository.*;
import vn.edu.dlu.khachsan.service.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:mysql://localhost:3307/quan_ly_khach_san_kiem_thu?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Ho_Chi_Minh","app.email-log-dir=.tools/email-kiem-thu","app.scheduler.enabled=false"})
@AutoConfigureMockMvc @Transactional
class Buoc9QuanLyIT {
 @Autowired PhongService phong;@Autowired NhanVienService nv;@Autowired DatPhongService don;@Autowired ThamSoService ts;@Autowired ThanhToanService thanhToan;@Autowired BaoCaoService baoCao;
 @Autowired PhongRepository pRepo;@Autowired LoaiPhongRepository lp;@Autowired NhanVienRepository nRepo;@Autowired ThanhToanRepository tt;@Autowired DatPhongRepository dp;
 @Autowired PasswordEncoder encoder;@Autowired MockMvc mvc;
 @Test void donXongBaoTriVaKhongChoBuongPhongCheckIn(){
  phong.thaoTac("P104",ThaoTacPhong.DON_XONG);assertThat(phong.layPhong("P104").getTrangThai()).isEqualTo("TRONG");
  phong.thaoTac("P104",ThaoTacPhong.BAO_TRI);assertThat(phong.layPhong("P104").layTrangThai().coTheNhanDat()).isFalse();
  phong.thaoTac("P104",ThaoTacPhong.KET_THUC_BAO_TRI);assertThat(phong.layPhong("P104").getTrangThai()).isEqualTo("TRONG");
  assertThatThrownBy(()->phong.thaoTac("P104",ThaoTacPhong.CHECK_IN)).isInstanceOf(NghiepVuException.class);
 }
 @Test void crudPhongLoaiVaChanXoaLichSu(){
  phong.luuLoai(null,new LoaiPhongDto("Loại kiểm thử",new BigDecimal("900000"),3,"Mô tả"));
  String ma=lp.findAll().stream().filter(l->l.getTenLoaiPhong().equals("Loại kiểm thử")).findFirst().orElseThrow().getMaLoaiPhong();
  phong.luuPhong(null,new PhongDto("999",9,ma));assertThat(pRepo.existsById("P999")).isTrue();
  phong.luuPhong("P999",new PhongDto("999",8,ma));assertThat(phong.layPhong("P999").getTang()).isEqualTo(8);
  assertThatThrownBy(()->phong.xoaLoai(ma)).isInstanceOf(NghiepVuException.class);
  phong.xoaPhong("P999");phong.xoaLoai(ma);assertThat(lp.existsById(ma)).isFalse();
  assertThatThrownBy(()->phong.xoaPhong("P201")).isInstanceOf(NghiepVuException.class).hasMessageContaining("Bảo trì");
 }
 @Test void doiGiaKhongDoiGiaDaChot(){
  var ct=don.chiTietQuanTri("DP00000002");BigDecimal cu=ct.chiPhi().tinhTien();
  phong.luuLoai("LP002",new LoaiPhongDto("Deluxe",new BigDecimal("999999"),2,"Đã cập nhật"));
  assertThat(don.chiTietQuanTri("DP00000002").chiPhi().tinhTien()).isEqualByComparingTo(cu);
 }
 @Test void crudNhanVienBamMatKhauVaChanXoa(){
  nv.luu(null,new NhanVienDto("Nhân viên thử","thu@khachsan.vn",null,"VT01"),"abcdef");
  var n=nRepo.findByEmail("thu@khachsan.vn").orElseThrow();assertThat(encoder.matches("abcdef",n.getMatKhau())).isTrue();
  nv.luu(n.getMaNV(),new NhanVienDto("Tên đã sửa","thu@khachsan.vn","0123456789","VT03"),null);
  nv.datLaiMatKhau(n.getMaNV(),"12345678");assertThat(encoder.matches("12345678",n.getMatKhau())).isTrue();nv.xoa(n.getMaNV(),"quanly@khachsan.vn");
  assertThatThrownBy(()->nv.xoa("NV0002","quanly@khachsan.vn")).isInstanceOf(NghiepVuException.class).hasMessageContaining("tự xóa");
  assertThatThrownBy(()->nv.xoa("NV0001","quanly@khachsan.vn")).isInstanceOf(NghiepVuException.class).hasMessageContaining("phụ trách");
 }
 @Test void thamSoCoHieuLucNgayQuaController() throws Exception {
  mvc.perform(post("/admin/tham-so/TI_LE_DAT_COC").with(user("quanly@khachsan.vn").roles("VT02")).with(csrf()).param("giaTri","50")).andExpect(status().is3xxRedirection());
  assertThat(thanhToan.cocToiThieu(new BigDecimal("1000000"))).isEqualByComparingTo("500000");
  assertThatThrownBy(()->ts.capNhat("TI_LE_DAT_COC",new BigDecimal("101"))).isInstanceOf(NghiepVuException.class);
 }
 @Test void baoCaoChiTinhThanhCongVaDemPhongGiaoKhoang(){
  LocalDate ngay=LocalDate.now().plusDays(250);int i=0;
  for(var t:tt.findAll()){t.setNgayThanhToan(ngay.atTime(12,0));t.setSoTien(new BigDecimal("10"));t.setTrangThai(i++==0?TrangThaiGiaoDich.THANH_CONG:TrangThaiGiaoDich.THAT_BAI);}
  var d1=dp.findById("DP00000001").orElseThrow();d1.setNgayNhanPhong(ngay.minusDays(1));d1.setNgayTraPhong(ngay.plusDays(1));
  for(String ma:List.of("DP00000002","DP00000003")){var d=dp.findById(ma).orElseThrow();d.setNgayNhanPhong(ngay);d.setNgayTraPhong(ngay.plusDays(2));}
  var bc=baoCao.baoCao(ngay,ngay);assertThat(bc.doanhThu()).isEqualByComparingTo("10");assertThat(bc.demPhong()).isEqualTo(3);assertThat(bc.sucChuaDemPhong()).isEqualTo(11);assertThat(bc.congSuat()).isEqualByComparingTo("27.27");
 }
 @Test void locDonTheoTrangThaiMaEmailVaNgay(){
  var d=dp.findById("DP00000001").orElseThrow();LocalDate n=d.getNgayDat().toLocalDate();
  assertThat(don.quanLy(TrangThaiDatPhong.HOAN_TAT,n,n,"00000001","KHACH@")).extracting(DatPhong::getMaDatPhong).containsExactly("DP00000001");
 }
 @Test void renderTatCaTrangQuanLyVaBuongPhong() throws Exception {
  for(String url:List.of("/admin/phong","/admin/phong/them","/admin/phong/P201/sua","/admin/phong/loai/them","/admin/phong/loai/LP001/sua","/admin/nhan-vien","/admin/nhan-vien/them","/admin/nhan-vien/NV0001/sua","/admin/dat-phong","/admin/dat-phong/DP00000001","/admin/tham-so","/admin/bao-cao"))
   mvc.perform(get(url).with(user("quanly@khachsan.vn").roles("VT02"))).andExpect(status().isOk());
  mvc.perform(get("/admin/phong/tinh-trang").with(user("buongphong@khachsan.vn").roles("VT03"))).andExpect(status().isOk());
 }
 @Test void dtoRecordDuocBindVaChanDuLieuSai() throws Exception {
  mvc.perform(post("/admin/phong/loai/them").with(user("quanly@khachsan.vn").roles("VT02")).with(csrf()).param("tenLoaiPhong","Loại từ form").param("giaMoiDem","750000").param("sucChua","2")).andExpect(status().is3xxRedirection());
  assertThat(lp.findAll()).extracting(LoaiPhong::getTenLoaiPhong).contains("Loại từ form");
  mvc.perform(post("/admin/phong/loai/them").with(user("quanly@khachsan.vn").roles("VT02")).with(csrf()).param("tenLoaiPhong","").param("giaMoiDem","-1").param("sucChua","0")).andExpect(status().isOk()).andExpect(model().hasErrors());
  mvc.perform(post("/admin/phong/loai/them").with(user("quanly@khachsan.vn").roles("VT02")).with(csrf()).param("tenLoaiPhong","Giá nhập sai").param("giaMoiDem","abc").param("sucChua","2"))
   .andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("Vui lòng nhập số tiền hợp lệ.")));
 }
}
