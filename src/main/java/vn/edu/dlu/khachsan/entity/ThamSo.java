package vn.edu.dlu.khachsan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "THAM_SO")
public class ThamSo {

    @Id
    @Column(name = "MaThamSo", length = 30, nullable = false)
    private String maThamSo;

    @Column(name = "TenThamSo", length = 100, nullable = false)
    private String tenThamSo;

    @Column(name = "GiaTri", precision = 12, scale = 2, nullable = false)
    private BigDecimal giaTri;

    @Column(name = "DonVi", length = 20, nullable = true)
    private String donVi;

    public ThamSo() {
    }

    public String getMaThamSo() {
        return maThamSo;
    }

    public void setMaThamSo(String maThamSo) {
        this.maThamSo = maThamSo;
    }

    public String getTenThamSo() {
        return tenThamSo;
    }

    public void setTenThamSo(String tenThamSo) {
        this.tenThamSo = tenThamSo;
    }

    public BigDecimal getGiaTri() {
        return giaTri;
    }

    public void setGiaTri(BigDecimal giaTri) {
        this.giaTri = giaTri;
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }
}

