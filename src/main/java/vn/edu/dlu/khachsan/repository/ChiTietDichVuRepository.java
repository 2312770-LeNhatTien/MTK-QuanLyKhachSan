package vn.edu.dlu.khachsan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.edu.dlu.khachsan.entity.ChiTietDichVu;

public interface ChiTietDichVuRepository extends JpaRepository<ChiTietDichVu, String> {
    @Query(value = "select MaCTDV from CHI_TIET_DICH_VU order by MaCTDV desc limit 1 for update", nativeQuery = true)
    String timMaLonNhat();
}

