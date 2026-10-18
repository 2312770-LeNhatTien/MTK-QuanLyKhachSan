package vn.edu.dlu.khachsan.pattern.state;

import java.util.Map;
import java.util.Set;

import vn.edu.dlu.khachsan.entity.Phong;
import vn.edu.dlu.khachsan.exception.NghiepVuException;

/** Vai trò trong mẫu State: State – quy định hành vi và thao tác hợp lệ của phòng. */
public interface TrangThaiPhong {

    Map<String, TrangThaiPhong> CAC_TRANG_THAI = Map.of(
            "TRONG", new PhongTrong(),
            "CO_KHACH", new PhongCoKhach(),
            "DANG_DON", new PhongDangDon(),
            "BAO_TRI", new PhongBaoTri());

    String getMa();

    String getTenHienThi();

    boolean coTheNhanDat();

    Set<ThaoTacPhong> thaoTacHopLe();

    void checkIn(Phong phong);

    void checkOut(Phong phong);

    void donXong(Phong phong);

    void baoTri(Phong phong);

    void ketThucBaoTri(Phong phong);

    static TrangThaiPhong tuMa(String ma) {
        TrangThaiPhong trangThai = ma == null ? null : CAC_TRANG_THAI.get(ma);
        if (trangThai == null) {
            throw new NghiepVuException("Trạng thái phòng không hợp lệ.");
        }
        return trangThai;
    }
}
