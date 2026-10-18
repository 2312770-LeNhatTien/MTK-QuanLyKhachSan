package vn.edu.dlu.khachsan.security;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class TaiKhoanDangNhap extends User {

    private final String maTaiKhoan;
    private final String hoTen;

    public TaiKhoanDangNhap(String maTaiKhoan, String hoTen, String email, String matKhau, String vaiTro) {
        super(email, matKhau, List.of(new SimpleGrantedAuthority("ROLE_" + vaiTro)));
        this.maTaiKhoan = maTaiKhoan;
        this.hoTen = hoTen;
    }

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public String getHoTen() {
        return hoTen;
    }
}
