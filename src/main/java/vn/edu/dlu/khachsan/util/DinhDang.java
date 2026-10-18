package vn.edu.dlu.khachsan.util;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
@Component("dinhDang")
public class DinhDang {
    public String tien(BigDecimal v){return DinhDangTien.hienThi(v==null?BigDecimal.ZERO:v);}
    public String ngay(LocalDate v){return v==null?"":v.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));}
    public String ngayGio(LocalDateTime v){return v==null?"":v.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));}
}
