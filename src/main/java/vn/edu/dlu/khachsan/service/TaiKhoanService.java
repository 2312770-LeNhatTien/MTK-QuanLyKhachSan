package vn.edu.dlu.khachsan.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.dlu.khachsan.dto.DangKyDto;
import vn.edu.dlu.khachsan.dto.DoiMatKhauDto;
import vn.edu.dlu.khachsan.entity.KhachHang;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.repository.KhachHangRepository;

@Service
public class TaiKhoanService {

    private final KhachHangRepository khachHangRepository;
    private final MaSinhService maSinhService;
    private final PasswordEncoder passwordEncoder;

    public TaiKhoanService(KhachHangRepository khachHangRepository, MaSinhService maSinhService,
            PasswordEncoder passwordEncoder) {
        this.khachHangRepository = khachHangRepository;
        this.maSinhService = maSinhService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void dangKy(DangKyDto dto) {
        kiemTraMatKhau(dto.getMatKhau(), dto.getXacNhanMatKhau());
        String email = dto.getEmail().trim().toLowerCase(Locale.ROOT);
        if (khachHangRepository.existsByEmail(email)) {
            throw new NghiepVuException("Email này đã được sử dụng.");
        }
        KhachHang khach = new KhachHang();
        khach.setHoTen(dto.getHoTen().trim());
        khach.setEmail(email);
        khach.setSdt(rongThanhNull(dto.getSdt()));
        khach.setDiaChi(rongThanhNull(dto.getDiaChi()));
        khach.setMatKhau(passwordEncoder.encode(dto.getMatKhau()));
        khach.setMaKH(maSinhService.sinhMaKhachHang());
        try {
            // UNIQUE trong CSDL vẫn chặn hai yêu cầu đăng ký cùng email đồng thời.
            khachHangRepository.saveAndFlush(khach);
        } catch (DataIntegrityViolationException ex) {
            throw new NghiepVuException("Email này đã được sử dụng.");
        }
    }

    @Transactional
    public void doiMatKhau(String emailDangNhap, DoiMatKhauDto dto) {
        KhachHang khach = khachHangRepository.findByEmail(emailDangNhap)
                .orElseThrow(() -> new NghiepVuException("Không tìm thấy tài khoản khách hàng."));
        if (dto.getMatKhauHienTai() == null || !passwordEncoder.matches(dto.getMatKhauHienTai(), khach.getMatKhau())) {
            throw new NghiepVuException("Mật khẩu hiện tại không đúng.");
        }
        kiemTraMatKhau(dto.getMatKhauMoi(), dto.getXacNhanMatKhau());
        khach.setMatKhau(passwordEncoder.encode(dto.getMatKhauMoi()));
        khachHangRepository.save(khach);
    }

    private void kiemTraMatKhau(String matKhau, String xacNhan) {
        if (matKhau == null || matKhau.isBlank() || matKhau.length() < 6
                || matKhau.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new NghiepVuException("Mật khẩu phải có ít nhất 6 ký tự và không quá dài.");
        }
        if (!matKhau.equals(xacNhan)) {
            throw new NghiepVuException("Mật khẩu xác nhận không khớp.");
        }
    }

    private String rongThanhNull(String giaTri) {
        return giaTri == null || giaTri.isBlank() ? null : giaTri.trim();
    }
}
