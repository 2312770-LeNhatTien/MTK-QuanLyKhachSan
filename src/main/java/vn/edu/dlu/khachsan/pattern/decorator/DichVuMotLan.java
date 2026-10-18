package vn.edu.dlu.khachsan.pattern.decorator;

import java.math.BigDecimal;

import vn.edu.dlu.khachsan.util.DinhDangTien;

/** Vai trò trong mẫu Decorator: ConcreteDecorator – dịch vụ chỉ tính một lần theo số lượng. */
public class DichVuMotLan extends DichVuDecorator {

    public DichVuMotLan(ChiPhiDatPhong biBoc, String tenDichVu, BigDecimal donGia, int soLuong) {
        super(biBoc, tenDichVu, donGia, soLuong);
    }

    @Override
    protected BigDecimal tienDichVu() {
        return donGia.multiply(BigDecimal.valueOf(soLuong));
    }

    @Override
    protected String moTaCachTinh() {
        return DinhDangTien.hienThi(donGia) + " × " + soLuong;
    }
}
