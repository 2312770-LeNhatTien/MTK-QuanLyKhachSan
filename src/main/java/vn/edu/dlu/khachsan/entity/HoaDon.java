package vn.edu.dlu.khachsan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "HOA_DON")
public class HoaDon {

    @Id
    @Column(name = "MaHoaDon", length = 10, nullable = false)
    private String maHoaDon;

    @Column(name = "NgayLap", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime ngayLap;

    @Column(name = "TongTien", precision = 12, scale = 2, nullable = false)
    private BigDecimal tongTien;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "TrangThaiThanhToan", length = 20, nullable = false)
    private TrangThaiThanhToan trangThaiThanhToan = TrangThaiThanhToan.CHUA_THANH_TOAN;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaDatPhong", nullable = false, unique = true)
    private DatPhong datPhong;

    @OneToMany(mappedBy = "hoaDon")
    @OrderBy("maThanhToan ASC")
    private List<ThanhToan> dsThanhToan = new ArrayList<>();

    public HoaDon() {
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public TrangThaiThanhToan getTrangThaiThanhToan() {
        return trangThaiThanhToan;
    }

    public void setTrangThaiThanhToan(TrangThaiThanhToan trangThaiThanhToan) {
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    public DatPhong getDatPhong() {
        return datPhong;
    }

    public void setDatPhong(DatPhong datPhong) {
        this.datPhong = datPhong;
    }

    public List<ThanhToan> getDsThanhToan() {
        return dsThanhToan;
    }

    @PrePersist
    private void ganNgayNeuChuaCo() {
        if (ngayLap == null) {
            ngayLap = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        }
    }
}

