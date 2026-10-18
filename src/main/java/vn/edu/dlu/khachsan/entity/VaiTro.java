package vn.edu.dlu.khachsan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "VAI_TRO")
public class VaiTro {

    @Id
    @Column(name = "MaVaiTro", length = 10, nullable = false)
    private String maVaiTro;

    @Column(name = "TenVaiTro", length = 50, nullable = false, unique = true)
    private String tenVaiTro;

    public VaiTro() {
    }

    public String getMaVaiTro() {
        return maVaiTro;
    }

    public void setMaVaiTro(String maVaiTro) {
        this.maVaiTro = maVaiTro;
    }

    public String getTenVaiTro() {
        return tenVaiTro;
    }

    public void setTenVaiTro(String tenVaiTro) {
        this.tenVaiTro = tenVaiTro;
    }
}

