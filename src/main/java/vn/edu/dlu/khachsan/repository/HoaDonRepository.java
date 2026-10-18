package vn.edu.dlu.khachsan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

import vn.edu.dlu.khachsan.entity.HoaDon;

public interface HoaDonRepository extends JpaRepository<HoaDon, String> {
    @Query(value = "select MaHoaDon from HOA_DON order by MaHoaDon desc limit 1 for update", nativeQuery = true)
    String timMaLonNhat();
    Optional<HoaDon> findByDatPhongMaDatPhong(String maDatPhong);
}

