package vn.edu.dlu.khachsan.dto;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;
public record LoaiPhongDto(
 @NotBlank(message="Vui lòng nhập tên loại phòng.") @Size(max=50,message="Tên tối đa 50 ký tự.") String tenLoaiPhong,
 @NotNull(message="Vui lòng nhập giá.") @DecimalMin(value="0.01",message="Giá phải lớn hơn 0.") @Digits(integer=10,fraction=2,message="Giá không hợp lệ.") BigDecimal giaMoiDem,
 @NotNull(message="Vui lòng nhập sức chứa.") @Min(value=1,message="Sức chứa phải lớn hơn 0.") Integer sucChua,
 @Size(max=255,message="Mô tả tối đa 255 ký tự.") String moTa){public LoaiPhongDto(){this(null,null,null,null);}}
