package vn.edu.dlu.khachsan.service;
import java.util.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.dlu.khachsan.dto.*;
import vn.edu.dlu.khachsan.entity.*;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.pattern.decorator.TienPhong;
import vn.edu.dlu.khachsan.repository.*;
@Service
@Transactional(readOnly=true)
public class PhongService {
    private final PhongRepository phongRepository;
    private final LoaiPhongRepository loaiPhongRepository;
    private final DatPhongRepository datPhongRepository;
    private final ChiTietDatPhongRepository chiTietRepository;
    private final MaSinhService maSinhService;
    public PhongService(PhongRepository p,LoaiPhongRepository l,DatPhongRepository d,ChiTietDatPhongRepository ct,MaSinhService ms){
        phongRepository=p;loaiPhongRepository=l;datPhongRepository=d;chiTietRepository=ct;maSinhService=ms;
    }
    public List<LoaiPhong> dsLoaiPhong(){return loaiPhongRepository.findAll(org.springframework.data.domain.Sort.by("maLoaiPhong"));}
    public List<Phong> dsPhong(){return phongRepository.layTatCaKemLoai();}
    public List<KetQuaTimPhong> timPhong(TimPhongDto dto){
        if(dto.getNgayNhanPhong()==null||dto.getNgayTraPhong()==null
                ||dto.getNgayNhanPhong().isBefore(LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")))
                ||!dto.getNgayTraPhong().isAfter(dto.getNgayNhanPhong())||dto.getSoKhach()<1)
            throw new NghiepVuException("Ngày lưu trú hoặc số khách không hợp lệ.");
        Map<String,List<Phong>> nhom=new LinkedHashMap<>();
        for(Phong p:dsPhong()){
            if(p.getLoaiPhong().getSucChua()<dto.getSoKhach()||!p.layTrangThai().coTheNhanDat())continue;
            if(dto.getMaLoaiPhong()!=null&&!dto.getMaLoaiPhong().isBlank()
                    &&!dto.getMaLoaiPhong().equals(p.getLoaiPhong().getMaLoaiPhong()))continue;
            if(!conLich(p.getMaPhong(),dto.getNgayNhanPhong(),dto.getNgayTraPhong()))continue;
            nhom.computeIfAbsent(p.getLoaiPhong().getMaLoaiPhong(),k->new ArrayList<>()).add(p);
        }
        return nhom.values().stream().map(ds->{
            ChiTietDatPhong ct=new ChiTietDatPhong();
            ct.setPhong(ds.getFirst());ct.setGiaThoiDiemDat(ds.getFirst().getLoaiPhong().getGiaMoiDem());
            return new KetQuaTimPhong(ds.getFirst().getLoaiPhong(),ds,
                    new TienPhong(List.of(ct),ChronoUnit.DAYS.between(dto.getNgayNhanPhong(),dto.getNgayTraPhong())).tinhTien());
        }).toList();
    }
    public boolean conLich(String ma,LocalDate nhan,LocalDate tra){
        return datPhongRepository.demTrungLich(ma,nhan,tra,
                List.of(TrangThaiDatPhong.CHO_THANH_TOAN,TrangThaiDatPhong.DA_XAC_NHAN))==0;
    }
    public Phong layPhong(String ma){Phong p=phongRepository.findById(ma).orElseThrow(()->new NghiepVuException("Không tìm thấy phòng."));p.getLoaiPhong().getTenLoaiPhong();return p;}
    public LoaiPhong layLoai(String ma){return loaiPhongRepository.findById(ma).orElseThrow(()->new NghiepVuException("Không tìm thấy loại phòng."));}
    public Map<Integer,List<Phong>> theoTang(){Map<Integer,List<Phong>> nhom=new TreeMap<>();dsPhong().forEach(p->nhom.computeIfAbsent(p.getTang()==null?0:p.getTang(),k->new ArrayList<>()).add(p));return nhom;}
    public List<Phong> canDon(){return dsPhong().stream().filter(p->p.layTrangThai().thaoTacHopLe().contains(vn.edu.dlu.khachsan.pattern.state.ThaoTacPhong.DON_XONG)).toList();}
    @Transactional public void thaoTac(String ma,vn.edu.dlu.khachsan.pattern.state.ThaoTacPhong tac){
        Phong p=phongRepository.khoaTheoMa(ma).orElseThrow(()->new NghiepVuException("Không tìm thấy phòng."));
        // Chọn thao tác buồng phòng; State quyết định chuyển trạng thái có hợp lệ không.
        Map<vn.edu.dlu.khachsan.pattern.state.ThaoTacPhong,Runnable> cacThaoTac=Map.of(
            vn.edu.dlu.khachsan.pattern.state.ThaoTacPhong.DON_XONG,p::donXong,
            vn.edu.dlu.khachsan.pattern.state.ThaoTacPhong.BAO_TRI,p::baoTri,
            vn.edu.dlu.khachsan.pattern.state.ThaoTacPhong.KET_THUC_BAO_TRI,p::ketThucBaoTri);
        Runnable thucHien=cacThaoTac.get(tac);if(thucHien==null)throw new NghiepVuException("Thao tác buồng phòng không hợp lệ.");thucHien.run();
    }
    @Transactional public void luuLoai(String ma,LoaiPhongDto dto){
        maSinhService.khoaGiaoDich();
        String ten=dto.tenLoaiPhong().trim();if(loaiPhongRepository.existsByTenLoaiPhongAndMaLoaiPhongNot(ten,ma==null?"":ma))throw new NghiepVuException("Tên loại phòng đã tồn tại.");
        LoaiPhong l=ma==null?new LoaiPhong():layLoai(ma);if(ma==null)l.setMaLoaiPhong(maSinhService.sinhMaLoaiPhong());
        l.setTenLoaiPhong(ten);l.setGiaMoiDem(dto.giaMoiDem());l.setSucChua(dto.sucChua());l.setMoTa(dto.moTa());loaiPhongRepository.saveAndFlush(l);
    }
    @Transactional public void xoaLoai(String ma){
        if(phongRepository.existsByLoaiPhongMaLoaiPhong(ma))throw new NghiepVuException("Không thể xóa loại phòng đang có phòng.");loaiPhongRepository.delete(layLoai(ma));loaiPhongRepository.flush();
    }
    @Transactional public void luuPhong(String ma,PhongDto dto){
        maSinhService.khoaGiaoDich();Phong p=ma==null?new Phong():phongRepository.khoaTheoMa(ma).orElseThrow(()->new NghiepVuException("Không tìm thấy phòng."));
        if(ma==null){if(phongRepository.existsBySoPhong(dto.soPhong()))throw new NghiepVuException("Số phòng đã tồn tại.");p.setMaPhong(maSinhService.sinhMaPhong(dto.soPhong()));p.setSoPhong(dto.soPhong());}
        else if(!p.getSoPhong().equals(dto.soPhong()))throw new NghiepVuException("Không đổi số phòng để giữ nguyên mã phòng và lịch sử.");
        p.setTang(dto.tang());p.setLoaiPhong(layLoai(dto.maLoaiPhong()));phongRepository.saveAndFlush(p);
    }
    @Transactional public void xoaPhong(String ma){
        Phong p=phongRepository.khoaTheoMa(ma).orElseThrow(()->new NghiepVuException("Không tìm thấy phòng."));
        if(chiTietRepository.existsByPhongMaPhong(ma))throw new NghiepVuException("Phòng đã có lịch sử đặt. Vui lòng dùng chức năng Bảo trì thay thế.");phongRepository.delete(p);phongRepository.flush();
    }
}
