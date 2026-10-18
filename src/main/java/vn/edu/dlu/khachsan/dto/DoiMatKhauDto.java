package vn.edu.dlu.khachsan.dto;

import java.nio.charset.StandardCharsets;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DoiMatKhauDto {

    @NotBlank(message = "Vui lòng nhập mật khẩu hiện tại.")
    private String matKhauHienTai;

    @NotBlank(message = "Vui lòng nhập mật khẩu mới.")
    @Size(min = 6, max = 72, message = "Mật khẩu mới phải có từ 6 đến 72 ký tự.")
    private String matKhauMoi;

    @NotBlank(message = "Vui lòng xác nhận mật khẩu mới.")
    private String xacNhanMatKhau;

    public String getMatKhauHienTai() {
        return matKhauHienTai;
    }

    public void setMatKhauHienTai(String matKhauHienTai) {
        this.matKhauHienTai = matKhauHienTai;
    }

    public String getMatKhauMoi() {
        return matKhauMoi;
    }

    public void setMatKhauMoi(String matKhauMoi) {
        this.matKhauMoi = matKhauMoi;
    }

    public String getXacNhanMatKhau() {
        return xacNhanMatKhau;
    }

    public void setXacNhanMatKhau(String xacNhanMatKhau) {
        this.xacNhanMatKhau = xacNhanMatKhau;
    }

    @AssertTrue(message = "Mật khẩu xác nhận không khớp.")
    public boolean isXacNhanHopLe() {
        return matKhauMoi == null || xacNhanMatKhau == null || matKhauMoi.equals(xacNhanMatKhau);
    }

    @AssertTrue(message = "Mật khẩu quá dài. Vui lòng sử dụng ít ký tự hơn.")
    public boolean isDoDaiMatKhauHopLe() {
        return matKhauMoi == null || matKhauMoi.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
}

