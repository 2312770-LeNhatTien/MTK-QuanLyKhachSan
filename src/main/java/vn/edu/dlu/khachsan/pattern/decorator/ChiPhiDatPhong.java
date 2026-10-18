package vn.edu.dlu.khachsan.pattern.decorator;

import java.math.BigDecimal;
import java.util.List;

/** Vai trò trong mẫu Decorator: Component – tổng tiền và các dòng chi phí của đơn. */
public interface ChiPhiDatPhong {
    BigDecimal tinhTien();

    List<DongChiPhi> chiTiet();
}
