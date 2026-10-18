package vn.edu.dlu.khachsan.pattern.strategy;

import vn.edu.dlu.khachsan.entity.TrangThaiGiaoDich;

/** Vai trò trong mẫu Strategy: kết quả xử lý và đường dẫn chuyển hướng của phương thức. */
public record KetQuaXuLy(TrangThaiGiaoDich trangThai, String thongBao, String urlChuyenHuong) {
}
