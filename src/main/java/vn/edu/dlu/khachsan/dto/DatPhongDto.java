package vn.edu.dlu.khachsan.dto;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
public class DatPhongDto {
    @NotNull(message="Vui lòng chọn ngày nhận phòng.")
    @FutureOrPresent(message="Ngày nhận phòng không được trước hôm nay.")
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private LocalDate ngayNhanPhong;
    @NotNull(message="Vui lòng chọn ngày trả phòng.")
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private LocalDate ngayTraPhong;
    @NotEmpty(message="Vui lòng chọn ít nhất một phòng.")
    private Map<@NotBlank(message="Mã phòng không hợp lệ.") @Size(max=10,message="Mã phòng tối đa 10 ký tự.") String,@NotNull(message="Vui lòng nhập số khách.") @Min(value=1,message="Số khách phải lớn hơn 0.") Integer> phong=new LinkedHashMap<>();
    private Map<@NotBlank(message="Mã dịch vụ không hợp lệ.") @Size(max=10,message="Mã dịch vụ tối đa 10 ký tự.") String,@NotNull(message="Vui lòng nhập số lượng dịch vụ.") @Min(value=1,message="Số lượng dịch vụ phải từ 1 đến 10.") @Max(value=10,message="Số lượng dịch vụ phải từ 1 đến 10.") Integer> dichVu=new LinkedHashMap<>();
    private boolean dongYQuyDinh;
    public LocalDate getNgayNhanPhong(){return ngayNhanPhong;}
    public void setNgayNhanPhong(LocalDate v){ngayNhanPhong=v;}
    public LocalDate getNgayTraPhong(){return ngayTraPhong;}
    public void setNgayTraPhong(LocalDate v){ngayTraPhong=v;}
    public Map<String,Integer> getPhong(){return phong;}
    public void setPhong(Map<String,Integer> v){phong=v;}
    public Map<String,Integer> getDichVu(){return dichVu;}
    public void setDichVu(Map<String,Integer> v){dichVu=v;}
    public boolean isDongYQuyDinh(){return dongYQuyDinh;}
    public void setDongYQuyDinh(boolean v){dongYQuyDinh=v;}
    @AssertTrue(message="Ngày trả phòng phải sau ngày nhận phòng.")
    public boolean isNgayHopLe(){return ngayNhanPhong==null||ngayTraPhong==null||ngayTraPhong.isAfter(ngayNhanPhong);}
}
