package vn.edu.dlu.khachsan.dto;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;
public record ThamSoDto(@NotNull(message="Vui lòng nhập giá trị.") @DecimalMin(value="1",message="Giá trị phải là số nguyên dương.") @Digits(integer=10,fraction=0,message="Giá trị phải là số nguyên, tối đa 10 chữ số.") BigDecimal giaTri){}
