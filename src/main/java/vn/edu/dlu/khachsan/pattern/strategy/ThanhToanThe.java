package vn.edu.dlu.khachsan.pattern.strategy;

import org.springframework.stereotype.Component;

import vn.edu.dlu.khachsan.entity.ThanhToan;
import vn.edu.dlu.khachsan.entity.TrangThaiGiaoDich;
import vn.edu.dlu.khachsan.exception.NghiepVuException;

/** Vai trò trong mẫu Strategy: ConcreteStrategy – xác thực mã chuẩn chi POS giả lập. */
@Component
public class ThanhToanThe implements PhuongThucThanhToan {

    @Override
    public String getMa() {
        return "THE";
    }

    @Override
    public String getTenHienThi() {
        return "Thẻ ngân hàng";
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
        if (duLieu == null || duLieu.maChuanChi() == null || !duLieu.maChuanChi().matches("[0-9]{6}")) {
            throw new NghiepVuException("Mã chuẩn chi phải gồm đúng 6 chữ số.");
        }
        giaoDich.setGhiChu("Mã chuẩn chi: " + duLieu.maChuanChi());
        return new KetQuaXuLy(TrangThaiGiaoDich.THANH_CONG, "Thanh toán bằng thẻ thành công.", null);
    }
}
