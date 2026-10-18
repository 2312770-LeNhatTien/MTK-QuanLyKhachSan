package vn.edu.dlu.khachsan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import vn.edu.dlu.khachsan.pattern.state.PhongTrong;
import vn.edu.dlu.khachsan.pattern.state.TrangThaiPhong;

@Entity
@Table(name = "PHONG")
public class Phong {

    @Id
    @Column(name = "MaPhong", length = 10, nullable = false)
    private String maPhong;

    @Column(name = "SoPhong", length = 10, nullable = false, unique = true)
    private String soPhong;

    @Column(name = "Tang", nullable = true)
    private Integer tang;

    @Column(name = "TrangThai", length = 20, nullable = false)
    private String trangThai = new PhongTrong().getMa();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaLoaiPhong", nullable = false)
    private LoaiPhong loaiPhong;

    public Phong() {
    }

    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public String getSoPhong() {
        return soPhong;
    }

    public void setSoPhong(String soPhong) {
        this.soPhong = soPhong;
    }

    public Integer getTang() {
        return tang;
    }

    public void setTang(Integer tang) {
        this.tang = tang;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public TrangThaiPhong layTrangThai() {
        return TrangThaiPhong.tuMa(trangThai);
    }

    // Chỉ các lớp State gọi phương thức này khi chuyển trạng thái hợp lệ.
    public void chuyenSang(TrangThaiPhong moi) {
        this.trangThai = moi.getMa();
    }

    public void checkIn() {
        layTrangThai().checkIn(this);
    }

    public void checkOut() {
        layTrangThai().checkOut(this);
    }

    public void donXong() {
        layTrangThai().donXong(this);
    }

    public void baoTri() {
        layTrangThai().baoTri(this);
    }

    public void ketThucBaoTri() {
        layTrangThai().ketThucBaoTri(this);
    }

    public LoaiPhong getLoaiPhong() {
        return loaiPhong;
    }

    public void setLoaiPhong(LoaiPhong loaiPhong) {
        this.loaiPhong = loaiPhong;
    }
}

