package vn.edu.dlu.khachsan.service;
import java.math.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.dlu.khachsan.dto.*;
import vn.edu.dlu.khachsan.entity.*;
import vn.edu.dlu.khachsan.repository.*;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
@Service @Transactional(readOnly=true)
public class BaoCaoService {
 private final ThanhToanRepository tt;private final DatPhongRepository dp;private final PhongRepository phong;private final ThanhToanService thanhToan;
 public BaoCaoService(ThanhToanRepository t,DatPhongRepository d,PhongRepository p,ThanhToanService s){tt=t;dp=d;phong=p;thanhToan=s;}
 public BaoCaoDto baoCao(LocalDate tu,LocalDate den){
  if(tu==null||den==null||den.isBefore(tu))throw new NghiepVuException("Khoảng ngày không hợp lệ.");
  Map<LocalDate,BigDecimal> ngay=new LinkedHashMap<>();tu.datesUntil(den.plusDays(1)).forEach(d->ngay.put(d,BigDecimal.ZERO));
  Map<String,BigDecimal> phuongThuc=new LinkedHashMap<>();thanhToan.phuongThucTrucTuyen().forEach(p->phuongThuc.put(p.getMa(),BigDecimal.ZERO));thanhToan.phuongThucTaiQuay().forEach(p->phuongThuc.put(p.getMa(),BigDecimal.ZERO));
  for(ThanhToan t:tt.findAll()){
   LocalDate n=t.getNgayThanhToan().toLocalDate();
   if(t.getTrangThai()==TrangThaiGiaoDich.THANH_CONG&&!n.isBefore(tu)&&!n.isAfter(den)){ngay.merge(n,t.getSoTien(),BigDecimal::add);phuongThuc.merge(t.getPhuongThuc(),t.getSoTien(),BigDecimal::add);}
  }
  Map<TrangThaiDatPhong,Long> soDon=new LinkedHashMap<>();Arrays.stream(TrangThaiDatPhong.values()).forEach(s->soDon.put(s,0L));long demPhong=0;
  for(DatPhong d:dp.findAll()){
   if(!d.getNgayDat().toLocalDate().isBefore(tu)&&!d.getNgayDat().toLocalDate().isAfter(den))soDon.merge(d.getTrangThai(),1L,Long::sum);
   if(d.getTrangThai()==TrangThaiDatPhong.DA_XAC_NHAN||d.getTrangThai()==TrangThaiDatPhong.HOAN_TAT){
    LocalDate dau=d.getNgayNhanPhong().isAfter(tu)?d.getNgayNhanPhong():tu;
    LocalDate cuoi=d.getNgayTraPhong().isBefore(den.plusDays(1))?d.getNgayTraPhong():den.plusDays(1);
    if(cuoi.isAfter(dau))demPhong+=ChronoUnit.DAYS.between(dau,cuoi)*d.getDsChiTietDatPhong().size();
   }
  }
  long mau=phong.findAll().stream().filter(p->p.layTrangThai().coTheNhanDat()).count()*(ChronoUnit.DAYS.between(tu,den)+1);
  BigDecimal congSuat=mau==0?BigDecimal.ZERO:BigDecimal.valueOf(demPhong).multiply(new BigDecimal("100")).divide(BigDecimal.valueOf(mau),2,RoundingMode.HALF_UP);
  return new BaoCaoDto(ngay,phuongThuc,soDon,ngay.values().stream().reduce(BigDecimal.ZERO,BigDecimal::add),demPhong,mau,congSuat);
 }
}
