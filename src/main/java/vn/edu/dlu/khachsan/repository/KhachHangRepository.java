package vn.edu.dlu.khachsan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

import vn.edu.dlu.khachsan.entity.KhachHang;

public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    Optional<KhachHang> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "select MaKH from KHACH_HANG order by MaKH desc limit 1 for update", nativeQuery = true)
    String timMaLonNhat();
}

