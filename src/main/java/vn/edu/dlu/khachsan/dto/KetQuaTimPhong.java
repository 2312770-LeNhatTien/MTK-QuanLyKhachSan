package vn.edu.dlu.khachsan.dto;
import java.math.BigDecimal;
import java.util.List;
import vn.edu.dlu.khachsan.entity.LoaiPhong;
import vn.edu.dlu.khachsan.entity.Phong;
public record KetQuaTimPhong(LoaiPhong loaiPhong,List<Phong> dsPhong,BigDecimal giaDuKien) {}
