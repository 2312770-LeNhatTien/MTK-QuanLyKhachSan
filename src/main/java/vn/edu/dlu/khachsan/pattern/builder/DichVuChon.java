package vn.edu.dlu.khachsan.pattern.builder;

import vn.edu.dlu.khachsan.entity.DichVu;

/** Vai trò trong mẫu Builder: dữ liệu dịch vụ và số lượng được chọn cho đơn. */
public record DichVuChon(DichVu dichVu, int soLuong) {
}
