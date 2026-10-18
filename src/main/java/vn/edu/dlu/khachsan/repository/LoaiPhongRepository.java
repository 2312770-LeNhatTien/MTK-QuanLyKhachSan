package vn.edu.dlu.khachsan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.edu.dlu.khachsan.entity.LoaiPhong;

public interface LoaiPhongRepository extends JpaRepository<LoaiPhong, String> {
    boolean existsByTenLoaiPhongAndMaLoaiPhongNot(String ten,String ma);
    @Query(value = "select MaLoaiPhong from LOAI_PHONG order by MaLoaiPhong desc limit 1 for update", nativeQuery = true)
    String timMaLonNhat();
}

