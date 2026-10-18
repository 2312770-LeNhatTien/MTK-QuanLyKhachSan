package vn.edu.dlu.khachsan.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.dlu.khachsan.entity.DatPhong;
import vn.edu.dlu.khachsan.entity.LoaiPhong;
import vn.edu.dlu.khachsan.entity.TrangThaiDatPhong;
import vn.edu.dlu.khachsan.entity.TrangThaiGiaoDich;
import vn.edu.dlu.khachsan.entity.TrangThaiThanhToan;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.pattern.decorator.GhepChiPhi;
import vn.edu.dlu.khachsan.repository.*;
import vn.edu.dlu.khachsan.seed.DuLieuMauRunner;
import vn.edu.dlu.khachsan.service.MaSinhService;
import vn.edu.dlu.khachsan.service.ThamSoService;

// Chạy riêng với -Dtest=Buoc4MySqlIT; dữ liệu thay đổi trong test đều được rollback.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties={
 "spring.datasource.url=jdbc:mysql://localhost:3307/quan_ly_khach_san_kiem_thu_nen?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Ho_Chi_Minh",
 "app.scheduler.enabled=false"})
@Transactional
class Buoc4MySqlIT {

    @Autowired private VaiTroRepository vaiTroRepository;
    @Autowired private NhanVienRepository nhanVienRepository;
    @Autowired private KhachHangRepository khachHangRepository;
    @Autowired private LoaiPhongRepository loaiPhongRepository;
    @Autowired private PhongRepository phongRepository;
    @Autowired private DichVuRepository dichVuRepository;
    @Autowired private DatPhongRepository datPhongRepository;
    @Autowired private ChiTietDatPhongRepository chiTietDatPhongRepository;
    @Autowired private ChiTietDichVuRepository chiTietDichVuRepository;
    @Autowired private HoaDonRepository hoaDonRepository;
    @Autowired private ThanhToanRepository thanhToanRepository;
    @Autowired private DanhGiaRepository danhGiaRepository;
    @Autowired private ThamSoRepository thamSoRepository;
    @Autowired private MaSinhService maSinhService;
    @Autowired private ThamSoService thamSoService;
    @Autowired private DuLieuMauRunner duLieuMauRunner;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EntityManager entityManager;

    @Test
    void duSoLuongDuLieuMauVaThamSo() {
        assertThat(vaiTroRepository.count()).isEqualTo(3);
        assertThat(nhanVienRepository.count()).isEqualTo(3);
        assertThat(khachHangRepository.count()).isEqualTo(1);
        assertThat(loaiPhongRepository.count()).isEqualTo(3);
        assertThat(phongRepository.count()).isEqualTo(12);
        assertThat(dichVuRepository.count()).isEqualTo(4);
        assertThat(datPhongRepository.count()).isEqualTo(4);
        assertThat(chiTietDatPhongRepository.count()).isEqualTo(4);
        assertThat(chiTietDichVuRepository.count()).isEqualTo(2);
        assertThat(hoaDonRepository.count()).isEqualTo(3);
        assertThat(thanhToanRepository.count()).isEqualTo(3);
        assertThat(danhGiaRepository.count()).isZero();
        assertThat(thamSoRepository.count()).isEqualTo(3);
        assertThat(thamSoService.thoiGianGiuPhongPhut()).isEqualTo(15);
        assertThat(thamSoService.tiLeDatCoc()).isEqualByComparingTo("30");
        assertThat(thamSoService.thoiHanHuyDonNgay()).isEqualTo(1);
    }

    @Test
    void bonTaiKhoanMatKhauBcryptVaVaiTroDung() {
        assertThat(nhanVienRepository.findAll()).extracting(nv -> nv.getEmail() + ":" + nv.getVaiTro().getMaVaiTro())
                .containsExactlyInAnyOrder("letan@khachsan.vn:VT01", "quanly@khachsan.vn:VT02", "buongphong@khachsan.vn:VT03");
        nhanVienRepository.findAll().forEach(nv -> {
            assertThat(nv.getMatKhau()).startsWith("$2").isNotEqualTo("123456");
            assertThat(passwordEncoder.matches("123456", nv.getMatKhau())).isTrue();
        });
        var khach = khachHangRepository.findById("KH000001").orElseThrow();
        assertThat(khach.getEmail()).isEqualTo("khach@gmail.com");
        assertThat(passwordEncoder.matches("123456", khach.getMatKhau())).isTrue();
    }

