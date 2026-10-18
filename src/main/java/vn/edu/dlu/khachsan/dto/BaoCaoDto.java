package vn.edu.dlu.khachsan.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import vn.edu.dlu.khachsan.entity.TrangThaiDatPhong;
public record BaoCaoDto(Map<LocalDate,BigDecimal> theoNgay,Map<String,BigDecimal> theoPhuongThuc,Map<TrangThaiDatPhong,Long> soDon,BigDecimal doanhThu,long demPhong,long sucChuaDemPhong,BigDecimal congSuat){}
