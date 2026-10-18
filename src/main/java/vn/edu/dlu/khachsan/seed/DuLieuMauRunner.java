package vn.edu.dlu.khachsan.seed;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.dlu.khachsan.entity.CachTinhDichVu;
import vn.edu.dlu.khachsan.entity.DatPhong;
import vn.edu.dlu.khachsan.entity.DichVu;
import vn.edu.dlu.khachsan.entity.HoaDon;
import vn.edu.dlu.khachsan.entity.KhachHang;
import vn.edu.dlu.khachsan.entity.LoaiPhong;
import vn.edu.dlu.khachsan.entity.NhanVien;
import vn.edu.dlu.khachsan.entity.Phong;
import vn.edu.dlu.khachsan.entity.ThamSo;
import vn.edu.dlu.khachsan.entity.ThanhToan;
import vn.edu.dlu.khachsan.entity.TrangThaiDatPhong;
import vn.edu.dlu.khachsan.entity.TrangThaiGiaoDich;
import vn.edu.dlu.khachsan.entity.TrangThaiThanhToan;
import vn.edu.dlu.khachsan.entity.VaiTro;
import vn.edu.dlu.khachsan.pattern.builder.DatPhongBuilder;
import vn.edu.dlu.khachsan.pattern.strategy.ThanhToanTrucTuyen;
import vn.edu.dlu.khachsan.repository.DatPhongRepository;
import vn.edu.dlu.khachsan.repository.DichVuRepository;
import vn.edu.dlu.khachsan.repository.HoaDonRepository;
import vn.edu.dlu.khachsan.repository.KhachHangRepository;
import vn.edu.dlu.khachsan.repository.LoaiPhongRepository;
import vn.edu.dlu.khachsan.repository.NhanVienRepository;
import vn.edu.dlu.khachsan.repository.PhongRepository;
import vn.edu.dlu.khachsan.repository.ThamSoRepository;
import vn.edu.dlu.khachsan.repository.ThanhToanRepository;
import vn.edu.dlu.khachsan.repository.VaiTroRepository;
import vn.edu.dlu.khachsan.service.MaSinhService;
import vn.edu.dlu.khachsan.service.ThamSoService;

