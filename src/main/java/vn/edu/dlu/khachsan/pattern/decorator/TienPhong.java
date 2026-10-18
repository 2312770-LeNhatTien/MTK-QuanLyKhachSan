package vn.edu.dlu.khachsan.pattern.decorator;

import java.math.BigDecimal;
import java.util.List;

import vn.edu.dlu.khachsan.entity.ChiTietDatPhong;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.util.DinhDangTien;

/** Vai trò trong mẫu Decorator: ConcreteComponent – tiền phòng dựa trên giá đã chốt. */
public class TienPhong implements ChiPhiDatPhong {

    private final List<ChiTietDatPhong> dsChiTiet;
    private final long soDem;

    public TienPhong(List<ChiTietDatPhong> dsChiTiet, long soDem) {
        if (dsChiTiet == null || dsChiTiet.isEmpty() || soDem <= 0) {
            throw new NghiepVuException("Cần ít nhất một phòng và số đêm lưu trú phải lớn hơn 0.");
        }
        this.dsChiTiet = List.copyOf(dsChiTiet);
        this.soDem = soDem;
    }

    @Override
    public BigDecimal tinhTien() {
        return dsChiTiet.stream()
                .map(ct -> ct.getGiaThoiDiemDat().multiply(BigDecimal.valueOf(soDem)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<DongChiPhi> chiTiet() {
        return dsChiTiet.stream().map(ct -> new DongChiPhi(
                "Phòng " + ct.getPhong().getSoPhong() + " ("
                        + ct.getPhong().getLoaiPhong().getTenLoaiPhong() + ")",
                DinhDangTien.hienThi(ct.getGiaThoiDiemDat()) + " × " + soDem + " đêm",
                ct.getGiaThoiDiemDat().multiply(BigDecimal.valueOf(soDem)))).toList();
    }
}
