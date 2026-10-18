package vn.edu.dlu.khachsan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "DICH_VU")
public class DichVu {

    @Id
    @Column(name = "MaDichVu", length = 10, nullable = false)
    private String maDichVu;

    @Column(name = "TenDichVu", length = 100, nullable = false, unique = true)
    private String tenDichVu;

    @Column(name = "DonGia", precision = 12, scale = 2, nullable = false)
    private BigDecimal donGia;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "CachTinh", length = 20, nullable = false)
    private CachTinhDichVu cachTinh;

    @Column(name = "DonViTinh", length = 30, nullable = false)
    private String donViTinh;

    @Column(name = "MoTa", length = 255, nullable = true)
    private String moTa;

    public DichVu() {
    }

    public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public CachTinhDichVu getCachTinh() {
        return cachTinh;
    }

    public void setCachTinh(CachTinhDichVu cachTinh) {
        this.cachTinh = cachTinh;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}

