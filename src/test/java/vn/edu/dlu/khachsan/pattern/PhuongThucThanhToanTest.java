package vn.edu.dlu.khachsan.pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static vn.edu.dlu.khachsan.pattern.DuLieuKiemThu.giaoDich;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import vn.edu.dlu.khachsan.entity.ThanhToan;
import vn.edu.dlu.khachsan.entity.TrangThaiGiaoDich;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.pattern.strategy.DuLieuThanhToan;
import vn.edu.dlu.khachsan.pattern.strategy.KetQuaXuLy;
import vn.edu.dlu.khachsan.pattern.strategy.PhuongThucThanhToan;
import vn.edu.dlu.khachsan.pattern.strategy.ThanhToanThe;
import vn.edu.dlu.khachsan.pattern.strategy.ThanhToanTienMat;
import vn.edu.dlu.khachsan.pattern.strategy.ThanhToanTrucTuyen;

class PhuongThucThanhToanTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"999999", "0", "-1"})
    void tienMatThieuHoacChuaNhapTienBiTuChoi(String soTien) {
        ThanhToan giaoDich = giaoDich();
        BigDecimal tienDua = soTien == null ? null : new BigDecimal(soTien);
        assertThatThrownBy(() -> new ThanhToanTienMat().xuLy(giaoDich, new DuLieuThanhToan(tienDua, null)))
                .isInstanceOf(NghiepVuException.class);
        assertThat(giaoDich.getGhiChu()).isNull();
    }

    @ParameterizedTest
    @CsvSource({"1000000, 0 đ", "1250000, 250.000 đ"})
    void tienMatDuHoacDuTienHienDungTienThoi(String tienDua, String tienThoi) {
        ThanhToan giaoDich = giaoDich();
        KetQuaXuLy ketQua = new ThanhToanTienMat().xuLy(giaoDich,
                new DuLieuThanhToan(new BigDecimal(tienDua), null));
        assertThat(ketQua.trangThai()).isEqualTo(TrangThaiGiaoDich.THANH_CONG);
        assertThat(ketQua.thongBao()).isEqualTo("Tiền thối lại: " + tienThoi);
        assertThat(ketQua.urlChuyenHuong()).isNull();
        assertThat(giaoDich.getGhiChu()).endsWith(", thối " + tienThoi);
    }

    @Test
    void tienThoiKhongMatPhanLe() {
        ThanhToan giaoDich = giaoDich();
        giaoDich.setSoTien(new BigDecimal("999999.75"));
        KetQuaXuLy ketQua = new ThanhToanTienMat().xuLy(giaoDich,
                new DuLieuThanhToan(new BigDecimal("1000000.00"), null));
        assertThat(ketQua.thongBao()).isEqualTo("Tiền thối lại: 0,25 đ");
        assertThat(giaoDich.getGhiChu()).isEqualTo("Khách đưa 1.000.000 đ, thối 0,25 đ");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"12345", "1234567", "12a456", " 123456", "123456 ", "１２３４５６"})
    void theSaiMaChuanChiBiTuChoi(String ma) {
        ThanhToan giaoDich = giaoDich();
        assertThatThrownBy(() -> new ThanhToanThe().xuLy(giaoDich, new DuLieuThanhToan(null, ma)))
                .isInstanceOf(NghiepVuException.class).hasMessage("Mã chuẩn chi phải gồm đúng 6 chữ số.");
        assertThat(giaoDich.getGhiChu()).isNull();
    }

    @Test
    void theMaHopLeGiuSoKhongODauVaGhiChu() {
        ThanhToan giaoDich = giaoDich();
        KetQuaXuLy ketQua = new ThanhToanThe().xuLy(giaoDich, new DuLieuThanhToan(null, "001234"));
        assertThat(ketQua.trangThai()).isEqualTo(TrangThaiGiaoDich.THANH_CONG);
        assertThat(ketQua.urlChuyenHuong()).isNull();
        assertThat(giaoDich.getGhiChu()).isEqualTo("Mã chuẩn chi: 001234");
    }

    @Test
    void trucTuyenTraDangXuLyVaUrlChinhXac() {
        ThanhToan giaoDich = giaoDich();
        KetQuaXuLy ketQua = new ThanhToanTrucTuyen().xuLy(giaoDich, null);
        assertThat(ketQua.trangThai()).isEqualTo(TrangThaiGiaoDich.DANG_XU_LY);
        assertThat(ketQua.urlChuyenHuong()).isEqualTo("/thanh-toan/gia-lap/TT00000001");
        assertThat(giaoDich.getTrangThai()).isEqualTo(TrangThaiGiaoDich.DANG_XU_LY);
    }

    @Test
    void trucTuyenThieuMaBiTuChoi() {
        ThanhToan giaoDich = giaoDich();
        giaoDich.setMaThanhToan(null);
        assertThatThrownBy(() -> new ThanhToanTrucTuyen().xuLy(giaoDich, null))
                .isInstanceOf(NghiepVuException.class).hasMessageContaining("chưa có mã");
    }

    @Test
    void thieuDuLieuTaiQuayBiTuChoi() {
        assertThatThrownBy(() -> new ThanhToanTienMat().xuLy(giaoDich(), null))
                .isInstanceOf(NghiepVuException.class);
        assertThatThrownBy(() -> new ThanhToanThe().xuLy(giaoDich(), null))
                .isInstanceOf(NghiepVuException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void caBaPhuongThucTuChoiSoTienKhongDuong(String soTien) {
        List<PhuongThucThanhToan> ds = List.of(new ThanhToanTienMat(), new ThanhToanThe(), new ThanhToanTrucTuyen());
        for (PhuongThucThanhToan phuongThuc : ds) {
            ThanhToan giaoDich = giaoDich();
            giaoDich.setSoTien(new BigDecimal(soTien));
            assertThatThrownBy(() -> phuongThuc.xuLy(giaoDich,
                    new DuLieuThanhToan(new BigDecimal("1000000"), "123456")))
                    .isInstanceOf(NghiepVuException.class).hasMessage("Số tiền thanh toán phải lớn hơn 0.");
        }
    }

    @Test
    void springNhanDuBaStrategyVaChonBangMaKhongCanReNhanh() {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.scan("vn.edu.dlu.khachsan.pattern.strategy");
            context.refresh();
            Map<String, PhuongThucThanhToan> ds = context.getBeansOfType(PhuongThucThanhToan.class)
                    .values().stream().collect(Collectors.toMap(PhuongThucThanhToan::getMa, Function.identity()));
            assertThat(ds).containsOnlyKeys("TIEN_MAT", "THE", "TRUC_TUYEN");
            assertThat(ds.values().stream().filter(PhuongThucThanhToan::dungTaiQuay)
                    .map(PhuongThucThanhToan::getTenHienThi))
                    .containsExactlyInAnyOrder("Tiền mặt", "Thẻ ngân hàng");
            assertThat(ds.values().stream().filter(p -> !p.dungTaiQuay()).map(PhuongThucThanhToan::getTenHienThi))
                    .containsExactly("Trực tuyến");
            assertThat(ds.get("THE").xuLy(giaoDich(), new DuLieuThanhToan(null, "123456")).trangThai())
                    .isEqualTo(TrangThaiGiaoDich.THANH_CONG);
        }
    }
}
