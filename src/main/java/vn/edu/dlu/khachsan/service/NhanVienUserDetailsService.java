package vn.edu.dlu.khachsan.service;

import java.util.Locale;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.dlu.khachsan.repository.NhanVienRepository;
import vn.edu.dlu.khachsan.security.TaiKhoanDangNhap;

@Service
public class NhanVienUserDetailsService implements UserDetailsService {

    private final NhanVienRepository nhanVienRepository;

    public NhanVienUserDetailsService(NhanVienRepository nhanVienRepository) {
        this.nhanVienRepository = nhanVienRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        var nv = nhanVienRepository.findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new UsernameNotFoundException("Email hoặc mật khẩu không đúng."));
        return new TaiKhoanDangNhap(nv.getMaNV(), nv.getHoTen(), nv.getEmail(), nv.getMatKhau(), nv.getVaiTro().getMaVaiTro());
    }
}
