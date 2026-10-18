package vn.edu.dlu.khachsan.dto;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;
public class ThuTienDto {
 @NotBlank(message="Vui lòng chọn phương thức.") private String maPhuongThuc;
 @DecimalMin(value="0",message="Tiền khách đưa không được âm.") @Digits(integer=10,fraction=2,message="Số tiền không hợp lệ.") private BigDecimal tienKhachDua;
 @Size(max=6,message="Mã chuẩn chi gồm 6 chữ số.") private String maChuanChi;
 public String getMaPhuongThuc(){return maPhuongThuc;} public void setMaPhuongThuc(String v){maPhuongThuc=v;}
 public BigDecimal getTienKhachDua(){return tienKhachDua;} public void setTienKhachDua(BigDecimal v){tienKhachDua=v;}
 public String getMaChuanChi(){return maChuanChi;} public void setMaChuanChi(String v){maChuanChi=v;}
}
