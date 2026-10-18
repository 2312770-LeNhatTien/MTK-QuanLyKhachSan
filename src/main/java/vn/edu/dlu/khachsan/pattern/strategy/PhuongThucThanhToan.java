package vn.edu.dlu.khachsan.pattern.strategy;

import vn.edu.dlu.khachsan.entity.ThanhToan;

/** Vai trò trong mẫu Strategy: Strategy – giao diện chung cho từng phương thức thanh toán. */
public interface PhuongThucThanhToan {
    String getMa();

    String getTenHienThi();

    boolean dungTaiQuay();

    KetQuaXuLy xuLy(ThanhToan giaoDich, DuLieuThanhToan duLieu);
}