@Component
public class DuLieuMauRunner implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DuLieuMauRunner.class);
    private static final ZoneId MUI_GIO = ZoneId.of("Asia/Ho_Chi_Minh");
    private final VaiTroRepository vaiTroRepository;
    private final NhanVienRepository nhanVienRepository;
    private final KhachHangRepository khachHangRepository;
    private final LoaiPhongRepository loaiPhongRepository;
    private final PhongRepository phongRepository;
    private final DichVuRepository dichVuRepository;
    private final DatPhongRepository datPhongRepository;
    private final HoaDonRepository hoaDonRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final ThamSoRepository thamSoRepository;
    private final MaSinhService maSinhService;
    private final ThamSoService thamSoService;
    private final PasswordEncoder passwordEncoder;
    private final ThanhToanTrucTuyen thanhToanTrucTuyen;

    public DuLieuMauRunner(VaiTroRepository vaiTroRepository, NhanVienRepository nhanVienRepository,
            KhachHangRepository khachHangRepository, LoaiPhongRepository loaiPhongRepository,
            PhongRepository phongRepository, DichVuRepository dichVuRepository,
            DatPhongRepository datPhongRepository, HoaDonRepository hoaDonRepository,
            ThanhToanRepository thanhToanRepository, ThamSoRepository thamSoRepository,
            MaSinhService maSinhService, ThamSoService thamSoService,
            PasswordEncoder passwordEncoder, ThanhToanTrucTuyen thanhToanTrucTuyen) {
        this.vaiTroRepository = vaiTroRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.khachHangRepository = khachHangRepository;
        this.loaiPhongRepository = loaiPhongRepository;
        this.phongRepository = phongRepository;
        this.dichVuRepository = dichVuRepository;
        this.datPhongRepository = datPhongRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.thanhToanRepository = thanhToanRepository;
        this.thamSoRepository = thamSoRepository;
        this.maSinhService = maSinhService;
        this.thamSoService = thamSoService;
        this.passwordEncoder = passwordEncoder;
        this.thanhToanTrucTuyen = thanhToanTrucTuyen;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (nhanVienRepository.count() != 0) {
            LOG.info("Bảng NHAN_VIEN đã có dữ liệu, bỏ qua khởi tạo dữ liệu mẫu.");
            return;
        }

        LocalDate homNay = LocalDate.now(MUI_GIO);
        taoThamSo(ThamSoService.THOI_GIAN_GIU_PHONG, "Thời gian giữ phòng chờ thanh toán", "15", "phút");
        taoThamSo(ThamSoService.TI_LE_DAT_COC, "Tỉ lệ đặt cọc tối thiểu", "30", "%");
        taoThamSo(ThamSoService.THOI_HAN_HUY_DON, "Số ngày tối thiểu trước ngày nhận phòng được hủy đơn", "1", "ngày");

        VaiTro leTan = taoVaiTro("VT01", "Lễ tân");
        VaiTro quanLy = taoVaiTro("VT02", "Quản lý");
        VaiTro buongPhong = taoVaiTro("VT03", "Buồng phòng");
        NhanVien nhanVien = taoNhanVien("Trần Thị Lễ Tân", "letan@khachsan.vn", leTan);
        taoNhanVien("Nguyễn Văn Quản Lý", "quanly@khachsan.vn", quanLy);
        taoNhanVien("Lê Thị Buồng Phòng", "buongphong@khachsan.vn", buongPhong);
        KhachHang khach = taoKhachHang(homNay);

        LoaiPhong standard = taoLoaiPhong("Standard", "500000", 2);
        LoaiPhong deluxe = taoLoaiPhong("Deluxe", "800000", 2);
        LoaiPhong family = taoLoaiPhong("Family", "1200000", 4);
        Map<String, Phong> dsPhong = new LinkedHashMap<>();
        taoPhongTheoTang(dsPhong, 1, standard);
        taoPhongTheoTang(dsPhong, 2, deluxe);
        taoPhongTheoTang(dsPhong, 3, family);
        dsPhong.get("104").checkIn();
        dsPhong.get("104").checkOut();
        dsPhong.get("304").baoTri();

        DichVu anSang = taoDichVu("Ăn sáng buffet", "150000", CachTinhDichVu.THEO_DEM, "người/đêm");
        taoDichVu("Giường phụ", "200000", CachTinhDichVu.THEO_DEM, "giường/đêm");
        DichVu duaDon = taoDichVu("Đưa đón sân bay Liên Khương", "300000", CachTinhDichVu.MOT_LAN, "lượt");
        taoDichVu("Giặt ủi", "80000", CachTinhDichVu.MOT_LAN, "lần");

        DatPhong hoanTat = luuDon(builderMau(khach, homNay.minusDays(5), homNay.minusDays(3))
                .themPhong(dsPhong.get("101"), 2).themDichVu(anSang, 2));
        hoanTat.setNhanVien(nhanVien);
        hoanTat.setTrangThai(TrangThaiDatPhong.DA_XAC_NHAN);
        dsPhong.get("101").checkIn();
        taoHoaDonVaThanhToan(hoanTat, true);
        dsPhong.get("101").checkOut();
        dsPhong.get("101").donXong();
        hoanTat.setTrangThai(TrangThaiDatPhong.HOAN_TAT);

        DatPhong choNhanPhong = luuDon(builderMau(khach, homNay, homNay.plusDays(2))
                .themPhong(dsPhong.get("201"), 2));
        taoHoaDonVaThanhToan(choNhanPhong, false);
        choNhanPhong.setTrangThai(TrangThaiDatPhong.DA_XAC_NHAN);

        DatPhong dangO = luuDon(builderMau(khach, homNay.minusDays(1), homNay.plusDays(1))
                .themPhong(dsPhong.get("202"), 2).themDichVu(duaDon, 1));
        dangO.setNhanVien(nhanVien);
        taoHoaDonVaThanhToan(dangO, false);
        dangO.setTrangThai(TrangThaiDatPhong.DA_XAC_NHAN);
        dsPhong.get("202").checkIn();

        DatPhong daHuy = luuDon(builderMau(khach, homNay.plusDays(3), homNay.plusDays(4))
                .themPhong(dsPhong.get("102"), 1));
        daHuy.setTrangThai(TrangThaiDatPhong.DA_HUY);

        // Flush trong transaction để lỗi ở bất kỳ bảng nào đều rollback toàn bộ dữ liệu mẫu.
        datPhongRepository.flush();
        LOG.info("Đã tạo dữ liệu mẫu: 3 nhân viên, 1 khách hàng, 12 phòng và 4 đơn đặt phòng.");
    }

    private void taoThamSo(String ma, String ten, String giaTri, String donVi) {
        ThamSo thamSo = new ThamSo();
        thamSo.setMaThamSo(ma);
        thamSo.setTenThamSo(ten);
        thamSo.setGiaTri(new BigDecimal(giaTri));
        thamSo.setDonVi(donVi);
        thamSoRepository.save(thamSo);
    }

    private VaiTro taoVaiTro(String ma, String ten) {
        VaiTro vaiTro = new VaiTro();
        vaiTro.setMaVaiTro(ma);
        vaiTro.setTenVaiTro(ten);
        return vaiTroRepository.save(vaiTro);
    }

    private NhanVien taoNhanVien(String ten, String email, VaiTro vaiTro) {
        NhanVien nv = new NhanVien();
        nv.setMaNV(maSinhService.sinhMaNhanVien());
        nv.setHoTen(ten);
        nv.setEmail(email);
        nv.setMatKhau(passwordEncoder.encode("123456"));
        nv.setVaiTro(vaiTro);
        return nhanVienRepository.save(nv);
    }

    private KhachHang taoKhachHang(LocalDate homNay) {
        KhachHang khach = new KhachHang();
        khach.setMaKH(maSinhService.sinhMaKhachHang());
        khach.setHoTen("Nguyễn Văn A");
        khach.setEmail("khach@gmail.com");
        khach.setMatKhau(passwordEncoder.encode("123456"));
        khach.setNgayTao(homNay.minusDays(30).atTime(9, 0));
        return khachHangRepository.save(khach);
    }

    private LoaiPhong taoLoaiPhong(String ten, String gia, int sucChua) {
        LoaiPhong loai = new LoaiPhong();
        loai.setMaLoaiPhong(maSinhService.sinhMaLoaiPhong());
        loai.setTenLoaiPhong(ten);
        loai.setGiaMoiDem(new BigDecimal(gia));
        loai.setSucChua(sucChua);
        return loaiPhongRepository.save(loai);
    }

    private void taoPhongTheoTang(Map<String, Phong> dsPhong, int tang, LoaiPhong loai) {
        for (int thuTu = 1; thuTu <= 4; thuTu++) {
            String soPhong = Integer.toString(tang * 100 + thuTu);
            Phong phong = new Phong();
            phong.setMaPhong(maSinhService.sinhMaPhong(soPhong));
            phong.setSoPhong(soPhong);
            phong.setTang(tang);
            phong.setLoaiPhong(loai);
            dsPhong.put(soPhong, phongRepository.save(phong));
        }
    }

    private DichVu taoDichVu(String ten, String donGia, CachTinhDichVu cachTinh, String donVi) {
        DichVu dv = new DichVu();
        dv.setMaDichVu(maSinhService.sinhMaDichVu());
        dv.setTenDichVu(ten);
        dv.setDonGia(new BigDecimal(donGia));
        dv.setCachTinh(cachTinh);
        dv.setDonViTinh(donVi);
        return dichVuRepository.save(dv);
    }

    private DatPhongBuilder builderMau(KhachHang khach, LocalDate ngayNhan, LocalDate ngayTra) {
        LocalDate ngayDatMau = ngayNhan.minusDays(1);
        LocalDate homNay = LocalDate.now(MUI_GIO);
        if (!ngayDatMau.isBefore(homNay)) {
            ngayDatMau = homNay.minusDays(1);
        }
        LocalDateTime ngayDat = ngayDatMau.atTime(9, 0);
        Clock dongHoMau = Clock.fixed(ngayDat.atZone(MUI_GIO).toInstant(), MUI_GIO);
        return new DatPhongBuilder(dongHoMau).choKhachHang(khach)
                .ngayNhanPhong(ngayNhan).ngayTraPhong(ngayTra);
    }

    private DatPhong luuDon(DatPhongBuilder builder) {
        DatPhong don = builder.build();
        don.setMaDatPhong(maSinhService.sinhMaDatPhong());
        don.getDsChiTietDatPhong().forEach(ct -> ct.setMaCTDP(maSinhService.sinhMaChiTietDatPhong()));
        don.getDsChiTietDichVu().forEach(ct -> ct.setMaCTDV(maSinhService.sinhMaChiTietDichVu()));
        return datPhongRepository.save(don);
    }

    private void taoHoaDonVaThanhToan(DatPhong don, boolean thanhToanDu) {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(maSinhService.sinhMaHoaDon());
        hoaDon.setDatPhong(don);
        hoaDon.setNgayLap(don.getNgayDat().plusMinutes(3));
        hoaDon.setTongTien(don.getTongTien());
        hoaDon.setTrangThaiThanhToan(thanhToanDu
                ? TrangThaiThanhToan.DA_HOAN_TAT : TrangThaiThanhToan.DA_COC);
        hoaDon = hoaDonRepository.save(hoaDon);

        ThanhToan giaoDich = new ThanhToan();
        giaoDich.setMaThanhToan(maSinhService.sinhMaThanhToan());
        giaoDich.setHoaDon(hoaDon);
        giaoDich.setPhuongThuc(thanhToanTrucTuyen.getMa());
        giaoDich.setSoTien(thanhToanDu ? don.getTongTien() : don.getTongTien()
                .multiply(thamSoService.tiLeDatCoc()).divide(new BigDecimal("100"), 2, RoundingMode.CEILING));
        giaoDich.setNgayThanhToan(don.getNgayDat().plusMinutes(5));
        giaoDich.setTrangThai(thanhToanTrucTuyen.xuLy(giaoDich, null).trangThai());
        // Dữ liệu lịch sử giả lập đã nhận kết quả thành công, không gọi cổng hay gửi email.
        giaoDich.setTrangThai(TrangThaiGiaoDich.THANH_CONG);
        giaoDich.setGhiChu("Giao dịch thành công trong dữ liệu mẫu.");
        thanhToanRepository.save(giaoDich);
    }
}
