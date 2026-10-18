package vn.edu.dlu.khachsan.dto;

import java.nio.charset.StandardCharsets;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public class DangKyDto {

    @NotBlank(message = "Vui lòng nhập họ tên.")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự.")
    private String hoTen;

    @NotBlank(message = "Vui lòng nhập email.")
    @Email(message = "Email không đúng định dạng.")
    @Size(max = 100, message = "Email tối đa 100 ký tự.")
    private String email;

    @Size(max = 15, message = "Số điện thoại tối đa 15 ký tự.")
    private String sdt;

    @Size(max = 200, message = "Địa chỉ tối đa 200 ký tự.")
    private String diaChi;

    @NotBlank(message = "Vui lòng nhập mật khẩu.")
    @Size(min = 6, max = 72, message = "Mật khẩu phải có từ 6 đến 72 ký tự.")
    private String matKhau;

    @NotBlank(message = "Vui lòng xác nhận mật khẩu.")
    private String xacNhanMatKhau;

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getXacNhanMatKhau() {
        return xacNhanMatKhau;
    }

    public void setXacNhanMatKhau(String xacNhanMatKhau) {
        this.xacNhanMatKhau = xacNhanMatKhau;
    }

    @AssertTrue(message = "Mật khẩu xác nhận không khớp.")
    public boolean isXacNhanHopLe() {
        return matKhau == null || xacNhanMatKhau == null || matKhau.equals(xacNhanMatKhau);
    }

    @AssertTrue(message = "Mật khẩu quá dài. Vui lòng sử dụng ít ký tự hơn.")
    public boolean isDoDaiMatKhauHopLe() {
        return matKhau == null || matKhau.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
}

