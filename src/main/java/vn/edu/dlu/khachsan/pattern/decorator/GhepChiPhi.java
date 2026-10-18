package vn.edu.dlu.khachsan.pattern.decorator;

import java.time.temporal.ChronoUnit;

import vn.edu.dlu.khachsan.entity.CachTinhDichVu;
import vn.edu.dlu.khachsan.entity.ChiTietDichVu;
import vn.edu.dlu.khachsan.entity.DatPhong;
import vn.edu.dlu.khachsan.exception.NghiepVuException;

/** Vai trò trong mẫu Decorator: ghép tiền phòng và lần lượt bọc các dịch vụ theo thứ tự chọn. */
public final class GhepChiPhi {

    private GhepChiPhi() {
    }

    public static ChiPhiDatPhong tuDonDatPhong(DatPhong don) {
        long soDem = ChronoUnit.DAYS.between(don.getNgayNhanPhong(), don.getNgayTraPhong());
        ChiPhiDatPhong chiPhi = new TienPhong(don.getDsChiTietDatPhong(), soDem);
        for (ChiTietDichVu ct : don.getDsChiTietDichVu()) {
            CachTinhDichVu cachTinh = ct.getDichVu().getCachTinh();
            if (cachTinh == CachTinhDichVu.THEO_DEM) {
                chiPhi = new DichVuTheoDem(chiPhi, ct.getDichVu().getTenDichVu(),
                        ct.getDonGiaThoiDiemDat(), ct.getSoLuong(), soDem);
            } else if (cachTinh == CachTinhDichVu.MOT_LAN) {
                chiPhi = new DichVuMotLan(chiPhi, ct.getDichVu().getTenDichVu(),
                        ct.getDonGiaThoiDiemDat(), ct.getSoLuong());
            } else {
                throw new NghiepVuException("Cách tính tiền dịch vụ không hợp lệ.");
            }
        }
        return chiPhi;
    }
}
