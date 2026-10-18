package vn.edu.dlu.khachsan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(name = "CHI_TIET_DAT_PHONG", uniqueConstraints = @UniqueConstraint(name = "UK_CHI_TIET_DAT_PHONG_DonPhong", columnNames = {"MaDatPhong", "MaPhong"}))
public class ChiTietDatPhong {

    @Id
    @Column(name = "MaCTDP", length = 10, nullable = false)
    private String maCTDP;

    @Column(name = "SoLuongKhach", nullable = false)
    private int soLuongKhach;

    @Column(name = "GiaThoiDiemDat", precision = 12, scale = 2, nullable = false)
    private BigDecimal giaThoiDiemDat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaDatPhong", nullable = false)
    private DatPhong datPhong;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaPhong", nullable = false)
    private Phong phong;

    public ChiTietDatPhong() {
    }

    public String getMaCTDP() {
        return maCTDP;
    }

    public void setMaCTDP(String maCTDP) {
        this.maCTDP = maCTDP;
    }

    public int getSoLuongKhach() {
        return soLuongKhach;
    }

    public void setSoLuongKhach(int soLuongKhach) {
        this.soLuongKhach = soLuongKhach;
    }

    public BigDecimal getGiaThoiDiemDat() {
        return giaThoiDiemDat;
    }

    public void setGiaThoiDiemDat(BigDecimal giaThoiDiemDat) {
        this.giaThoiDiemDat = giaThoiDiemDat;
    }

    public DatPhong getDatPhong() {
        return datPhong;
    }

    public void setDatPhong(DatPhong datPhong) {
        this.datPhong = datPhong;
    }

    public Phong getPhong() {
        return phong;
    }

    public void setPhong(Phong phong) {
        this.phong = phong;
    }
}

