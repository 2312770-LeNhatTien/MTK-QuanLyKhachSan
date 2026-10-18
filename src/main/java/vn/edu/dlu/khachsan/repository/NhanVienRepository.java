package vn.edu.dlu.khachsan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

import vn.edu.dlu.khachsan.entity.NhanVien;

public interface NhanVienRepository extends JpaRepository<NhanVien, String> {
    boolean existsByEmailAndMaNVNot(String email,String ma);
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths="vaiTro")
    java.util.List<NhanVien> findAllByOrderByMaNVAsc();
    Optional<NhanVien> findByEmail(String email);

    @Query(value = "select MaNV from NHAN_VIEN order by MaNV desc limit 1 for update", nativeQuery = true)
    String timMaLonNhat();
}

