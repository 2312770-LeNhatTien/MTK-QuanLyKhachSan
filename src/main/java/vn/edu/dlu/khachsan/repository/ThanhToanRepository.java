package vn.edu.dlu.khachsan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import vn.edu.dlu.khachsan.entity.TrangThaiGiaoDich;

import vn.edu.dlu.khachsan.entity.ThanhToan;

public interface ThanhToanRepository extends JpaRepository<ThanhToan, String> {
    @Query(value = "select MaThanhToan from THANH_TOAN order by MaThanhToan desc limit 1 for update", nativeQuery = true)
    String timMaLonNhat();
    List<ThanhToan> findByHoaDonMaHoaDonOrderByNgayThanhToanAsc(String maHoaDon);

    @Query("select coalesce(sum(t.soTien),0) from ThanhToan t where t.hoaDon.maHoaDon=:ma and t.trangThai=:tt")
    BigDecimal tongDaTra(@Param("ma") String ma, @Param("tt") TrangThaiGiaoDich tt);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from ThanhToan t where t.maThanhToan=:ma")
    Optional<ThanhToan> khoaTheoMa(@Param("ma") String ma);
}

