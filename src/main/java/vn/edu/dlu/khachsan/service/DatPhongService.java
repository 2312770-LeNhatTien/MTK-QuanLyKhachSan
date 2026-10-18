package vn.edu.dlu.khachsan.service;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import vn.edu.dlu.khachsan.dto.*;
import vn.edu.dlu.khachsan.entity.*;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.pattern.builder.DatPhongBuilder;
import vn.edu.dlu.khachsan.pattern.decorator.GhepChiPhi;
import vn.edu.dlu.khachsan.repository.*;
@Service
@Transactional(readOnly=true)
public class DatPhongService {
    private final DatPhongRepository datPhongRepository;
    private final PhongRepository phongRepository;
    private final DichVuRepository dichVuRepository;
    private final KhachHangRepository khachHangRepository;
    private final HoaDonRepository hoaDonRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final PhongService phongService;
    private final ThamSoService thamSoService;
    private final MaSinhService maSinhService;
    private final DanhGiaRepository danhGiaRepository;
    public DatPhongService(DatPhongRepository d,PhongRepository p,DichVuRepository dv,KhachHangRepository k,
            HoaDonRepository h,ThanhToanRepository t,PhongService ps,ThamSoService ts,MaSinhService ms,DanhGiaRepository dg){
        datPhongRepository=d;phongRepository=p;dichVuRepository=dv;khachHangRepository=k;
        hoaDonRepository=h;thanhToanRepository=t;phongService=ps;thamSoService=ts;maSinhService=ms;
        danhGiaRepository=dg;
    }
    public List<DichVu> dsDichVu(){return dichVuRepository.findAll(org.springframework.data.domain.Sort.by("maDichVu"));}
    public DatPhong tamTinh(String email,DatPhongDto dto){return builder(email,dto,false).build();}
    public vn.edu.dlu.khachsan.pattern.decorator.ChiPhiDatPhong chiPhi(DatPhong d){return GhepChiPhi.tuDonDatPhong(d);}
    public Map<String,Object> tamTinhHienThi(String email,DatPhongDto dto){
        var chiPhi=chiPhi(tamTinh(email,dto));return Map.of("chiTiet",chiPhi.chiTiet(),"tongTien",chiPhi.tinhTien());
    }
    @Transactional(isolation=Isolation.READ_COMMITTED)
    public DatPhong taoDonTam(String email,DatPhongDto dto){
        maSinhService.khoaGiaoDich();
        if(!dto.isDongYQuyDinh())throw new NghiepVuException("Vui lòng đồng ý quy định đặt phòng.");
        DatPhong don=builder(email,dto,true).build();
        don.setMaDatPhong(maSinhService.sinhMaDatPhong());
        don.getDsChiTietDatPhong().forEach(ct->ct.setMaCTDP(maSinhService.sinhMaChiTietDatPhong()));
        don.getDsChiTietDichVu().forEach(ct->ct.setMaCTDV(maSinhService.sinhMaChiTietDichVu()));
        return datPhongRepository.saveAndFlush(don);
    }
    private DatPhongBuilder builder(String email,DatPhongDto dto,boolean khoa){
        var khach=khachHangRepository.findByEmail(email).orElseThrow(()->new AccessDeniedException("Chỉ khách hàng được đặt phòng."));
        DatPhongBuilder b=new DatPhongBuilder().choKhachHang(khach).ngayNhanPhong(dto.getNgayNhanPhong()).ngayTraPhong(dto.getNgayTraPhong());
        if(dto.getPhong()==null||dto.getDichVu()==null)throw new NghiepVuException("Lựa chọn phòng hoặc dịch vụ không hợp lệ.");
        dto.getPhong().keySet().stream().sorted().forEach(ma->{
            Phong p=(khoa?phongRepository.khoaTheoMa(ma):phongRepository.findById(ma))
                    .orElseThrow(()->new NghiepVuException("Không tìm thấy phòng được chọn."));
            if(dto.getNgayNhanPhong()!=null&&dto.getNgayTraPhong()!=null
                    &&!phongService.conLich(ma,dto.getNgayNhanPhong(),dto.getNgayTraPhong()))
                throw new NghiepVuException("Phòng "+p.getSoPhong()+" không còn trống.");
            Integer soKhach=dto.getPhong().get(ma);
            if(soKhach==null)throw new NghiepVuException("Vui lòng nhập số khách.");
            b.themPhong(p,soKhach);
        });
        dto.getDichVu().forEach((ma,sl)->{
            if(sl==null)throw new NghiepVuException("Vui lòng nhập số lượng dịch vụ.");
            b.themDichVu(dichVuRepository.findById(ma).orElseThrow(()->new NghiepVuException("Không tìm thấy dịch vụ.")),sl);
        });
        return b;
    }
    public DatPhong layDon(String ma){return datPhongRepository.findById(ma).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));}
    public DatPhong khoaDon(String ma){return datPhongRepository.khoaTheoMa(ma).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));}
    public void kiemTraChuDon(DatPhong d,String email){
        if(!d.getKhachHang().getEmail().equalsIgnoreCase(email))throw new AccessDeniedException("Bạn không được truy cập đơn này.");
    }
    public void napChiTiet(DatPhong d){
        d.getKhachHang().getHoTen();
        if(d.getNhanVien()!=null)d.getNhanVien().getHoTen();
        d.getDsChiTietDatPhong().forEach(ct->{ct.getPhong().getSoPhong();ct.getPhong().getLoaiPhong().getTenLoaiPhong();});
        d.getDsChiTietDichVu().forEach(ct->ct.getDichVu().getTenDichVu());
    }
    public ChiTietDonDto chiTietCuaKhach(String ma,String email){DatPhong d=layDon(ma);kiemTraChuDon(d,email);return chiTiet(d);}
    public ChiTietDonDto chiTietQuanTri(String ma){return chiTiet(layDon(ma));}
    public ChiTietDonDto chiTiet(DatPhong d){
        napChiTiet(d);
        HoaDon hd=hoaDonRepository.findByDatPhongMaDatPhong(d.getMaDatPhong()).orElse(null);
        List<ThanhToan> ds=hd==null?List.of():thanhToanRepository.findByHoaDonMaHoaDonOrderByNgayThanhToanAsc(hd.getMaHoaDon());
        BigDecimal daTra=ds.stream().filter(t->t.getTrangThai()==TrangThaiGiaoDich.THANH_CONG)
                .map(ThanhToan::getSoTien).reduce(BigDecimal.ZERO,BigDecimal::add);
        LocalDateTime han=hetHan(d);
        return new ChiTietDonDto(d,GhepChiPhi.tuDonDatPhong(d),hd,ds,daTra,d.getTongTien().subtract(daTra),
                han,d.getTrangThai()==TrangThaiDatPhong.CHO_THANH_TOAN&&!han.isBefore(bayGio()),coTheHuy(d),
                d.getTrangThai()==TrangThaiDatPhong.HOAN_TAT&&!danhGiaRepository.existsByDatPhongMaDatPhong(d.getMaDatPhong()));
    }
    public List<DatPhong> lichSu(String email,TrangThaiDatPhong tt){
        return datPhongRepository.findByKhachHangEmailOrderByNgayDatDesc(email).stream()
                .filter(d->tt==null||d.getTrangThai()==tt).peek(this::napChiTiet).toList();
    }
    public List<DatPhong> quanLy(TrangThaiDatPhong tt,LocalDate tu,LocalDate den,String ma,String email){
        if(tu!=null&&den!=null&&den.isBefore(tu))throw new NghiepVuException("Đến ngày phải từ ngày trở đi.");
        return datPhongRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,"ngayDat")).stream()
            .filter(d->tt==null||d.getTrangThai()==tt).filter(d->tu==null||!d.getNgayDat().toLocalDate().isBefore(tu))
            .filter(d->den==null||!d.getNgayDat().toLocalDate().isAfter(den))
            .filter(d->ma==null||d.getMaDatPhong().toLowerCase(Locale.ROOT).contains(ma.trim().toLowerCase(Locale.ROOT)))
            .filter(d->email==null||d.getKhachHang().getEmail().toLowerCase(Locale.ROOT).contains(email.trim().toLowerCase(Locale.ROOT)))
            .peek(this::napChiTiet).toList();
    }
    public boolean coTheHuy(DatPhong d){
        return d.getTrangThai()==TrangThaiDatPhong.CHO_THANH_TOAN
                ||(d.getTrangThai()==TrangThaiDatPhong.DA_XAC_NHAN
                &&!LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")).isAfter(d.getNgayNhanPhong().minusDays(thamSoService.thoiHanHuyDonNgay())));
    }
    @Transactional
    public boolean huyDon(String ma,String email){
        DatPhong d=khoaDon(ma);kiemTraChuDon(d,email);
        if(!coTheHuy(d))throw new NghiepVuException("Đơn không được hủy hoặc đã quá thời hạn hủy.");
        boolean daTra=chiTiet(d).daTra().signum()>0;huyQuaHan(d);return daTra;
    }
    public LocalDateTime hetHan(DatPhong d){return d.getNgayDat().plusMinutes(thamSoService.thoiGianGiuPhongPhut());}
    public static LocalDateTime bayGio(){return LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));}
    @Transactional
    public void huyDonQuaHan(){
        for(String ma:datPhongRepository.timDonQuaHan(TrangThaiDatPhong.CHO_THANH_TOAN,bayGio().minusMinutes(thamSoService.thoiGianGiuPhongPhut()))){
            DatPhong d=khoaDon(ma);
            if(d.getTrangThai()==TrangThaiDatPhong.CHO_THANH_TOAN&&hetHan(d).isBefore(bayGio()))huyQuaHan(d);
        }
    }
    public void huyQuaHan(DatPhong d){
        d.setTrangThai(TrangThaiDatPhong.DA_HUY);
        hoaDonRepository.findByDatPhongMaDatPhong(d.getMaDatPhong()).ifPresent(h->
            thanhToanRepository.findByHoaDonMaHoaDonOrderByNgayThanhToanAsc(h.getMaHoaDon()).stream()
                .filter(t->t.getTrangThai()==TrangThaiGiaoDich.DANG_XU_LY).forEach(t->t.setTrangThai(TrangThaiGiaoDich.THAT_BAI)));
    }
}
