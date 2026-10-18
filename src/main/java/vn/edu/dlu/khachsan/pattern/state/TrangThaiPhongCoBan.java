package vn.edu.dlu.khachsan.pattern.state;

import vn.edu.dlu.khachsan.entity.Phong;
import vn.edu.dlu.khachsan.exception.NghiepVuException;

/** Vai trò trong mẫu State: lớp cơ bản từ chối thao tác, lớp con ghi đè hành vi hợp lệ. */
public abstract class TrangThaiPhongCoBan implements TrangThaiPhong {

    @Override
    public boolean coTheNhanDat() {
        return true;
    }

    @Override
    public void checkIn(Phong phong) {
        throw tuChoi(phong, "check-in");
    }

    @Override
    public void checkOut(Phong phong) {
        throw tuChoi(phong, "check-out");
    }

    @Override
    public void donXong(Phong phong) {
        throw tuChoi(phong, "dọn xong");
    }

    @Override
    public void baoTri(Phong phong) {
        throw tuChoi(phong, "bảo trì");
    }

    @Override
    public void ketThucBaoTri(Phong phong) {
        throw tuChoi(phong, "kết thúc bảo trì");
    }

    private NghiepVuException tuChoi(Phong phong, String thaoTac) {
        return new NghiepVuException("Phòng " + phong.getSoPhong() + " đang ở trạng thái "
                + getTenHienThi() + ", không thể " + thaoTac + ".");
    }
}
