package vn.edu.dlu.khachsan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "LOAI_PHONG")
public class LoaiPhong {

    @Id
    @Column(name = "MaLoaiPhong", length = 10, nullable = false)
    private String maLoaiPhong;

    @Column(name = "TenLoaiPhong", length = 50, nullable = false, unique = true)
    private String tenLoaiPhong;

    @Column(name = "GiaMoiDem", precision = 12, scale = 2, nullable = false)
    private BigDecimal giaMoiDem;

    @Column(name = "SucChua", nullable = false)
    private int sucChua;

    @Column(name = "MoTa", length = 255, nullable = true)
    private String moTa;

    public LoaiPhong() {
    }

    public String getMaLoaiPhong() {
        return maLoaiPhong;
    }

    public void setMaLoaiPhong(String maLoaiPhong) {
        this.maLoaiPhong = maLoaiPhong;
    }

    public String getTenLoaiPhong() {
        return tenLoaiPhong;
    }

    public void setTenLoaiPhong(String tenLoaiPhong) {
        this.tenLoaiPhong = tenLoaiPhong;
    }

    public BigDecimal getGiaMoiDem() {
        return giaMoiDem;
    }

    public void setGiaMoiDem(BigDecimal giaMoiDem) {
        this.giaMoiDem = giaMoiDem;
    }

    public int getSucChua() {
        return sucChua;
    }

    public void setSucChua(int sucChua) {
        this.sucChua = sucChua;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}

