package vn.edu.dlu.khachsan.dto;
import jakarta.validation.constraints.*;
public record NhanVienDto(
 @NotBlank(message="Vui lòng nhập họ tên.") @Size(max=100,message="Họ tên tối đa 100 ký tự.") String hoTen,
 @NotBlank(message="Vui lòng nhập email.") @Email(message="Email không đúng định dạng.") @Size(max=100,message="Email tối đa 100 ký tự.") String email,
 @Size(max=15,message="Số điện thoại tối đa 15 ký tự.") String sdt,
 @NotBlank(message="Vui lòng chọn vai trò.") String maVaiTro){public NhanVienDto(){this(null,null,null,null);}}
