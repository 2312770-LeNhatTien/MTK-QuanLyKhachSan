package vn.edu.dlu.khachsan.pattern.state;

import java.util.Set;

import vn.edu.dlu.khachsan.entity.Phong;

/** Vai trò trong mẫu State: ConcreteState – trạng thái đang dọn và các chuyển đổi hợp lệ. */
public class PhongDangDon extends TrangThaiPhongCoBan {

    @Override
    public String getMa() {
        return "DANG_DON";
    }

    @Override
    public String getTenHienThi() {
        return "Đang dọn";
    }

    @Override
    public Set<ThaoTacPhong> thaoTacHopLe() {
        return Set.of(ThaoTacPhong.DON_XONG, ThaoTacPhong.BAO_TRI);
    }

    @Override
    public void donXong(Phong phong) {
        phong.chuyenSang(new PhongTrong());
    }

    @Override
    public void baoTri(Phong phong) {
        phong.chuyenSang(new PhongBaoTri());
    }
}

