package vn.edu.dlu.khachsan.pattern.state;

import java.util.Set;

import vn.edu.dlu.khachsan.entity.Phong;

/** Vai trò trong mẫu State: ConcreteState – trạng thái đang có khách và các chuyển đổi hợp lệ. */
public class PhongCoKhach extends TrangThaiPhongCoBan {

    @Override
    public String getMa() {
        return "CO_KHACH";
    }

    @Override
    public String getTenHienThi() {
        return "Đang có khách";
    }

    @Override
    public Set<ThaoTacPhong> thaoTacHopLe() {
        return Set.of(ThaoTacPhong.CHECK_OUT);
    }

    @Override
    public void checkOut(Phong phong) {
        phong.chuyenSang(new PhongDangDon());
    }
}

