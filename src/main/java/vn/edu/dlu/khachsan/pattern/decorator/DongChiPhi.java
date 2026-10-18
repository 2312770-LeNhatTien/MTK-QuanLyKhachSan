package vn.edu.dlu.khachsan.pattern.decorator;

import java.math.BigDecimal;

/** Vai trò trong mẫu Decorator: dữ liệu một dòng chi phí hiển thị trên hóa đơn. */
public record DongChiPhi(String noiDung, String cachTinh, BigDecimal thanhTien) {
}
