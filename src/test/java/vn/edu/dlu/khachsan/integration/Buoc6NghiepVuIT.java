package vn.edu.dlu.khachsan.integration;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.math.BigDecimal;
import java.time.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.access.AccessDeniedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.edu.dlu.khachsan.dto.*;
import vn.edu.dlu.khachsan.entity.*;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.repository.*;
import vn.edu.dlu.khachsan.service.*;
@SpringBootTest(properties={
 "spring.datasource.url=jdbc:mysql://localhost:3307/quan_ly_khach_san_kiem_thu?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Ho_Chi_Minh",
 "app.email-log-dir=.tools/email-kiem-thu","app.scheduler.enabled=false"})
@AutoConfigureMockMvc
class Buoc6NghiepVuIT {
 @Autowired DatPhongService datPhongService;
 @Autowired PhongService phongService;
 @Autowired ThanhToanService thanhToanService;
 @Autowired DatPhongRepository datPhongRepository;
 @Autowired PhongRepository phongRepository;
 @Autowired HoaDonRepository hoaDonRepository;
 @Autowired ThanhToanRepository thanhToanRepository;
 @Autowired PlatformTransactionManager transactionManager;
 @Autowired MockMvc mvc;
 @Autowired ObjectMapper mapper;
 private DatPhongDto dto(String ma,int ngay){
  DatPhongDto d=new DatPhongDto();d.setNgayNhanPhong(LocalDate.now().plusDays(ngay));
  d.setNgayTraPhong(d.getNgayNhanPhong().plusDays(2));d.getPhong().put(ma,2);d.setDongYQuyDinh(true);return d;
 }
 private ThanhToanDto coc(String tien){ThanhToanDto t=new ThanhToanDto();t.setToanBo(false);t.setSoTien(new BigDecimal(tien));t.setMaPhuongThuc("TRUC_TUYEN");return t;}
 private String batDau(String ma,String tien){
  String url=thanhToanService.batDauTrucTuyen(ma,"khach@gmail.com",coc(tien)).urlChuyenHuong();
  return url.substring(url.lastIndexOf('/')+1);
 }
 @Test @Transactional
 void timPhongLoaiBaoTriTrungLichVaKiemTraNgay(){
  TimPhongDto t=new TimPhongDto();t.setNgayNhanPhong(LocalDate.now().plusDays(60));t.setNgayTraPhong(t.getNgayNhanPhong().plusDays(2));
  assertThat(phongService.timPhong(t).stream().flatMap(k->k.dsPhong().stream()).map(Phong::getMaPhong)).doesNotContain("P304");
  var d=datPhongService.taoDonTam("khach@gmail.com",dto("P103",60));
  assertThat(phongService.timPhong(t).stream().flatMap(k->k.dsPhong().stream()).map(Phong::getMaPhong)).doesNotContain("P103");
  t.setNgayNhanPhong(d.getNgayTraPhong());t.setNgayTraPhong(t.getNgayNhanPhong().plusDays(1));
  assertThat(phongService.timPhong(t).stream().flatMap(k->k.dsPhong().stream()).map(Phong::getMaPhong)).contains("P103");
  t.setNgayTraPhong(t.getNgayNhanPhong());
  assertThatThrownBy(()->phongService.timPhong(t)).isInstanceOf(NghiepVuException.class);
 }
 @Test @Transactional
 void datNhieuPhongDichVuChotGiaVaKhongDoiTrangThai(){
  DatPhongDto dto=dto("P201",70);dto.getPhong().put("P203",1);dto.getDichVu().put("DV01",2);dto.getDichVu().put("DV03",1);
  DatPhong d=datPhongService.taoDonTam("khach@gmail.com",dto);
  assertThat(d.getTongTien()).isEqualByComparingTo("4100000");
  assertThat(d.getDsChiTietDatPhong()).extracting(ChiTietDatPhong::getMaCTDP).doesNotHaveDuplicates().hasSize(2);
  assertThat(phongRepository.findById("P201").orElseThrow().getTrangThai()).isEqualTo("TRONG");
 }
 @Test @Transactional
 void tamTinhKhongLuuVaCoCsrf() throws Exception{
  long cu=datPhongRepository.count();DatPhongDto d=dto("P201",80);d.getDichVu().put("DV01",2);d.getDichVu().put("DV03",1);
  mvc.perform(post("/dat-phong/tam-tinh").with(user("khach@gmail.com").roles("KHACH")).with(csrf())
   .contentType("application/json").content(mapper.writeValueAsString(d)))
   .andExpect(status().isOk()).andExpect(jsonPath("$.tongTien").value(2500000)).andExpect(jsonPath("$.chiTiet.length()").value(3));
  assertThat(datPhongRepository.count()).isEqualTo(cu);
  mvc.perform(post("/dat-phong/tam-tinh").with(user("khach@gmail.com").roles("KHACH"))
   .contentType("application/json").content(mapper.writeValueAsString(d))).andExpect(status().isForbidden());
 }
 @Test @Transactional
 void cacTrangRenderDuoc() throws Exception{
  var d=datPhongService.taoDonTam("khach@gmail.com",dto("P201",90));String tt=batDau(d.getMaDatPhong(),"480000");
  mvc.perform(get("/dat-phong/tim-kiem")).andExpect(status().isOk());
  mvc.perform(get("/dat-phong/xac-nhan").with(user("khach@gmail.com").roles("KHACH"))
   .param("ngayNhanPhong",LocalDate.now().plusDays(90).toString()).param("ngayTraPhong",LocalDate.now().plusDays(92).toString()).param("maPhong","P203"))
   .andExpect(status().isOk());
  for(String url:List.of("/thanh-toan/"+d.getMaDatPhong(),"/thanh-toan/gia-lap/"+tt,"/thanh-toan/ket-qua/"+tt))
   mvc.perform(get(url).with(user("khach@gmail.com").roles("KHACH"))).andExpect(status().isOk());
  mvc.perform(get("/admin/tim-phong").with(user("letan@khachsan.vn").roles("VT01"))).andExpect(status().isOk());
 }
 @Test @Transactional
 void thatBaiThuLaiDungMotHoaDonVaHaiGiaoDichThanhCongIdempotent() throws Exception{
  var d=datPhongService.taoDonTam("khach@gmail.com",dto("P201",100));
  String lan1=batDau(d.getMaDatPhong(),"480000");
  thanhToanService.xacNhanTrucTuyen(lan1,false,"khach@gmail.com");
  String lan2=batDau(d.getMaDatPhong(),"480000");
  assertThat(lan2).isNotEqualTo(lan1);
  thanhToanService.xacNhanTrucTuyen(lan2,true,"khach@gmail.com");
  thanhToanService.xacNhanTrucTuyen(lan2,false,"khach@gmail.com");
  var ct=datPhongService.chiTietCuaKhach(d.getMaDatPhong(),"khach@gmail.com");
  assertThat(ct.giaoDich()).hasSize(2);assertThat(ct.daTra()).isEqualByComparingTo("480000");
  assertThat(ct.hoaDon().getTrangThaiThanhToan()).isEqualTo(TrangThaiThanhToan.DA_COC);
  assertThat(ct.don().getTrangThai()).isEqualTo(TrangThaiDatPhong.DA_XAC_NHAN);
  Path file=Path.of(".tools/email-kiem-thu/xac-nhan-"+d.getMaDatPhong()+".html");
  assertThat(Files.readString(file)).contains("Phòng 201","480.000 đ",d.getMaDatPhong());
 }
 @Test @Transactional
 void cocThapVaDonKhongThuocKhachBiTuChoi(){
  var d=datPhongService.taoDonTam("khach@gmail.com",dto("P201",110));
  assertThatThrownBy(()->batDau(d.getMaDatPhong(),"1")).isInstanceOf(NghiepVuException.class);
  assertThatThrownBy(()->datPhongService.chiTietCuaKhach(d.getMaDatPhong(),"nguoi-khac@example.com")).isInstanceOf(AccessDeniedException.class);
  assertThatThrownBy(()->thanhToanService.batDauTrucTuyen(d.getMaDatPhong(),"nguoi-khac@example.com",coc("480000"))).isInstanceOf(AccessDeniedException.class);
 }
 @Test @Transactional
 void quaHanHuyDonVaGiaoDichDangXuLy(){
  var d=datPhongService.taoDonTam("khach@gmail.com",dto("P201",115));String tt=batDau(d.getMaDatPhong(),"480000");
  d.setNgayDat(DatPhongService.bayGio().minusMinutes(20));datPhongRepository.flush();
  datPhongService.huyDonQuaHan();
  assertThat(d.getTrangThai()).isEqualTo(TrangThaiDatPhong.DA_HUY);
  assertThat(thanhToanRepository.findById(tt).orElseThrow().getTrangThai()).isEqualTo(TrangThaiGiaoDich.THAT_BAI);
 }
 @Test
 void haiYeuCauDongThoiChiTaoMotDon() throws Exception{
  var pool=Executors.newFixedThreadPool(2);var barrier=new CyclicBarrier(2);
  Callable<String> task=()->{barrier.await(10,TimeUnit.SECONDS);try{return datPhongService.taoDonTam("khach@gmail.com",dto("P103",120)).getMaDatPhong();}
    catch(NghiepVuException ex){return "BI_TU_CHOI";}};
  String a=null,b=null;
  try{var f1=pool.submit(task);var f2=pool.submit(task);a=f1.get(20,TimeUnit.SECONDS);b=f2.get(20,TimeUnit.SECONDS);
   assertThat(List.of(a,b).stream().filter(s->!s.equals("BI_TU_CHOI"))).hasSize(1);
  }finally{pool.shutdownNow();for(String ma:Arrays.asList(a,b))if(ma!=null&&!ma.equals("BI_TU_CHOI")){
   String id=ma;new TransactionTemplate(transactionManager).execute(status->{datPhongRepository.khoaTheoMa(id).orElseThrow().setTrangThai(TrangThaiDatPhong.DA_HUY);return null;});
  }}
 }
}
