package vn.edu.dlu.khachsan.pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static vn.edu.dlu.khachsan.pattern.DuLieuKiemThu.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import vn.edu.dlu.khachsan.entity.DatPhong;
import vn.edu.dlu.khachsan.entity.DichVu;
import vn.edu.dlu.khachsan.entity.Phong;
import vn.edu.dlu.khachsan.entity.TrangThaiDatPhong;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.pattern.builder.DatPhongBuilder;
import vn.edu.dlu.khachsan.pattern.decorator.GhepChiPhi;

class DatPhongBuilderTest {

    @Test
    void thieuKhachHangBiTuChoi() {
        assertThatThrownBy(() -> builder().choKhachHang(null).themPhong(phong(), 2).build())
                .isInstanceOf(NghiepVuException.class).hasMessageContaining("khách hàng");
    }

    @Test
    void thieuPhongBiTuChoi() {
        assertThatThrownBy(() -> builder().build()).isInstanceOf(NghiepVuException.class)
                .hasMessage("Vui lòng chọn ít nhất một phòng.");
    }

    @Test
    void thieuNgayBiTuChoi() {
        assertThatThrownBy(() -> builder().ngayNhanPhong(null).themPhong(phong(), 2).build())
                .isInstanceOf(NghiepVuException.class).hasMessageContaining("ngày nhận phòng");
        assertThatThrownBy(() -> builder().ngayTraPhong(null).themPhong(phong(), 2).build())
                .isInstanceOf(NghiepVuException.class).hasMessageContaining("ngày trả phòng");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void ngayTraKhongSauNgayNhanBiTuChoi(int soNgay) {
        assertThatThrownBy(() -> builder().ngayTraPhong(HOM_NAY.plusDays(soNgay))
                .themPhong(phong(), 2).build()).isInstanceOf(NghiepVuException.class)
                .hasMessage("Ngày trả phòng phải sau ngày nhận phòng.");
    }

    @Test
    void ngayNhanTrongQuaKhuBiTuChoi() {
        assertThatThrownBy(() -> builder().ngayNhanPhong(HOM_NAY.minusDays(1))
                .themPhong(phong(), 2).build()).isInstanceOf(NghiepVuException.class)
                .hasMessage("Ngày nhận phòng không được trước hôm nay.");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 3})
    void soKhachKhongHopLeBiTuChoi(int soKhach) {
        assertThatThrownBy(() -> builder().themPhong(phong(), soKhach).build())
                .isInstanceOf(NghiepVuException.class).hasMessageContaining("phải từ 1 đến 2");
    }

    @Test
    void phongBaoTriBiTuChoi() {
        Phong phong = phong();
        phong.baoTri();
        assertThatThrownBy(() -> builder().themPhong(phong, 2).build())
                .isInstanceOf(NghiepVuException.class).hasMessageContaining("đang bảo trì");
        assertThat(phong.getTrangThai()).isEqualTo("BAO_TRI");
    }

    @Test
    void phongTrungCungDoiTuongBiTuChoi() {
        Phong phong = phong();
        assertThatThrownBy(() -> builder().themPhong(phong, 1).themPhong(phong, 1).build())
                .isInstanceOf(NghiepVuException.class).hasMessageContaining("đã được chọn");
    }

    @Test
    void phongTrungMaKhacDoiTuongBiTuChoi() {
        assertThatThrownBy(() -> builder().themPhong(phong(), 1).themPhong(phong(), 1).build())
                .isInstanceOf(NghiepVuException.class).hasMessageContaining("đã được chọn");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11})
    void soLuongDichVuNgoaiGioiHanBiTuChoi(int soLuong) {
        assertThatThrownBy(() -> builder().themPhong(phong(), 2).themDichVu(anSang(), soLuong).build())
                .isInstanceOf(NghiepVuException.class).hasMessageContaining("từ 1 đến 10");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    void chapNhanBienSoLuongDichVu(int soLuong) {
        DatPhong don = builder().themPhong(phong(), 2).themDichVu(anSang(), soLuong).build();
        assertThat(don.getDsChiTietDichVu()).singleElement()
                .satisfies(ct -> assertThat(ct.getSoLuong()).isEqualTo(soLuong));
    }

    @Test
    void dichVuTrungMaBiTuChoi() {
        assertThatThrownBy(() -> builder().themPhong(phong(), 2)
                .themDichVu(anSang(), 1).themDichVu(anSang(), 2).build())
                .isInstanceOf(NghiepVuException.class).hasMessageContaining("chỉ được chọn một lần");
    }

    @Test
    void donHopLeChotGiaTongTienVaLienKetHaiChieu() {
        Phong phong = phong();
        DichVu anSang = anSang();
        DatPhong don = builder().themPhong(phong, 2).themDichVu(anSang, 2).themDichVu(duaDon(), 1).build();

        assertThat(don.getTongTien()).isEqualByComparingTo("2500000.00");
        assertThat(don.getTrangThai()).isEqualTo(TrangThaiDatPhong.CHO_THANH_TOAN);
        assertThat(don.getNgayDat()).isEqualTo(LocalDateTime.of(2026, 10, 19, 0, 30));
        assertThat(don.getMaDatPhong()).isNull();
        assertThat(don.getDsChiTietDatPhong()).singleElement().satisfies(ct -> {
            assertThat(ct.getGiaThoiDiemDat()).isEqualByComparingTo("800000");
            assertThat(ct.getDatPhong()).isSameAs(don);
            assertThat(ct.getMaCTDP()).isNull();
        });
        assertThat(don.getDsChiTietDichVu()).allSatisfy(ct -> {
            assertThat(ct.getDatPhong()).isSameAs(don);
            assertThat(ct.getMaCTDV()).isNull();
        });

        phong.getLoaiPhong().setGiaMoiDem(new BigDecimal("900000"));
        anSang.setDonGia(new BigDecimal("200000"));
        assertThat(GhepChiPhi.tuDonDatPhong(don).tinhTien()).isEqualByComparingTo("2500000");
        assertThat(phong.getTrangThai()).isEqualTo("TRONG");
    }

    @Test
    void datPhongDangDonKhongDoiTrangThaiPhong() {
        Phong phong = phong();
        phong.checkIn();
        phong.checkOut();
        builder().themPhong(phong, 2).build();
        assertThat(phong.getTrangThai()).isEqualTo("DANG_DON");
    }

    @Test
    void buildHaiLanTaoHaiDonDocLap() {
        DatPhongBuilder builder = builder().themPhong(phong(), 2);
        DatPhong don1 = builder.build();
        DatPhong don2 = builder.build();
        assertThat(don2).isNotSameAs(don1);
        assertThat(don2.getDsChiTietDatPhong()).singleElement()
                .isNotSameAs(don1.getDsChiTietDatPhong().getFirst());
    }
}
