package vn.edu.dlu.khachsan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.edu.dlu.khachsan.entity.ChiTietDatPhong;

public interface ChiTietDatPhongRepository extends JpaRepository<ChiTietDatPhong, String> {
    boolean existsByPhongMaPhong(String maPhong);
    @Query(value = "select MaCTDP from CHI_TIET_DAT_PHONG order by MaCTDP desc limit 1 for update", nativeQuery = true)
    String timMaLonNhat();
}

