package vn.edu.dlu.khachsan.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.dlu.khachsan.dto.DangKyDto;
import vn.edu.dlu.khachsan.repository.KhachHangRepository;
import vn.edu.dlu.khachsan.service.TaiKhoanService;

@SpringBootTest(properties={
 "spring.datasource.url=jdbc:mysql://localhost:3307/quan_ly_khach_san_kiem_thu_nen?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Ho_Chi_Minh",
 "app.scheduler.enabled=false"})
@AutoConfigureMockMvc
@Transactional
@Import(Buoc5SecurityIT.KiemTraQuyenController.class)
class Buoc5SecurityIT {

    @Autowired private MockMvc mvc;
    @Autowired private KhachHangRepository khachHangRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private TaiKhoanService taiKhoanService;

    @ParameterizedTest
    @CsvSource({"/dang-nhap,khach@gmail.com,KHACH,/", "/admin/dang-nhap,letan@khachsan.vn,VT01,/admin",
            "/admin/dang-nhap,quanly@khachsan.vn,VT02,/admin", "/admin/dang-nhap,buongphong@khachsan.vn,VT03,/admin"})
    void dangNhapBonTaiKhoanMau(String url, String email, String role, String dich) throws Exception {
        mvc.perform(post(url).with(csrf()).param("email", email).param("password", "123456"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(dich))
                .andExpect(authenticated().withUsername(email).withRoles(role));
    }

    @ParameterizedTest
    @CsvSource({"/dang-nhap,letan@khachsan.vn,/dang-nhap?loi", "/admin/dang-nhap,khach@gmail.com,/admin/dang-nhap?loi"})
    void khongDungTaiKhoanPhanHeKhac(String url, String email, String dich) throws Exception {
        mvc.perform(post(url).with(csrf()).param("email", email).param("password", "123456"))
                .andExpect(redirectedUrl(dich)).andExpect(unauthenticated());
    }

    @Test
    void emailGiongNhauVanXacThucDungBang() throws Exception {
        DangKyDto dto = new DangKyDto();
        dto.setHoTen("Khách dùng email trùng nhân viên");
        dto.setEmail("letan@khachsan.vn");
        dto.setMatKhau("khach123");
        dto.setXacNhanMatKhau("khach123");
        taiKhoanService.dangKy(dto);
        mvc.perform(post("/dang-nhap").with(csrf()).param("email", "letan@khachsan.vn").param("password", "123456"))
                .andExpect(redirectedUrl("/dang-nhap?loi")).andExpect(unauthenticated());
        mvc.perform(post("/dang-nhap").with(csrf()).param("email", "letan@khachsan.vn").param("password", "khach123"))
                .andExpect(authenticated().withRoles("KHACH"));
        mvc.perform(post("/admin/dang-nhap").with(csrf()).param("email", "letan@khachsan.vn").param("password", "khach123"))
                .andExpect(unauthenticated());
        mvc.perform(post("/admin/dang-nhap").with(csrf()).param("email", "letan@khachsan.vn").param("password", "123456"))
                .andExpect(authenticated().withRoles("VT01"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/dang-nhap", "/admin/dang-nhap"})
    void saiMatKhauHienThongBaoTiengViet(String url) throws Exception {
        String email = url.startsWith("/admin") ? "letan@khachsan.vn" : "khach@gmail.com";
        mvc.perform(post(url).with(csrf()).param("email", email).param("password", "sai123"))
                .andExpect(redirectedUrl(url + "?loi")).andExpect(unauthenticated());
        mvc.perform(get(url).param("loi", ""))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Email hoặc mật khẩu không đúng.")));
    }

    static Stream<Arguments> maTranQuyen() {
        Map<String, Set<String>> quyen = Map.ofEntries(
                Map.entry("/admin", Set.of("VT01", "VT02", "VT03")),
                Map.entry("/admin/tim-phong/__kiem-tra-quyen", Set.of("VT01", "VT02")),
                Map.entry("/admin/dat-phong/can-xac-nhan/__kiem-tra-quyen", Set.of("VT01")),
                Map.entry("/admin/nhan-tra-phong/check-in/__kiem-tra-quyen", Set.of("VT01")),
                Map.entry("/admin/nhan-tra-phong/check-out/__kiem-tra-quyen", Set.of("VT01")),
                Map.entry("/admin/hoa-don/__kiem-tra-quyen", Set.of("VT01")),
                Map.entry("/admin/phong/tinh-trang/__kiem-tra-quyen", Set.of("VT03")),
                Map.entry("/admin/phong/__kiem-tra-quyen", Set.of("VT02")),
                Map.entry("/admin/nhan-vien/__kiem-tra-quyen", Set.of("VT02")),
                Map.entry("/admin/bao-cao/__kiem-tra-quyen", Set.of("VT02")),
                Map.entry("/admin/dat-phong/__kiem-tra-quyen", Set.of("VT02")),
                Map.entry("/admin/tham-so/__kiem-tra-quyen", Set.of("VT02")),
                Map.entry("/dat-phong/__kiem-tra-quyen", Set.of("KHACH")),
                Map.entry("/thanh-toan/__kiem-tra-quyen", Set.of("KHACH")),
                Map.entry("/danh-gia/__kiem-tra-quyen", Set.of("KHACH")),
                Map.entry("/tai-khoan/__kiem-tra-quyen", Set.of("KHACH")));
        return quyen.entrySet().stream().flatMap(e -> Stream.of("KHACH", "VT01", "VT02", "VT03")
                .map(role -> Arguments.of(e.getKey(), role, e.getValue().contains(role))));
    }

    @ParameterizedTest
    @MethodSource("maTranQuyen")
    void dungVaiTroMoiQuaDuocUrlTrucTiep(String url, String role, boolean choPhep) throws Exception {
        mvc.perform(get(url).with(user("kiem-tra").roles(role)))
                .andExpect(status().is(choPhep ? 200 : 403));
    }

    @Test
    void khachVangDuocXemTrangChuVaDuocDanDenDungTrangDangNhap() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(content().string(containsString("Chạm vào")));
        mvc.perform(get("/tai-khoan/doi-mat-khau")).andExpect(redirectedUrlPattern("**/dang-nhap"));
        mvc.perform(get("/admin")).andExpect(redirectedUrlPattern("**/admin/dang-nhap"));
        mvc.perform(get("/dat-phong/tim-kiem")).andExpect(status().isOk());
        mvc.perform(get("/css/ung-dung.css")).andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/dang-nhap", "/admin/dang-nhap", "/dang-ky"})
    void formCoCsrfVaPostThieuTokenBiTuChoi(String url) throws Exception {
        mvc.perform(get(url)).andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"_csrf\"")));
        mvc.perform(post(url).param("email", "khach@gmail.com").param("password", "123456"))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @CsvSource({"letan@khachsan.vn,Nhận phòng,Báo cáo doanh thu,Tình trạng phòng",
            "quanly@khachsan.vn,Báo cáo doanh thu,Nhận phòng,Tình trạng phòng",
            "buongphong@khachsan.vn,Tình trạng phòng,Nhận phòng,Báo cáo doanh thu"})
    void menuVaTheTomTatChiHienDungVaiTro(String email, String co, String khong1, String khong2) throws Exception {
        MockHttpSession session = dangNhap("/admin/dang-nhap", email, "123456");
        mvc.perform(get("/admin").session(session)).andExpect(status().isOk())
                .andExpect(content().string(containsString(co)))
                .andExpect(content().string(not(containsString(khong1))))
                .andExpect(content().string(not(containsString(khong2))));
    }

    @ParameterizedTest
    @CsvSource({"/dang-nhap,khach@gmail.com,/dang-xuat,/dang-nhap?dang-xuat",
            "/admin/dang-nhap,letan@khachsan.vn,/admin/dang-xuat,/admin/dang-nhap?dang-xuat"})
    void dangXuatChiThucHienBangPostCoCsrf(String login, String email, String logout, String dich) throws Exception {
        MockHttpSession session = dangNhap(login, email, "123456");
        mvc.perform(get(logout).session(session)).andExpect(authenticated());
        mvc.perform(post(logout).session(session)).andExpect(status().isForbidden()).andExpect(authenticated());
        mvc.perform(post(logout).session(session).with(csrf())).andExpect(redirectedUrl(dich)).andExpect(unauthenticated());
    }

    @Test
    void dangKyLuuBcryptVaDangNhapDuoc() throws Exception {
        mvc.perform(post("/dang-ky").with(csrf()).param("hoTen", "Khách kiểm thử")
                .param("email", "NGUOIMOI@example.com").param("matKhau", "abcdef")
                .param("xacNhanMatKhau", "abcdef"))
                .andExpect(redirectedUrl("/dang-nhap?dang-ky-thanh-cong"));
        var khach = khachHangRepository.findByEmail("nguoimoi@example.com").orElseThrow();
        assertThat(passwordEncoder.matches("abcdef", khach.getMatKhau())).isTrue();
        mvc.perform(post("/dang-nhap").with(csrf()).param("email", "nguoimoi@example.com").param("password", "abcdef"))
                .andExpect(authenticated().withRoles("KHACH"));
    }

    @ParameterizedTest
    @CsvSource({"abc,abc", "abcdef,khongkhop"})
    void dangKyMatKhauSaiKhongLuu(String matKhau, String xacNhan) throws Exception {
        long soKhach = khachHangRepository.count();
        mvc.perform(post("/dang-ky").with(csrf()).param("hoTen", "Khách kiểm thử")
                .param("email", "kiemtra@example.com").param("matKhau", matKhau).param("xacNhanMatKhau", xacNhan))
                .andExpect(status().isOk()).andExpect(model().attributeHasErrors("dangKyDto"));
        assertThat(khachHangRepository.count()).isEqualTo(soKhach);
    }

    @Test
    void dangKyTrungEmailBiTuChoi() throws Exception {
        mvc.perform(post("/dang-ky").with(csrf()).param("hoTen", "Khách kiểm thử")
                .param("email", "KHACH@gmail.com").param("matKhau", "abcdef").param("xacNhanMatKhau", "abcdef"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Email này đã được sử dụng.")));
    }

    @Test
    void doiMatKhauDangXuatVaChiMatKhauMoiDangNhapDuoc() throws Exception {
        MockHttpSession session = dangNhap("/dang-nhap", "khach@gmail.com", "123456");
        mvc.perform(post("/tai-khoan/doi-mat-khau").session(session).with(csrf())
                .param("matKhauHienTai", "123456").param("matKhauMoi", "matkhaumoi")
                .param("xacNhanMatKhau", "matkhaumoi"))
                .andExpect(redirectedUrl("/dang-nhap?doi-mat-khau-thanh-cong")).andExpect(unauthenticated());
        mvc.perform(post("/dang-nhap").with(csrf()).param("email", "khach@gmail.com").param("password", "123456"))
                .andExpect(unauthenticated());
        mvc.perform(post("/dang-nhap").with(csrf()).param("email", "khach@gmail.com").param("password", "matkhaumoi"))
                .andExpect(authenticated().withRoles("KHACH"));
    }

    @Test
    void doiMatKhauSaiMatKhauCuKhongThayDoiDuLieu() throws Exception {
        MockHttpSession session = dangNhap("/dang-nhap", "khach@gmail.com", "123456");
        mvc.perform(post("/tai-khoan/doi-mat-khau").session(session).with(csrf())
                .param("matKhauHienTai", "sai123").param("matKhauMoi", "matkhaumoi").param("xacNhanMatKhau", "matkhaumoi"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Mật khẩu hiện tại không đúng.")));
        assertThat(passwordEncoder.matches("123456", khachHangRepository.findByEmail("khach@gmail.com").orElseThrow().getMatKhau())).isTrue();
    }

    @Test
    void trangLoiTiengVietKhongLoChiTietHeThong() throws Exception {
        mvc.perform(get("/loi/403")).andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Bạn chưa thể truy cập")));
        mvc.perform(get("/error").with(request -> { request.setDispatcherType(DispatcherType.ERROR); return request; }).accept(MediaType.TEXT_HTML)
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404))
                .andExpect(status().isNotFound()).andExpect(content().string(containsString("Trang bạn tìm không có ở đây.")));
        mvc.perform(get("/error").with(request -> { request.setDispatcherType(DispatcherType.ERROR); return request; }).accept(MediaType.TEXT_HTML)
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 500)
                .requestAttr(RequestDispatcher.ERROR_EXCEPTION, new RuntimeException("KHONG_DUOC_HIEN_THI")))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Có một sự cố vừa xảy ra.")))
                .andExpect(content().string(not(containsString("KHONG_DUOC_HIEN_THI"))));
    }

    private MockHttpSession dangNhap(String url, String email, String matKhau) throws Exception {
        return (MockHttpSession) mvc.perform(post(url).with(csrf()).param("email", email).param("password", matKhau))
                .andExpect(authenticated()).andReturn().getRequest().getSession(false);
    }

    // Chỉ tồn tại trong test để kiểm tra filter bảo mật trước khi các trang nghiệp vụ được triển khai.
    @RestController
    static class KiemTraQuyenController {
        @GetMapping({"/admin/tim-phong/__kiem-tra-quyen", "/admin/dat-phong/can-xac-nhan/__kiem-tra-quyen",
                "/admin/nhan-tra-phong/check-in/__kiem-tra-quyen", "/admin/nhan-tra-phong/check-out/__kiem-tra-quyen",
                "/admin/hoa-don/__kiem-tra-quyen", "/admin/phong/tinh-trang/__kiem-tra-quyen",
                "/admin/phong/__kiem-tra-quyen", "/admin/nhan-vien/__kiem-tra-quyen",
                "/admin/bao-cao/__kiem-tra-quyen", "/admin/dat-phong/__kiem-tra-quyen", "/admin/tham-so/__kiem-tra-quyen",
                "/dat-phong/__kiem-tra-quyen", "/thanh-toan/__kiem-tra-quyen", "/danh-gia/__kiem-tra-quyen", "/tai-khoan/__kiem-tra-quyen"})
        String kiemTraQuyen() {
            return "Được phép truy cập.";
        }
    }
}
