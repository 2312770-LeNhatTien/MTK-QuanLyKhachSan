package vn.edu.dlu.khachsan.pattern.builder;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import vn.edu.dlu.khachsan.entity.ChiTietDatPhong;
import vn.edu.dlu.khachsan.entity.ChiTietDichVu;
import vn.edu.dlu.khachsan.entity.DatPhong;
import vn.edu.dlu.khachsan.entity.DichVu;
import vn.edu.dlu.khachsan.entity.KhachHang;
import vn.edu.dlu.khachsan.entity.Phong;
import vn.edu.dlu.khachsan.entity.TrangThaiDatPhong;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.pattern.decorator.GhepChiPhi;

/** Vai trò trong mẫu Builder: Builder – kiểm tra, chốt giá và tạo đơn từng bước. */
public class DatPhongBuilder {

    private final Clock dongHo;
    private KhachHang khachHang;
    private LocalDate ngayNhanPhong;
    private LocalDate ngayTraPhong;
    private final List<PhongChon> dsPhong = new ArrayList<>();
    private final List<DichVuChon> dsDichVu = new ArrayList<>();

    public DatPhongBuilder() {
        this(Clock.system(ZoneId.of("Asia/Ho_Chi_Minh")));
    }

    public DatPhongBuilder(Clock dongHo) {
        this.dongHo = Objects.requireNonNull(dongHo).withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
    }

    public DatPhongBuilder choKhachHang(KhachHang kh) {
        this.khachHang = kh;
        return this;
    }

    public DatPhongBuilder ngayNhanPhong(LocalDate ngay) {
        this.ngayNhanPhong = ngay;
        return this;
    }

    public DatPhongBuilder ngayTraPhong(LocalDate ngay) {
        this.ngayTraPhong = ngay;
        return this;
    }

    public DatPhongBuilder themPhong(Phong phong, int soKhach) {
        dsPhong.add(new PhongChon(phong, soKhach));
        return this;
    }

    public DatPhongBuilder themDichVu(DichVu dichVu, int soLuong) {
        dsDichVu.add(new DichVuChon(dichVu, soLuong));
        return this;
    }

    public DatPhong build() {
        kiemTraDuLieu();

        DatPhong don = new DatPhong();
        don.setKhachHang(khachHang);
        don.setNgayNhanPhong(ngayNhanPhong);
        don.setNgayTraPhong(ngayTraPhong);
        don.setNgayDat(LocalDateTime.now(dongHo));
        don.setTrangThai(TrangThaiDatPhong.CHO_THANH_TOAN);

        // Chốt giá vào chi tiết để thay đổi giá danh mục không ảnh hưởng đơn cũ.
        for (PhongChon chon : dsPhong) {
            ChiTietDatPhong ct = new ChiTietDatPhong();
            ct.setDatPhong(don);
            ct.setPhong(chon.phong());
            ct.setSoLuongKhach(chon.soKhach());
            ct.setGiaThoiDiemDat(chon.phong().getLoaiPhong().getGiaMoiDem());
            don.getDsChiTietDatPhong().add(ct);
        }
        for (DichVuChon chon : dsDichVu) {
            ChiTietDichVu ct = new ChiTietDichVu();
            ct.setDatPhong(don);
            ct.setDichVu(chon.dichVu());
            ct.setSoLuong(chon.soLuong());
            ct.setDonGiaThoiDiemDat(chon.dichVu().getDonGia());
            don.getDsChiTietDichVu().add(ct);
        }
        don.setTongTien(GhepChiPhi.tuDonDatPhong(don).tinhTien());
        return don;
    }

    private void kiemTraDuLieu() {
        if (khachHang == null) {
            throw new NghiepVuException("Vui lòng chọn khách hàng đặt phòng.");
        }
        if (ngayNhanPhong == null || ngayTraPhong == null) {
            throw new NghiepVuException("Vui lòng chọn ngày nhận phòng và ngày trả phòng.");
        }
        if (ngayNhanPhong.isBefore(LocalDate.now(dongHo))) {
            throw new NghiepVuException("Ngày nhận phòng không được trước hôm nay.");
        }
        if (!ngayTraPhong.isAfter(ngayNhanPhong)) {
            throw new NghiepVuException("Ngày trả phòng phải sau ngày nhận phòng.");
        }
        if (dsPhong.isEmpty()) {
            throw new NghiepVuException("Vui lòng chọn ít nhất một phòng.");
        }
        kiemTraPhong();
        kiemTraDichVu();
    }

    private void kiemTraPhong() {
        Set<Phong> phongDaChon = new HashSet<>();
        Set<String> maPhongDaChon = new HashSet<>();
        for (PhongChon chon : dsPhong) {
            Phong phong = chon.phong();
            if (phong == null || phong.getLoaiPhong() == null
                    || phong.getLoaiPhong().getGiaMoiDem() == null
                    || phong.getLoaiPhong().getGiaMoiDem().signum() <= 0) {
                throw new NghiepVuException("Thông tin phòng hoặc giá phòng không hợp lệ.");
            }
            if (!phongDaChon.add(phong)
                    || (phong.getMaPhong() != null && !maPhongDaChon.add(phong.getMaPhong()))) {
                throw new NghiepVuException("Phòng " + phong.getSoPhong() + " đã được chọn trong đơn.");
            }
            if (chon.soKhach() < 1 || chon.soKhach() > phong.getLoaiPhong().getSucChua()) {
                throw new NghiepVuException("Số khách của phòng " + phong.getSoPhong()
                        + " phải từ 1 đến " + phong.getLoaiPhong().getSucChua() + ".");
            }
            if (!phong.layTrangThai().coTheNhanDat()) {
                throw new NghiepVuException("Phòng " + phong.getSoPhong() + " đang bảo trì, không thể đặt.");
            }
        }
    }

    private void kiemTraDichVu() {
        Set<DichVu> dichVuDaChon = new HashSet<>();
        Set<String> maDichVuDaChon = new HashSet<>();
        for (DichVuChon chon : dsDichVu) {
            DichVu dichVu = chon.dichVu();
            if (dichVu == null || dichVu.getCachTinh() == null
                    || dichVu.getTenDichVu() == null || dichVu.getTenDichVu().isBlank()
                    || dichVu.getDonGia() == null || dichVu.getDonGia().signum() <= 0) {
                throw new NghiepVuException("Thông tin dịch vụ không hợp lệ.");
            }
            if (!dichVuDaChon.add(dichVu)
                    || (dichVu.getMaDichVu() != null && !maDichVuDaChon.add(dichVu.getMaDichVu()))) {
                throw new NghiepVuException("Mỗi dịch vụ chỉ được chọn một lần trong đơn.");
            }
            if (chon.soLuong() < 1 || chon.soLuong() > 10) {
                throw new NghiepVuException("Số lượng dịch vụ phải từ 1 đến 10.");
            }
        }
    }
}
