package vn.edu.dlu.khachsan.pattern.state;

import java.util.Set;

import vn.edu.dlu.khachsan.entity.Phong;

/** Vai trò trong mẫu State: ConcreteState – trạng thái bảo trì và các chuyển đổi hợp lệ. */
public class PhongBaoTri extends TrangThaiPhongCoBan {

    @Override
    public String getMa() {
        return "BAO_TRI";
    }

    @Override
    public String getTenHienThi() {
        return "Bảo trì";
    }

    @Override
    public Set<ThaoTacPhong> thaoTacHopLe() {
        return Set.of(ThaoTacPhong.KET_THUC_BAO_TRI);
    }

    @Override
    public boolean coTheNhanDat() {
        return false;
    }

    @Override
    public void ketThucBaoTri(Phong phong) {
        phong.chuyenSang(new PhongTrong());
    }
}

