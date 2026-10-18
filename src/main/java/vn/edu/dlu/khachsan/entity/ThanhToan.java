package vn.edu.dlu.khachsan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "THANH_TOAN")
public class ThanhToan {

    @Id
    @Column(name = "MaThanhToan", length = 10, nullable = false)
    private String maThanhToan;

    @Column(name = "PhuongThuc", length = 20, nullable = false)
    private String phuongThuc;

    @Column(name = "SoTien", precision = 12, scale = 2, nullable = false)
    private BigDecimal soTien;

    @Column(name = "NgayThanhToan", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime ngayThanhToan;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiGiaoDich trangThai = TrangThaiGiaoDich.DANG_XU_LY;

    @Column(name = "GhiChu", length = 200, nullable = true)
    private String ghiChu;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaHoaDon", nullable = false)
    private HoaDon hoaDon;

    public ThanhToan() {
    }

    public String getMaThanhToan() {
        return maThanhToan;
    }

    public void setMaThanhToan(String maThanhToan) {
        this.maThanhToan = maThanhToan;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public LocalDateTime getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(LocalDateTime ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public TrangThaiGiaoDich getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiGiaoDich trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    @PrePersist
    private void ganNgayNeuChuaCo() {
        if (ngayThanhToan == null) {
            ngayThanhToan = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        }
    }
}

