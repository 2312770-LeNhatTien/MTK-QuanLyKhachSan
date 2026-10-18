package vn.edu.dlu.khachsan.pattern.decorator;

import java.math.BigDecimal;

import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.util.DinhDangTien;

/** Vai trò trong mẫu Decorator: ConcreteDecorator – dịch vụ tính theo số đêm lưu trú. */
public class DichVuTheoDem extends DichVuDecorator {

    private final long soDem;

    public DichVuTheoDem(ChiPhiDatPhong biBoc, String tenDichVu, BigDecimal donGia, int soLuong, long soDem) {
        super(biBoc, tenDichVu, donGia, soLuong);
        if (soDem <= 0) {
            throw new NghiepVuException("Số đêm lưu trú phải lớn hơn 0.");
        }
        this.soDem = soDem;
    }

    @Override
    protected BigDecimal tienDichVu() {
        return donGia.multiply(BigDecimal.valueOf(soLuong)).multiply(BigDecimal.valueOf(soDem));
    }

    @Override
    protected String moTaCachTinh() {
        return DinhDangTien.hienThi(donGia) + " × " + soLuong + " × " + soDem + " đêm";
    }
}
