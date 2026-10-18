package vn.edu.dlu.khachsan.pattern;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import vn.edu.dlu.khachsan.entity.CachTinhDichVu;
import vn.edu.dlu.khachsan.entity.DichVu;
import vn.edu.dlu.khachsan.entity.KhachHang;
import vn.edu.dlu.khachsan.entity.LoaiPhong;
import vn.edu.dlu.khachsan.entity.Phong;
import vn.edu.dlu.khachsan.entity.ThanhToan;
import vn.edu.dlu.khachsan.pattern.builder.DatPhongBuilder;

final class DuLieuKiemThu {

    static final Clock DONG_HO = Clock.fixed(Instant.parse("2026-10-18T17:30:00Z"), ZoneOffset.UTC);
    static final LocalDate HOM_NAY = LocalDate.of(2026, 10, 19);

    private DuLieuKiemThu() {
    }

    static DatPhongBuilder builder() {
        KhachHang khach = new KhachHang();
        khach.setMaKH("KH000001");
        return new DatPhongBuilder(DONG_HO).choKhachHang(khach)
                .ngayNhanPhong(HOM_NAY).ngayTraPhong(HOM_NAY.plusDays(2));
    }

    static Phong phong() {
        LoaiPhong loai = new LoaiPhong();
        loai.setMaLoaiPhong("LP002");
        loai.setTenLoaiPhong("Deluxe");
        loai.setGiaMoiDem(new BigDecimal("800000.00"));
        loai.setSucChua(2);
        Phong phong = new Phong();
        phong.setMaPhong("P201");
        phong.setSoPhong("201");
        phong.setLoaiPhong(loai);
        return phong;
    }

    static DichVu anSang() {
        DichVu dv = new DichVu();
        dv.setMaDichVu("DV01");
        dv.setTenDichVu("Ăn sáng buffet");
        dv.setDonGia(new BigDecimal("150000.00"));
        dv.setCachTinh(CachTinhDichVu.THEO_DEM);
        return dv;
    }

    static DichVu duaDon() {
        DichVu dv = new DichVu();
        dv.setMaDichVu("DV03");
        dv.setTenDichVu("Đưa đón sân bay Liên Khương");
        dv.setDonGia(new BigDecimal("300000.00"));
        dv.setCachTinh(CachTinhDichVu.MOT_LAN);
        return dv;
    }

    static ThanhToan giaoDich() {
        ThanhToan giaoDich = new ThanhToan();
        giaoDich.setMaThanhToan("TT00000001");
        giaoDich.setSoTien(new BigDecimal("1000000.00"));
        return giaoDich;
    }
}
