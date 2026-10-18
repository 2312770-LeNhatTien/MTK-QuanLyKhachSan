package vn.edu.dlu.khachsan.pattern.state;

import java.util.Set;

import vn.edu.dlu.khachsan.entity.Phong;

/** Vai trò trong mẫu State: ConcreteState – trạng thái trống và các chuyển đổi hợp lệ. */
public class PhongTrong extends TrangThaiPhongCoBan {

    @Override
    public String getMa() {
        return "TRONG";
    }

    @Override
    public String getTenHienThi() {
        return "Trống";
    }

    @Override
    public Set<ThaoTacPhong> thaoTacHopLe() {
        return Set.of(ThaoTacPhong.CHECK_IN, ThaoTacPhong.BAO_TRI);
    }

    @Override
    public void checkIn(Phong phong) {
        phong.chuyenSang(new PhongCoKhach());
    }

    @Override
    public void baoTri(Phong phong) {
        phong.chuyenSang(new PhongBaoTri());
    }
}

