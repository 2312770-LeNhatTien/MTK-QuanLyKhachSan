package vn.edu.dlu.khachsan.service;
import java.math.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import vn.edu.dlu.khachsan.dto.*;
import vn.edu.dlu.khachsan.entity.*;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.pattern.strategy.*;
import vn.edu.dlu.khachsan.repository.*;
@Service
@Transactional(readOnly=true)
public class ThanhToanService {
    private final Map<String,PhuongThucThanhToan> dsPhuongThuc;
    private final DatPhongService datPhongService;
    private final HoaDonRepository hoaDonRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final MaSinhService maSinhService;
    private final ThamSoService thamSoService;
    private final EmailLogService emailLogService;
    public ThanhToanService(List<PhuongThucThanhToan> ds,DatPhongService d,HoaDonRepository h,ThanhToanRepository t,MaSinhService ms,ThamSoService ts,EmailLogService e){
        dsPhuongThuc=ds.stream().collect(Collectors.toMap(PhuongThucThanhToan::getMa,Function.identity()));
        datPhongService=d;hoaDonRepository=h;thanhToanRepository=t;maSinhService=ms;thamSoService=ts;emailLogService=e;
    }
    public List<PhuongThucThanhToan> phuongThucTrucTuyen(){return dsPhuongThuc.values().stream().filter(p->!p.dungTaiQuay()).sorted(Comparator.comparing(PhuongThucThanhToan::getMa)).toList();}
    public List<PhuongThucThanhToan> phuongThucTaiQuay(){return dsPhuongThuc.values().stream().filter(PhuongThucThanhToan::dungTaiQuay).sorted(Comparator.comparing(PhuongThucThanhToan::getMa)).toList();}
    public String tenPhuongThuc(String ma){return layPhuongThuc(ma).getTenHienThi();}
    public PhuongThucThanhToan layPhuongThuc(String ma){PhuongThucThanhToan p=dsPhuongThuc.get(ma);if(p==null)throw new NghiepVuException("Phương thức thanh toán không hợp lệ.");return p;}
    public BigDecimal cocToiThieu(BigDecimal tong){return tong.multiply(thamSoService.tiLeDatCoc()).divide(new BigDecimal("100"),2,RoundingMode.CEILING);}
    public BigDecimal daTra(HoaDon h){return thanhToanRepository.tongDaTra(h.getMaHoaDon(),TrangThaiGiaoDich.THANH_CONG);}
    public void capNhatHoaDon(HoaDon h){
        BigDecimal daTra=daTra(h);
        h.setTrangThaiThanhToan(daTra.signum()==0?TrangThaiThanhToan.CHUA_THANH_TOAN:daTra.compareTo(h.getTongTien())>=0?TrangThaiThanhToan.DA_HOAN_TAT:TrangThaiThanhToan.DA_COC);
    }
    @Transactional(isolation=Isolation.READ_COMMITTED)
    public KetQuaXuLy batDauTrucTuyen(String ma,String email,ThanhToanDto dto){
        maSinhService.khoaGiaoDich();
        DatPhong d=datPhongService.khoaDon(ma);datPhongService.kiemTraChuDon(d,email);
        if(d.getTrangThai()!=TrangThaiDatPhong.CHO_THANH_TOAN||datPhongService.hetHan(d).isBefore(DatPhongService.bayGio()))throw new NghiepVuException("Đơn không còn trong thời gian chờ thanh toán.");
        PhuongThucThanhToan p=layPhuongThuc(dto.getMaPhuongThuc());
        if(p.dungTaiQuay())throw new NghiepVuException("Vui lòng chọn phương thức thanh toán trực tuyến.");
        HoaDon hd=hoaDonRepository.findByDatPhongMaDatPhong(ma).orElse(null);
        BigDecimal conLai=d.getTongTien().subtract(hd==null?BigDecimal.ZERO:daTra(hd));
        BigDecimal soTien=dto.isToanBo()?conLai:dto.getSoTien();
        if(soTien==null||soTien.scale()>2||soTien.compareTo(cocToiThieu(d.getTongTien()))<0||soTien.compareTo(conLai)>0)throw new NghiepVuException("Số tiền phải từ mức cọc tối thiểu đến số còn lại, tối đa 2 số thập phân.");
        if(hd==null){
            hd=new HoaDon();hd.setMaHoaDon(maSinhService.sinhMaHoaDon());hd.setDatPhong(d);hd.setTongTien(d.getTongTien());
            hd=hoaDonRepository.saveAndFlush(hd);
        }
        for(ThanhToan t:thanhToanRepository.findByHoaDonMaHoaDonOrderByNgayThanhToanAsc(hd.getMaHoaDon()))if(t.getTrangThai()==TrangThaiGiaoDich.DANG_XU_LY)return p.xuLy(t,null);
        ThanhToan t=new ThanhToan();t.setMaThanhToan(maSinhService.sinhMaThanhToan());t.setHoaDon(hd);t.setSoTien(soTien);t.setPhuongThuc(p.getMa());
        KetQuaXuLy k=p.xuLy(t,null);t.setTrangThai(k.trangThai());thanhToanRepository.saveAndFlush(t);return k;
    }
    public ThanhToan giaoDichCuaKhach(String ma,String email){
        ThanhToan t=thanhToanRepository.findById(ma).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        datPhongService.kiemTraChuDon(t.getHoaDon().getDatPhong(),email);
        t.getHoaDon().getDatPhong().getMaDatPhong();return t;
    }
    @Transactional(isolation=Isolation.READ_COMMITTED)
    public void xacNhanTrucTuyen(String ma,boolean thanhCong,String email){
        ThanhToan banDau=giaoDichCuaKhach(ma,email);
        DatPhong d=datPhongService.khoaDon(banDau.getHoaDon().getDatPhong().getMaDatPhong());
        ThanhToan t=thanhToanRepository.khoaTheoMa(ma).orElseThrow();
        if(layPhuongThuc(t.getPhuongThuc()).dungTaiQuay())throw new AccessDeniedException("Giao dịch không phải trực tuyến.");
        if(t.getTrangThai()!=TrangThaiGiaoDich.DANG_XU_LY)return;
        if(d.getTrangThai()!=TrangThaiDatPhong.CHO_THANH_TOAN||datPhongService.hetHan(d).isBefore(DatPhongService.bayGio())){
            datPhongService.huyQuaHan(d);t.setTrangThai(TrangThaiGiaoDich.THAT_BAI);return;
        }
        t.setTrangThai(thanhCong?TrangThaiGiaoDich.THANH_CONG:TrangThaiGiaoDich.THAT_BAI);
        thanhToanRepository.flush();capNhatHoaDon(t.getHoaDon());
        if(thanhCong){d.setTrangThai(TrangThaiDatPhong.DA_XAC_NHAN);emailLogService.ghiXacNhan(datPhongService.chiTiet(d));}
    }
    @Transactional(isolation=Isolation.READ_COMMITTED)
    public KetQuaXuLy thanhToan(String maHoaDon,BigDecimal soTien,String maPhuongThuc,DuLieuThanhToan duLieu){
        maSinhService.khoaGiaoDich();
        HoaDon hd=hoaDonRepository.findById(maHoaDon).orElseThrow(()->new NghiepVuException("Không tìm thấy hóa đơn."));
        DatPhong d=datPhongService.khoaDon(hd.getDatPhong().getMaDatPhong());
        if(d.getTrangThai()!=TrangThaiDatPhong.DA_XAC_NHAN||d.getDsChiTietDatPhong().stream().anyMatch(c->!c.getPhong().layTrangThai().thaoTacHopLe().contains(vn.edu.dlu.khachsan.pattern.state.ThaoTacPhong.CHECK_OUT)))
            throw new NghiepVuException("Chỉ thu tiền trả phòng cho đơn đã check-in.");
        BigDecimal conLai=hd.getTongTien().subtract(daTra(hd));
        if(soTien==null||soTien.signum()<=0||soTien.compareTo(conLai)!=0)throw new NghiepVuException("Số tiền thanh toán phải bằng số còn lại.");
        PhuongThucThanhToan p=layPhuongThuc(maPhuongThuc);
        if(!p.dungTaiQuay())throw new NghiepVuException("Vui lòng chọn phương thức thanh toán tại quầy.");
        ThanhToan t=new ThanhToan();t.setMaThanhToan(maSinhService.sinhMaThanhToan());t.setHoaDon(hd);t.setSoTien(conLai);t.setPhuongThuc(p.getMa());
        KetQuaXuLy k=p.xuLy(t,duLieu);t.setTrangThai(k.trangThai());thanhToanRepository.saveAndFlush(t);capNhatHoaDon(hd);return k;
    }
}
