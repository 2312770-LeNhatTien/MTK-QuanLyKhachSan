package vn.edu.dlu.khachsan.dto;
import java.time.LocalDate;
import java.time.ZoneId;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
public class TimPhongDto {
    @NotNull(message="Vui lòng chọn ngày nhận phòng.")
    @FutureOrPresent(message="Ngày nhận phòng không được trước hôm nay.")
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private LocalDate ngayNhanPhong=LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    @NotNull(message="Vui lòng chọn ngày trả phòng.")
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private LocalDate ngayTraPhong=ngayNhanPhong.plusDays(1);
    @Min(value=1,message="Số khách phải lớn hơn 0.")
    private int soKhach=1;
    private String maLoaiPhong;
    public LocalDate getNgayNhanPhong(){return ngayNhanPhong;}
    public void setNgayNhanPhong(LocalDate v){ngayNhanPhong=v;}
    public LocalDate getNgayTraPhong(){return ngayTraPhong;}
    public void setNgayTraPhong(LocalDate v){ngayTraPhong=v;}
    public int getSoKhach(){return soKhach;}
    public void setSoKhach(int v){soKhach=v;}
    public String getMaLoaiPhong(){return maLoaiPhong;}
    public void setMaLoaiPhong(String v){maLoaiPhong=v;}
    @AssertTrue(message="Ngày trả phòng phải sau ngày nhận phòng.")
    public boolean isNgayHopLe(){return ngayNhanPhong==null||ngayTraPhong==null||ngayTraPhong.isAfter(ngayNhanPhong);}
}
