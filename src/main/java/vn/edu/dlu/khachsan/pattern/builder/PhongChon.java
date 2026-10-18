package vn.edu.dlu.khachsan.pattern.builder;

import vn.edu.dlu.khachsan.entity.Phong;

/** Vai trò trong mẫu Builder: dữ liệu phòng và số khách được chọn cho đơn. */
public record PhongChon(Phong phong, int soKhach) {
}
