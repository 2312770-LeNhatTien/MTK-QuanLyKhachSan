package vn.edu.dlu.khachsan.dto;
import java.time.LocalDate;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import vn.edu.dlu.khachsan.entity.TrangThaiDatPhong;
public record LocDonDto(TrangThaiDatPhong trangThai,
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate tuNgay,
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate denNgay,
 @Size(max=10,message="Mã đơn tối đa 10 ký tự.") String ma,
 @Size(max=100,message="Email tối đa 100 ký tự.") String email){
 @AssertTrue(message="Đến ngày phải từ ngày trở đi.") public boolean isNgayHopLe(){return tuNgay==null||denNgay==null||!denNgay.isBefore(tuNgay);}
}
