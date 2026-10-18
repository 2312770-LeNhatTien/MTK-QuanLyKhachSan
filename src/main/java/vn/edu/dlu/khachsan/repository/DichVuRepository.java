package vn.edu.dlu.khachsan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.edu.dlu.khachsan.entity.DichVu;

public interface DichVuRepository extends JpaRepository<DichVu, String> {
    @Query(value = "select MaDichVu from DICH_VU order by MaDichVu desc limit 1 for update", nativeQuery = true)
    String timMaLonNhat();
}

