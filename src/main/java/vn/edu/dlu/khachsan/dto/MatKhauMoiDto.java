package vn.edu.dlu.khachsan.dto;
import jakarta.validation.constraints.*;
public record MatKhauMoiDto(@NotBlank(message="Vui lòng nhập mật khẩu.") @Size(min=6,max=72,message="Mật khẩu phải có từ 6 đến 72 ký tự.") String matKhau){
 public MatKhauMoiDto(){this(null);}
 @AssertTrue(message="Mật khẩu tối đa 72 byte UTF-8.") public boolean isDoDaiHopLe(){return matKhau==null||matKhau.getBytes(java.nio.charset.StandardCharsets.UTF_8).length<=72;}
}
