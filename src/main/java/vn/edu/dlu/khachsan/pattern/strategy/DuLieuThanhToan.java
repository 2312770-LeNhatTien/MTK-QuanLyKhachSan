package vn.edu.dlu.khachsan.pattern.strategy;

import java.math.BigDecimal;

/** Vai trò trong mẫu Strategy: dữ liệu bổ sung do khách hoặc lễ tân nhập khi thanh toán. */
public record DuLieuThanhToan(BigDecimal tienKhachDua, String maChuanChi) {
}
