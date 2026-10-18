package vn.edu.dlu.khachsan.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import vn.edu.dlu.khachsan.entity.*;
import vn.edu.dlu.khachsan.pattern.decorator.ChiPhiDatPhong;
public record ChiTietDonDto(DatPhong don,ChiPhiDatPhong chiPhi,HoaDon hoaDon,List<ThanhToan> giaoDich,
        BigDecimal daTra,BigDecimal conLai,LocalDateTime hetHan,boolean coTheThanhToan,boolean coTheHuy,boolean coTheDanhGia) {}
