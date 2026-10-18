package vn.edu.dlu.khachsan.dto;
import jakarta.validation.constraints.*;
public record PhongDto(
 @NotBlank(message="Vui lòng nhập số phòng.") @Pattern(regexp="[A-Za-z0-9]{1,9}",message="Số phòng gồm 1–9 chữ cái hoặc chữ số.") String soPhong,
 Integer tang,
 @NotBlank(message="Vui lòng chọn loại phòng.") String maLoaiPhong){public PhongDto(){this(null,null,null);}}
