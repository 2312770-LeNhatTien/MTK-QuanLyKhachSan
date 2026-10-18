package vn.edu.dlu.khachsan.dto;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;
public class ThanhToanDto {
    private boolean toanBo=true;
    @Digits(integer=10,fraction=2,message="Số tiền tối đa 10 chữ số và 2 số thập phân.")
    private BigDecimal soTien;
    @NotBlank(message="Vui lòng chọn phương thức thanh toán.")
    private String maPhuongThuc;
    public boolean isToanBo(){return toanBo;}
    public void setToanBo(boolean v){toanBo=v;}
    public BigDecimal getSoTien(){return soTien;}
    public void setSoTien(BigDecimal v){soTien=v;}
    public String getMaPhuongThuc(){return maPhuongThuc;}
    public void setMaPhuongThuc(String v){maPhuongThuc=v;}
}
