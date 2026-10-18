package vn.edu.dlu.khachsan.pattern.strategy;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import vn.edu.dlu.khachsan.entity.ThanhToan;
import vn.edu.dlu.khachsan.entity.TrangThaiGiaoDich;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.util.DinhDangTien;

/** Vai trò trong mẫu Strategy: ConcreteStrategy – kiểm tra tiền khách đưa và tính tiền thối. */
@Component
public class ThanhToanTienMat implements PhuongThucThanhToan {

    @Override
    public String getMa() {
        return "TIEN_MAT";
    }

    @Override
    public String getTenHienThi() {
        return "Tiền mặt";
    }

    @Override
    public boolean dungTaiQuay() {
        return true;
    }

    @Override
    public KetQuaXuLy xuLy(ThanhToan giaoDich, DuLieuThanhToan duLieu) {
        if (giaoDich == null || giaoDich.getSoTien() == null || giaoDich.getSoTien().signum() <= 0) {
            throw new NghiepVuException("Số tiền thanh toán phải lớn hơn 0.");
        }
        if (duLieu == null || duLieu.tienKhachDua() == null) {
            throw new NghiepVuException("Vui lòng nhập số tiền khách đưa.");
        }
        if (duLieu.tienKhachDua().compareTo(giaoDich.getSoTien()) < 0) {
            throw new NghiepVuException("Số tiền khách đưa không đủ để thanh toán.");
        }
        BigDecimal tienThoi = duLieu.tienKhachDua().subtract(giaoDich.getSoTien());
        giaoDich.setGhiChu("Khách đưa " + DinhDangTien.hienThi(duLieu.tienKhachDua())
                + ", thối " + DinhDangTien.hienThi(tienThoi));
        return new KetQuaXuLy(TrangThaiGiaoDich.THANH_CONG,
                "Tiền thối lại: " + DinhDangTien.hienThi(tienThoi), null);
    }
}
