package vn.edu.dlu.khachsan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import vn.edu.dlu.khachsan.entity.TrangThaiDatPhong;

import vn.edu.dlu.khachsan.entity.DatPhong;

public interface DatPhongRepository extends JpaRepository<DatPhong, String> {
    boolean existsByNhanVienMaNV(String maNV);
    List<DatPhong> findByTrangThaiAndNhanVienIsNullAndNgayNhanPhongBetweenOrderByNgayNhanPhongAsc(TrangThaiDatPhong tt, LocalDate tu, LocalDate den);
    @Query("select distinct d from DatPhong d join d.dsChiTietDatPhong c where c.phong.soPhong = :so and d.trangThai = :tt and c.phong.trangThai = 'CO_KHACH'")
    List<DatPhong> timDonDangO(@Param("so") String so,@Param("tt") TrangThaiDatPhong tt);
    @Query(value = "select MaDatPhong from DAT_PHONG order by MaDatPhong desc limit 1 for update", nativeQuery = true)
    String timMaLonNhat();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from DatPhong d where d.maDatPhong = :ma")
    Optional<DatPhong> khoaTheoMa(@Param("ma") String ma);

    @Query("select count(c) from ChiTietDatPhong c where c.phong.maPhong = :ma "
            + "and c.datPhong.trangThai in :trangThai and c.datPhong.ngayNhanPhong < :tra "
            + "and c.datPhong.ngayTraPhong > :nhan")
    long demTrungLich(@Param("ma") String ma, @Param("nhan") LocalDate nhan,
            @Param("tra") LocalDate tra, @Param("trangThai") List<TrangThaiDatPhong> trangThai);

    List<DatPhong> findByKhachHangEmailOrderByNgayDatDesc(String email);

    @Query("select d.maDatPhong from DatPhong d where d.trangThai = :tt and d.ngayDat < :han order by d.maDatPhong")
    List<String> timDonQuaHan(@Param("tt") TrangThaiDatPhong tt, @Param("han") LocalDateTime han);
}