    @Test
    void bonDonDungNgayTrangThaiChiPhiHoaDonVaTienCoc() {
        LocalDate homNay = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDate ngayKhoiTaoMau = khachHangRepository.findById("KH000001").orElseThrow()
                .getNgayTao().toLocalDate().plusDays(30);
        DatPhong hoanTat = datPhongRepository.findById("DP00000001").orElseThrow();
        DatPhong choNhan = datPhongRepository.findById("DP00000002").orElseThrow();
        DatPhong dangO = datPhongRepository.findById("DP00000003").orElseThrow();
        DatPhong daHuy = datPhongRepository.findById("DP00000004").orElseThrow();

        assertThat(hoanTat.getTrangThai()).isEqualTo(TrangThaiDatPhong.HOAN_TAT);
        assertThat(ngayKhoiTaoMau).isBeforeOrEqualTo(homNay);
        assertThat(hoanTat.getNgayNhanPhong()).isEqualTo(ngayKhoiTaoMau.minusDays(5));
        assertThat(hoanTat.getNgayTraPhong()).isEqualTo(ngayKhoiTaoMau.minusDays(3));
        assertThat(hoanTat.getTongTien()).isEqualByComparingTo("1600000");
        assertThat(hoanTat.getDsChiTietDichVu().getFirst().getSoLuong()).isEqualTo(2);
        assertThat(choNhan.getTrangThai()).isEqualTo(TrangThaiDatPhong.DA_XAC_NHAN);
        assertThat(choNhan.getNgayNhanPhong()).isEqualTo(ngayKhoiTaoMau);
        assertThat(choNhan.getNhanVien()).isNull();
        assertThat(dangO.getNgayNhanPhong()).isEqualTo(ngayKhoiTaoMau.minusDays(1));
        assertThat(dangO.getNgayTraPhong()).isEqualTo(ngayKhoiTaoMau.plusDays(1));
        assertThat(dangO.getTrangThai()).isEqualTo(TrangThaiDatPhong.DA_XAC_NHAN);
        assertThat(dangO.getTongTien()).isEqualByComparingTo("1900000");
        assertThat(daHuy.getTrangThai()).isEqualTo(TrangThaiDatPhong.DA_HUY);
        assertThat(phongRepository.findById("P101").orElseThrow().getTrangThai()).isEqualTo("TRONG");
        assertThat(phongRepository.findById("P201").orElseThrow().getTrangThai()).isEqualTo("TRONG");
        assertThat(phongRepository.findById("P202").orElseThrow().getTrangThai()).isEqualTo("CO_KHACH");
        assertThat(phongRepository.findById("P104").orElseThrow().getTrangThai()).isEqualTo("DANG_DON");
        assertThat(phongRepository.findById("P304").orElseThrow().getTrangThai()).isEqualTo("BAO_TRI");
        for (DatPhong don : List.of(hoanTat, choNhan, dangO, daHuy)) {
            assertThat(GhepChiPhi.tuDonDatPhong(don).tinhTien()).isEqualByComparingTo(don.getTongTien());
            assertThat(don.getNgayDat().toLocalDate()).isBeforeOrEqualTo(homNay);
            assertThat(don.getNgayDat().toLocalDate()).isBeforeOrEqualTo(don.getNgayNhanPhong());
        }
        assertThat(hoaDonRepository.findById("HD00000001").orElseThrow().getTrangThaiThanhToan())
                .isEqualTo(TrangThaiThanhToan.DA_HOAN_TAT);
        assertThat(hoaDonRepository.findById("HD00000002").orElseThrow().getTrangThaiThanhToan())
                .isEqualTo(TrangThaiThanhToan.DA_COC);
        assertThat(hoaDonRepository.findById("HD00000003").orElseThrow().getTrangThaiThanhToan())
                .isEqualTo(TrangThaiThanhToan.DA_COC);
        assertThat(thanhToanRepository.findById("TT00000001").orElseThrow().getSoTien()).isEqualByComparingTo("1600000");
        assertThat(thanhToanRepository.findById("TT00000002").orElseThrow().getSoTien()).isEqualByComparingTo("480000");
        assertThat(thanhToanRepository.findById("TT00000003").orElseThrow().getSoTien()).isEqualByComparingTo("570000");
        assertThat(thanhToanRepository.findAll()).allSatisfy(tt -> {
            assertThat(tt.getTrangThai()).isEqualTo(TrangThaiGiaoDich.THANH_CONG);
            assertThat(tt.getPhuongThuc()).isEqualTo("TRUC_TUYEN");
        });
    }

