package vn.edu.dlu.khachsan.pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static vn.edu.dlu.khachsan.pattern.DuLieuKiemThu.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import vn.edu.dlu.khachsan.entity.DatPhong;
import vn.edu.dlu.khachsan.entity.Phong;
import vn.edu.dlu.khachsan.pattern.decorator.ChiPhiDatPhong;
import vn.edu.dlu.khachsan.pattern.decorator.DichVuMotLan;
import vn.edu.dlu.khachsan.pattern.decorator.DichVuTheoDem;
import vn.edu.dlu.khachsan.pattern.decorator.DongChiPhi;
import vn.edu.dlu.khachsan.pattern.decorator.GhepChiPhi;
import vn.edu.dlu.khachsan.pattern.decorator.TienPhong;

class ChiPhiDatPhongTest {

    @Test
    void bocDichVuChoDeluxeHaiDemTinhDung2500000() {
        DatPhong don = builder().themPhong(phong(), 2).build();
        ChiPhiDatPhong chiPhi = new TienPhong(don.getDsChiTietDatPhong(), 2);
        chiPhi = new DichVuTheoDem(chiPhi, "Ăn sáng buffet", new BigDecimal("150000"), 2, 2);
        chiPhi = new DichVuMotLan(chiPhi, "Đưa đón sân bay Liên Khương", new BigDecimal("300000"), 1);

        assertThat(chiPhi.tinhTien()).isEqualByComparingTo("2500000");
        List<DongChiPhi> ds = chiPhi.chiTiet();
        assertThat(ds).extracting(DongChiPhi::noiDung).containsExactly(
                "Phòng 201 (Deluxe)", "Ăn sáng buffet", "Đưa đón sân bay Liên Khương");
        assertThat(ds).extracting(DongChiPhi::cachTinh).containsExactly(
                "800.000 đ × 2 đêm", "150.000 đ × 2 × 2 đêm", "300.000 đ × 1");
        assertThat(ds.get(0).thanhTien()).isEqualByComparingTo("1600000");
        assertThat(ds.get(1).thanhTien()).isEqualByComparingTo("600000");
        assertThat(ds.get(2).thanhTien()).isEqualByComparingTo("300000");
        assertThat(chiPhi.chiTiet()).hasSize(3);
    }

    @Test
    void ghepTuDonGiuThuTuVaGiaDaChot() {
        var duaDon = duaDon();
        var anSang = anSang();
        DatPhong don = builder().themPhong(phong(), 2)
                .themDichVu(duaDon, 1).themDichVu(anSang, 2).build();
        anSang.setDonGia(new BigDecimal("999999"));
        duaDon.setDonGia(new BigDecimal("999999"));
        ChiPhiDatPhong chiPhi = GhepChiPhi.tuDonDatPhong(don);
        assertThat(chiPhi.tinhTien()).isEqualByComparingTo("2500000");
        assertThat(chiPhi.chiTiet()).extracting(DongChiPhi::noiDung).containsExactly(
                "Phòng 201 (Deluxe)", "Đưa đón sân bay Liên Khương", "Ăn sáng buffet");
    }

    @Test
    void nhieuPhongMoiPhongMotDongDungGiaVaSoDem() {
        Phong phong2 = phong();
        phong2.setMaPhong("P202");
        phong2.setSoPhong("202");
        phong2.getLoaiPhong().setGiaMoiDem(new BigDecimal("500000"));
        DatPhong don = builder().ngayTraPhong(HOM_NAY.plusDays(3))
                .themPhong(phong(), 2).themPhong(phong2, 1).build();
        ChiPhiDatPhong chiPhi = GhepChiPhi.tuDonDatPhong(don);
        assertThat(chiPhi.tinhTien()).isEqualByComparingTo("3900000");
        assertThat(chiPhi.chiTiet()).extracting(DongChiPhi::noiDung)
                .containsExactly("Phòng 201 (Deluxe)", "Phòng 202 (Deluxe)");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    void dichVuMotLanKhongNhanVoiSoDem(int soDem) {
        DatPhong don = builder().ngayTraPhong(HOM_NAY.plusDays(soDem))
                .themPhong(phong(), 2).themDichVu(duaDon(), 2).build();
        assertThat(GhepChiPhi.tuDonDatPhong(don).chiTiet().get(1).thanhTien())
                .isEqualByComparingTo("600000");
    }

    @Test
    void tinhBangBigDecimalKhongMatPhanLe() {
        var anSang = anSang();
        anSang.setDonGia(new BigDecimal("150000.25"));
        DatPhong don = builder().themPhong(phong(), 2).themDichVu(anSang, 2).build();
        assertThat(don.getTongTien()).isEqualByComparingTo("2200001.00");
        assertThat(GhepChiPhi.tuDonDatPhong(don).chiTiet().get(1).cachTinh())
                .isEqualTo("150.000,25 đ × 2 × 2 đêm");
    }
}
