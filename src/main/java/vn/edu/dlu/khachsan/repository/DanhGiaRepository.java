package vn.edu.dlu.khachsan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;

import vn.edu.dlu.khachsan.entity.DanhGia;

public interface DanhGiaRepository extends JpaRepository<DanhGia, String> {
    @Query(value = "select MaDanhGia from DANH_GIA order by MaDanhGia desc limit 1 for update", nativeQuery = true)
    String timMaLonNhat();
    boolean existsByDatPhongMaDatPhong(String ma);
    @EntityGraph(attributePaths={"datPhong","datPhong.khachHang"})
    List<DanhGia> findTop6ByOrderByNgayDanhGiaDesc();
}

