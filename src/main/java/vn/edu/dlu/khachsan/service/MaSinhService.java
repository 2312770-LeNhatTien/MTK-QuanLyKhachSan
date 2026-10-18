package vn.edu.dlu.khachsan.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.repository.ChiTietDatPhongRepository;
import vn.edu.dlu.khachsan.repository.ChiTietDichVuRepository;
import vn.edu.dlu.khachsan.repository.DanhGiaRepository;
import vn.edu.dlu.khachsan.repository.DatPhongRepository;
import vn.edu.dlu.khachsan.repository.DichVuRepository;
import vn.edu.dlu.khachsan.repository.HoaDonRepository;
import vn.edu.dlu.khachsan.repository.KhachHangRepository;
import vn.edu.dlu.khachsan.repository.LoaiPhongRepository;
import vn.edu.dlu.khachsan.repository.NhanVienRepository;
import vn.edu.dlu.khachsan.repository.ThanhToanRepository;

@Service
@Transactional(propagation = Propagation.MANDATORY)
public class MaSinhService {

    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final LoaiPhongRepository loaiPhongRepository;
    private final DichVuRepository dichVuRepository;
    private final DatPhongRepository datPhongRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;
    private final ChiTietDichVuRepository chiTietDichVuRepository;
    private final HoaDonRepository hoaDonRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final DanhGiaRepository danhGiaRepository;
    private final ReentrantLock khoaSinhMa = new ReentrantLock();
    private final Object khoaTaiNguyen = new Object();

    public MaSinhService(KhachHangRepository khachHangRepository, NhanVienRepository nhanVienRepository,
            LoaiPhongRepository loaiPhongRepository, DichVuRepository dichVuRepository,
            DatPhongRepository datPhongRepository, ChiTietDatPhongRepository chiTietDatPhongRepository,
            ChiTietDichVuRepository chiTietDichVuRepository, HoaDonRepository hoaDonRepository,
            ThanhToanRepository thanhToanRepository, DanhGiaRepository danhGiaRepository) {
        this.khachHangRepository = khachHangRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.loaiPhongRepository = loaiPhongRepository;
        this.dichVuRepository = dichVuRepository;
        this.datPhongRepository = datPhongRepository;
        this.chiTietDatPhongRepository = chiTietDatPhongRepository;
        this.chiTietDichVuRepository = chiTietDichVuRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.thanhToanRepository = thanhToanRepository;
        this.danhGiaRepository = danhGiaRepository;
    }

    public String sinhMaKhachHang() {
        return sinhMa("KH", 6, khachHangRepository::timMaLonNhat);
    }

    public void khoaGiaoDich() {
        maDaCapTrongGiaoDich();
    }

    public String sinhMaNhanVien() {
        return sinhMa("NV", 4, nhanVienRepository::timMaLonNhat);
    }

    public String sinhMaLoaiPhong() {
        return sinhMa("LP", 3, loaiPhongRepository::timMaLonNhat);
    }

    public String sinhMaDichVu() {
        return sinhMa("DV", 2, dichVuRepository::timMaLonNhat);
    }

    public String sinhMaDatPhong() {
        return sinhMa("DP", 8, datPhongRepository::timMaLonNhat);
    }

    public String sinhMaChiTietDatPhong() {
        return sinhMa("CT", 8, chiTietDatPhongRepository::timMaLonNhat);
    }

    public String sinhMaChiTietDichVu() {
        return sinhMa("CV", 8, chiTietDichVuRepository::timMaLonNhat);
    }

    public String sinhMaHoaDon() {
        return sinhMa("HD", 8, hoaDonRepository::timMaLonNhat);
    }

    public String sinhMaThanhToan() {
        return sinhMa("TT", 8, thanhToanRepository::timMaLonNhat);
    }

    public String sinhMaDanhGia() {
        return sinhMa("DG", 8, danhGiaRepository::timMaLonNhat);
    }

    public String sinhMaPhong(String soPhong) {
        if (soPhong == null || !soPhong.matches("\\S{1,9}")) {
            throw new NghiepVuException("Số phòng phải gồm từ 1 đến 9 ký tự, không có khoảng trắng, để tạo mã phòng.");
        }
        return "P" + soPhong;
    }

    private String sinhMa(String tienTo, int soChuSo, Supplier<String> layMaLonNhat) {
        Map<String, Long> daCap = maDaCapTrongGiaoDich();
        String maLonNhat = layMaLonNhat.get();
        long lonNhat = 0;
        if (maLonNhat != null) {
            if (!maLonNhat.matches(tienTo + "[0-9]{" + soChuSo + "}")) {
                throw new NghiepVuException("Mã hiện có của " + tienTo + " không đúng định dạng.");
            }
            lonNhat = Long.parseLong(maLonNhat.substring(tienTo.length()));
        }
        long tiepTheo = Math.max(lonNhat, daCap.getOrDefault(tienTo, 0L)) + 1;
        if (Long.toString(tiepTheo).length() > soChuSo) {
            throw new NghiepVuException("Đã hết dải mã " + tienTo + ".");
        }
        daCap.put(tienTo, tiepTheo);
        return tienTo + String.format(Locale.ROOT, "%0" + soChuSo + "d", tiepTheo);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Long> maDaCapTrongGiaoDich() {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("Phải sinh mã trong transaction của thao tác lưu dữ liệu.");
        }
        Map<String, Long> daCap = (Map<String, Long>) TransactionSynchronizationManager.getResource(khoaTaiNguyen);
        if (daCap != null) {
            return daCap;
        }
        // Giữ khóa đến khi commit/rollback để hai transaction trong cùng ứng dụng không cấp trùng mã.
        khoaSinhMa.lock();
        Map<String, Long> moi = new HashMap<>();
        TransactionSynchronizationManager.bindResource(khoaTaiNguyen, moi);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                TransactionSynchronizationManager.unbindResourceIfPossible(khoaTaiNguyen);
                khoaSinhMa.unlock();
            }
        });
        return moi;
    }
}
