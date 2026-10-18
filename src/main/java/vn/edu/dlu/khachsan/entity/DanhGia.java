package vn.edu.dlu.khachsan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "DANH_GIA")
public class DanhGia {

    @Id
    @Column(name = "MaDanhGia", length = 10, nullable = false)
    private String maDanhGia;

    @Column(name = "SoSao", nullable = false)
    private int soSao;

    @Column(name = "NoiDung", length = 500, nullable = true)
    private String noiDung;

    @Column(name = "NgayDanhGia", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime ngayDanhGia;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaDatPhong", nullable = false, unique = true)
    private DatPhong datPhong;

    public DanhGia() {
    }

    public String getMaDanhGia() {
        return maDanhGia;
    }

    public void setMaDanhGia(String maDanhGia) {
        this.maDanhGia = maDanhGia;
    }

    public int getSoSao() {
        return soSao;
    }

    public void setSoSao(int soSao) {
        this.soSao = soSao;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public LocalDateTime getNgayDanhGia() {
        return ngayDanhGia;
    }

    public void setNgayDanhGia(LocalDateTime ngayDanhGia) {
        this.ngayDanhGia = ngayDanhGia;
    }

    public DatPhong getDatPhong() {
        return datPhong;
    }

    public void setDatPhong(DatPhong datPhong) {
        this.datPhong = datPhong;
    }

    @PrePersist
    private void ganNgayNeuChuaCo() {
        if (ngayDanhGia == null) {
            ngayDanhGia = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        }
    }
}

