package vn.edu.dlu.khachsan.service;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import vn.edu.dlu.khachsan.dto.DanhGiaDto;
import vn.edu.dlu.khachsan.entity.*;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.repository.DanhGiaRepository;
@Service
@Transactional(readOnly=true)
public class DanhGiaService {
 private final DanhGiaRepository danhGiaRepository;
 private final DatPhongService datPhongService;
 private final MaSinhService maSinhService;
 public DanhGiaService(DanhGiaRepository d,DatPhongService dp,MaSinhService m){danhGiaRepository=d;datPhongService=dp;maSinhService=m;}
 public List<DanhGia> moiNhat(){return danhGiaRepository.findTop6ByOrderByNgayDanhGiaDesc();}
 @Transactional
 public void danhGia(String ma,String email,DanhGiaDto dto){
  maSinhService.khoaGiaoDich();
  DatPhong d=datPhongService.khoaDon(ma);datPhongService.kiemTraChuDon(d,email);
  if(d.getTrangThai()!=TrangThaiDatPhong.HOAN_TAT)throw new NghiepVuException("Chỉ được đánh giá đơn đã hoàn tất.");
  if(danhGiaRepository.existsByDatPhongMaDatPhong(ma))throw new NghiepVuException("Đơn này đã được đánh giá.");
  if(dto.getSoSao()<1||dto.getSoSao()>5||dto.getNoiDung()!=null&&dto.getNoiDung().length()>500)
   throw new NghiepVuException("Đánh giá phải từ 1 đến 5 sao, nội dung tối đa 500 ký tự.");
  DanhGia dg=new DanhGia();dg.setMaDanhGia(maSinhService.sinhMaDanhGia());dg.setDatPhong(d);
  dg.setSoSao(dto.getSoSao());dg.setNoiDung(dto.getNoiDung());danhGiaRepository.saveAndFlush(dg);
 }
}
