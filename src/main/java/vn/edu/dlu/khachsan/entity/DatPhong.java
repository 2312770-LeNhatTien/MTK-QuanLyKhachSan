package vn.edu.dlu.khachsan.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "DAT_PHONG")
public class DatPhong {

    @Id
    @Column(name = "MaDatPhong", length = 10, nullable = false)
    private String maDatPhong;

    @Column(name = "NgayDat", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime ngayDat;

    @Column(name = "NgayNhanPhong", nullable = false)
    private LocalDate ngayNhanPhong;

    @Column(name = "NgayTraPhong", nullable = false)
    private LocalDate ngayTraPhong;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiDatPhong trangThai = TrangThaiDatPhong.CHO_THANH_TOAN;

    @Column(name = "TongTien", precision = 12, scale = 2, nullable = false)
    private BigDecimal tongTien = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaKH", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "MaNV", nullable = true)
    private NhanVien nhanVien;

    @OneToMany(mappedBy = "datPhong", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("maCTDP ASC")
    private List<ChiTietDatPhong> dsChiTietDatPhong = new ArrayList<>();

    @OneToMany(mappedBy = "datPhong", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("maCTDV ASC")
    private List<ChiTietDichVu> dsChiTietDichVu = new ArrayList<>();

    public DatPhong() {
    }

    public String getMaDatPhong() {
        return maDatPhong;
    }

    public void setMaDatPhong(String maDatPhong) {
        this.maDatPhong = maDatPhong;
    }

    public LocalDateTime getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(LocalDateTime ngayDat) {
        this.ngayDat = ngayDat;
    }

    public LocalDate getNgayNhanPhong() {
        return ngayNhanPhong;
    }

    public void setNgayNhanPhong(LocalDate ngayNhanPhong) {
        this.ngayNhanPhong = ngayNhanPhong;
    }

    public LocalDate getNgayTraPhong() {
        return ngayTraPhong;
    }

    public void setNgayTraPhong(LocalDate ngayTraPhong) {
        this.ngayTraPhong = ngayTraPhong;
    }

    public TrangThaiDatPhong getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiDatPhong trangThai) {
        this.trangThai = trangThai;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public List<ChiTietDatPhong> getDsChiTietDatPhong() {
        return dsChiTietDatPhong;
    }

    public List<ChiTietDichVu> getDsChiTietDichVu() {
        return dsChiTietDichVu;
    }

    @PrePersist
    private void ganNgayNeuChuaCo() {
        if (ngayDat == null) {
            ngayDat = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        }
    }
}