    @Test
    void sinhMaDuTienToVaKhongTrungChiTietChuaLuu() {
        assertThat(maSinhService.sinhMaKhachHang()).isEqualTo("KH000002");
        assertThat(maSinhService.sinhMaNhanVien()).isEqualTo("NV0004");
        assertThat(maSinhService.sinhMaLoaiPhong()).isEqualTo("LP004");
        assertThat(maSinhService.sinhMaDichVu()).isEqualTo("DV05");
        assertThat(maSinhService.sinhMaDatPhong()).isEqualTo("DP00000005");
        assertThat(maSinhService.sinhMaChiTietDatPhong()).isEqualTo("CT00000005");
        assertThat(maSinhService.sinhMaChiTietDatPhong()).isEqualTo("CT00000006");
        assertThat(maSinhService.sinhMaChiTietDichVu()).isEqualTo("CV00000003");
        assertThat(maSinhService.sinhMaChiTietDichVu()).isEqualTo("CV00000004");
        assertThat(maSinhService.sinhMaHoaDon()).isEqualTo("HD00000004");
        assertThat(maSinhService.sinhMaThanhToan()).isEqualTo("TT00000004");
        assertThat(maSinhService.sinhMaDanhGia()).isEqualTo("DG00000001");
        assertThat(maSinhService.sinhMaPhong("101")).isEqualTo("P101");
        assertThat(maSinhService.sinhMaPhong("A101")).isEqualTo("PA101");
    }

    @Test
    void sinhMaDungMaxCongMotKhongDungSoBanGhi() {
        loaiPhongRepository.saveAndFlush(loaiPhongThu("LP099"));
        assertThat(maSinhService.sinhMaLoaiPhong()).isEqualTo("LP100");
    }

    @Test
    void hetDaiMaBiTuChoi() {
        loaiPhongRepository.saveAndFlush(loaiPhongThu("LP999"));
        assertThatThrownBy(maSinhService::sinhMaLoaiPhong).isInstanceOf(NghiepVuException.class)
                .hasMessage("Đã hết dải mã LP.");
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void sinhMaBatBuocCoTransactionGhiDuLieu() {
        assertThatThrownBy(maSinhService::sinhMaKhachHang).isInstanceOf(IllegalTransactionStateException.class);
    }

    @Test
    void thamSoCapNhatDocLaiTuCsdlKhongCanKhoiDongLai() {
        assertThat(thamSoService.thoiGianGiuPhongPhut()).isEqualTo(15);
        thamSoService.capNhat(ThamSoService.THOI_GIAN_GIU_PHONG, new BigDecimal("20"));
        entityManager.flush();
        entityManager.clear();
        assertThat(thamSoService.thoiGianGiuPhongPhut()).isEqualTo(20);
        assertThat(thamSoService.layTatCa()).hasSize(3);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "-1", "1.5", "101", "10000000000"})
    void tuChoiTiLeCocSai(String giaTri) {
        assertThatThrownBy(() -> thamSoService.capNhat(ThamSoService.TI_LE_DAT_COC,
                giaTri == null ? null : new BigDecimal(giaTri))).isInstanceOf(NghiepVuException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "100"})
    void chapNhanHaiBienTiLeCoc(String giaTri) {
        thamSoService.capNhat(ThamSoService.TI_LE_DAT_COC, new BigDecimal(giaTri));
        entityManager.flush();
        entityManager.clear();
        assertThat(thamSoService.tiLeDatCoc()).isEqualByComparingTo(giaTri);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"KHONG_TON_TAI"})
    void tuChoiMaThamSoSai(String ma) {
        assertThatThrownBy(() -> thamSoService.layGiaTri(ma)).isInstanceOf(NghiepVuException.class)
                .hasMessage("Mã tham số không hợp lệ.");
    }

    @Test
    void chayLaiRunnerKhongTaoTrungHoacGhiDeThamSo() {
        String matKhauCu = nhanVienRepository.findById("NV0001").orElseThrow().getMatKhau();
        thamSoService.capNhat(ThamSoService.THOI_GIAN_GIU_PHONG, new BigDecimal("25"));
        duLieuMauRunner.run();
        assertThat(thamSoService.thoiGianGiuPhongPhut()).isEqualTo(25);
        assertThat(nhanVienRepository.count()).isEqualTo(3);
        assertThat(datPhongRepository.count()).isEqualTo(4);
        assertThat(thanhToanRepository.count()).isEqualTo(3);
        assertThat(nhanVienRepository.findById("NV0001").orElseThrow().getMatKhau()).isEqualTo(matKhauCu);
    }

    private LoaiPhong loaiPhongThu(String ma) {
        LoaiPhong loai = new LoaiPhong();
        loai.setMaLoaiPhong(ma);
        loai.setTenLoaiPhong("Loại phòng chỉ dùng kiểm thử");
        loai.setGiaMoiDem(new BigDecimal("500000"));
        loai.setSucChua(2);
        return loai;
    }
}
