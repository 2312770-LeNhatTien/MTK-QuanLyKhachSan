package vn.edu.dlu.khachsan.pattern.strategy;

import org.springframework.stereotype.Component;

import vn.edu.dlu.khachsan.entity.ThanhToan;
import vn.edu.dlu.khachsan.entity.TrangThaiGiaoDich;
import vn.edu.dlu.khachsan.exception.NghiepVuException;

/** Vai trò trong mẫu Strategy: ConcreteStrategy – chuyển đến cổng giả lập và chờ kết quả. */
@Component
public class ThanhToanTrucTuyen implements PhuongThucThanhToan {

    @Override
    public String getMa() {
        return "TRUC_TUYEN";
    }

    @Override
    public String getTenHienThi() {
        return "Trực tuyến";
    }

    @Override
    public boolean dungTaiQuay() {
        return false;
    }

    @Override
    public KetQuaXuLy xuLy(ThanhToan giaoDich, DuLieuThanhToan duLieu) {
        if (giaoDich == null || giaoDich.getSoTien() == null || giaoDich.getSoTien().signum() <= 0) {
            throw new NghiepVuException("Số tiền thanh toán phải lớn hơn 0.");
        }
        if (giaoDich.getMaThanhToan() == null || giaoDich.getMaThanhToan().isBlank()) {
            throw new NghiepVuException("Giao dịch thanh toán chưa có mã.");
        }
        return new KetQuaXuLy(TrangThaiGiaoDich.DANG_XU_LY,
                "Vui lòng hoàn tất thanh toán tại cổng thanh toán giả lập.",
                "/thanh-toan/gia-lap/" + giaoDich.getMaThanhToan());
    }
}
