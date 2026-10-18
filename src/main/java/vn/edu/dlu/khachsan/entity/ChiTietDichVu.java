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
@Table(name = "CHI_TIET_DICH_VU", uniqueConstraints = @UniqueConstraint(name = "UK_CHI_TIET_DICH_VU_DonDichVu", columnNames = {"MaDatPhong", "MaDichVu"}))
public class ChiTietDichVu {

    @Id
    @Column(name = "MaCTDV", length = 10, nullable = false)
    private String maCTDV;

    @Column(name = "SoLuong", nullable = false)
    private int soLuong;

    @Column(name = "DonGiaThoiDiemDat", precision = 12, scale = 2, nullable = false)
    private BigDecimal donGiaThoiDiemDat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaDatPhong", nullable = false)
    private DatPhong datPhong;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaDichVu", nullable = false)
    private DichVu dichVu;

    public ChiTietDichVu() {
    }

    public String getMaCTDV() {
        return maCTDV;
    }

    public void setMaCTDV(String maCTDV) {
        this.maCTDV = maCTDV;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGiaThoiDiemDat() {
        return donGiaThoiDiemDat;
    }

    public void setDonGiaThoiDiemDat(BigDecimal donGiaThoiDiemDat) {
        this.donGiaThoiDiemDat = donGiaThoiDiemDat;
    }

    public DatPhong getDatPhong() {
        return datPhong;
    }

    public void setDatPhong(DatPhong datPhong) {
        this.datPhong = datPhong;
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVu dichVu) {
        this.dichVu = dichVu;
    }
}

