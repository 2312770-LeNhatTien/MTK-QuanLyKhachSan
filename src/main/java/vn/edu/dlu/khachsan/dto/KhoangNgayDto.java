package vn.edu.dlu.khachsan.dto;
import java.time.*;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.*;
public class KhoangNgayDto {
 @NotNull(message="Vui lòng chọn từ ngày.") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate tuNgay=LocalDate.now().withDayOfMonth(1);
 @NotNull(message="Vui lòng chọn đến ngày.") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate denNgay=LocalDate.now();
 public LocalDate getTuNgay(){return tuNgay;} public void setTuNgay(LocalDate v){tuNgay=v;}
 public LocalDate getDenNgay(){return denNgay;} public void setDenNgay(LocalDate v){denNgay=v;}
 @AssertTrue(message="Đến ngày phải từ ngày trở đi.") public boolean isKhoangNgayHopLe(){return tuNgay==null||denNgay==null||!denNgay.isBefore(tuNgay);}
}
