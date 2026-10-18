package vn.edu.dlu.khachsan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.dlu.khachsan.entity.Phong;

public interface PhongRepository extends JpaRepository<Phong, String> {
    @Query("select p from Phong p join fetch p.loaiPhong order by p.maPhong")
    List<Phong> layTatCaKemLoai();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Phong p where p.maPhong = :ma")
    Optional<Phong> khoaTheoMa(@Param("ma") String ma);

    boolean existsByLoaiPhongMaLoaiPhong(String maLoaiPhong);
    boolean existsBySoPhong(String soPhong);
}

