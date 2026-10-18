package vn.edu.dlu.khachsan.pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static vn.edu.dlu.khachsan.pattern.DuLieuKiemThu.phong;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import vn.edu.dlu.khachsan.entity.Phong;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.pattern.state.ThaoTacPhong;
import vn.edu.dlu.khachsan.pattern.state.TrangThaiPhong;

class TrangThaiPhongTest {

    private static final Map<ThaoTacPhong, Consumer<Phong>> THAO_TAC = Map.of(
            ThaoTacPhong.CHECK_IN, Phong::checkIn,
            ThaoTacPhong.CHECK_OUT, Phong::checkOut,
            ThaoTacPhong.DON_XONG, Phong::donXong,
            ThaoTacPhong.BAO_TRI, Phong::baoTri,
            ThaoTacPhong.KET_THUC_BAO_TRI, Phong::ketThucBaoTri);

    @Test
    void diDuVongTrangThaiQuaContext() {
        Phong phong = phong();
        assertThat(phong.layTrangThai().getTenHienThi()).isEqualTo("Trống");
        phong.checkIn();
        assertThat(phong.getTrangThai()).isEqualTo("CO_KHACH");
        phong.checkOut();
        assertThat(phong.getTrangThai()).isEqualTo("DANG_DON");
        phong.donXong();
        assertThat(phong.getTrangThai()).isEqualTo("TRONG");
    }

    @Test
    void checkInKhiDangDonTraDungThongBaoVaKhongDoiTrangThai() {
        Phong phong = phong();
        phong.setSoPhong("104");
        phong.checkIn();
        phong.checkOut();
        assertThatThrownBy(phong::checkIn).isInstanceOf(NghiepVuException.class)
                .hasMessage("Phòng 104 đang ở trạng thái Đang dọn, không thể check-in.");
        assertThat(phong.getTrangThai()).isEqualTo("DANG_DON");
    }

    @Test
    void baoTriKhongNhanDatCacTrangThaiKhacVanNhanDat() {
        Phong phong = phong();
        assertThat(phong.layTrangThai().coTheNhanDat()).isTrue();
        phong.checkIn();
        assertThat(phong.layTrangThai().coTheNhanDat()).isTrue();
        phong.checkOut();
        assertThat(phong.layTrangThai().coTheNhanDat()).isTrue();
        phong.baoTri();
        assertThat(phong.layTrangThai().coTheNhanDat()).isFalse();
        phong.ketThucBaoTri();
        assertThat(phong.getTrangThai()).isEqualTo("TRONG");
    }

    static Stream<Arguments> maTranChuyenTrangThai() {
        return Stream.of("TRONG", "CO_KHACH", "DANG_DON", "BAO_TRI")
                .flatMap(ma -> Stream.of(ThaoTacPhong.values()).map(tt -> Arguments.of(ma, tt)));
    }

    @ParameterizedTest
    @MethodSource("maTranChuyenTrangThai")
    void kiemTraToanBo20CapTrangThaiVaThaoTac(String ma, ThaoTacPhong thaoTac) {
        Phong phong = phong();
        if (ma.equals("CO_KHACH") || ma.equals("DANG_DON")) {
            phong.checkIn();
        }
        if (ma.equals("DANG_DON")) {
            phong.checkOut();
        }
        if (ma.equals("BAO_TRI")) {
            phong.baoTri();
        }

        Map<String, Map<ThaoTacPhong, String>> chuyenDoi = Map.of(
                "TRONG", Map.of(ThaoTacPhong.CHECK_IN, "CO_KHACH", ThaoTacPhong.BAO_TRI, "BAO_TRI"),
                "CO_KHACH", Map.of(ThaoTacPhong.CHECK_OUT, "DANG_DON"),
                "DANG_DON", Map.of(ThaoTacPhong.DON_XONG, "TRONG", ThaoTacPhong.BAO_TRI, "BAO_TRI"),
                "BAO_TRI", Map.of(ThaoTacPhong.KET_THUC_BAO_TRI, "TRONG"));
        Set<ThaoTacPhong> hopLe = chuyenDoi.get(ma).keySet();
        assertThat(phong.layTrangThai().thaoTacHopLe()).isEqualTo(hopLe);
        assertThat(TrangThaiPhong.tuMa(ma).getMa()).isEqualTo(ma);

        String ketQua = chuyenDoi.get(ma).get(thaoTac);
        if (ketQua != null) {
            THAO_TAC.get(thaoTac).accept(phong);
            assertThat(phong.getTrangThai()).isEqualTo(ketQua);
        } else {
            assertThatThrownBy(() -> THAO_TAC.get(thaoTac).accept(phong))
                    .isInstanceOf(NghiepVuException.class).hasMessageContaining("không thể");
            assertThat(phong.getTrangThai()).isEqualTo(ma);
        }
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "KHONG_HOP_LE"})
    void maTrangThaiSaiBiTuChoi(String ma) {
        assertThatThrownBy(() -> TrangThaiPhong.tuMa(ma)).isInstanceOf(NghiepVuException.class)
                .hasMessage("Trạng thái phòng không hợp lệ.");
    }
}
