package vn.edu.dlu.khachsan.pattern.decorator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import vn.edu.dlu.khachsan.exception.NghiepVuException;

/** Vai trò trong mẫu Decorator: Decorator trừu tượng – bọc thêm một dịch vụ vào chi phí. */
public abstract class DichVuDecorator implements ChiPhiDatPhong {

    protected final ChiPhiDatPhong biBoc;
    protected final String tenDichVu;
    protected final BigDecimal donGia;
    protected final int soLuong;

    protected DichVuDecorator(ChiPhiDatPhong biBoc, String tenDichVu, BigDecimal donGia, int soLuong) {
        if (biBoc == null || tenDichVu == null || tenDichVu.isBlank()
                || donGia == null || donGia.signum() <= 0 || soLuong < 1 || soLuong > 10) {
            throw new NghiepVuException("Thông tin dịch vụ không hợp lệ; số lượng phải từ 1 đến 10.");
        }
        this.biBoc = biBoc;
        this.tenDichVu = tenDichVu;
        this.donGia = donGia;
        this.soLuong = soLuong;
    }

    @Override
    public BigDecimal tinhTien() {
        return biBoc.tinhTien().add(tienDichVu());
    }

    @Override
    public List<DongChiPhi> chiTiet() {
        List<DongChiPhi> ds = new ArrayList<>(biBoc.chiTiet());
        ds.add(new DongChiPhi(tenDichVu, moTaCachTinh(), tienDichVu()));
        return ds;
    }

    protected abstract BigDecimal tienDichVu();

    protected abstract String moTaCachTinh();
}
