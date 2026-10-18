package vn.edu.dlu.khachsan.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.dlu.khachsan.entity.ThamSo;
import vn.edu.dlu.khachsan.exception.NghiepVuException;
import vn.edu.dlu.khachsan.repository.ThamSoRepository;

@Service
@Transactional(readOnly = true)
public class ThamSoService {

    public static final String THOI_GIAN_GIU_PHONG = "THOI_GIAN_GIU_PHONG";
    public static final String TI_LE_DAT_COC = "TI_LE_DAT_COC";
    public static final String THOI_HAN_HUY_DON = "THOI_HAN_HUY_DON";
    private static final Set<String> CAC_MA = Set.of(THOI_GIAN_GIU_PHONG, TI_LE_DAT_COC, THOI_HAN_HUY_DON);
    private final ThamSoRepository thamSoRepository;

    public ThamSoService(ThamSoRepository thamSoRepository) {
        this.thamSoRepository = thamSoRepository;
    }

    public List<ThamSo> layTatCa() {
        return thamSoRepository.findAll(Sort.by("maThamSo"));
    }

    public BigDecimal layGiaTri(String maThamSo) {
        ThamSo thamSo = timThamSo(maThamSo);
        kiemTraGiaTri(maThamSo, thamSo.getGiaTri());
        return thamSo.getGiaTri();
    }

    public long thoiGianGiuPhongPhut() {
        return layGiaTri(THOI_GIAN_GIU_PHONG).longValueExact();
    }

    public BigDecimal tiLeDatCoc() {
        return layGiaTri(TI_LE_DAT_COC);
    }

    public long thoiHanHuyDonNgay() {
        return layGiaTri(THOI_HAN_HUY_DON).longValueExact();
    }

    @Transactional
    public void capNhat(String maThamSo, BigDecimal giaTri) {
        ThamSo thamSo = timThamSo(maThamSo);
        kiemTraGiaTri(maThamSo, giaTri);
        thamSo.setGiaTri(giaTri);
        thamSoRepository.save(thamSo);
    }

    private ThamSo timThamSo(String maThamSo) {
        if (maThamSo == null || !CAC_MA.contains(maThamSo)) {
            throw new NghiepVuException("Mã tham số không hợp lệ.");
        }
        return thamSoRepository.findById(maThamSo)
                .orElseThrow(() -> new NghiepVuException("Chưa cấu hình tham số " + maThamSo + "."));
    }

    private void kiemTraGiaTri(String maThamSo, BigDecimal giaTri) {
        if (giaTri == null || giaTri.signum() <= 0 || giaTri.stripTrailingZeros().scale() > 0
                || giaTri.compareTo(new BigDecimal("9999999999")) > 0) {
            throw new NghiepVuException("Giá trị tham số phải là số nguyên dương, tối đa 10 chữ số.");
        }
        if (TI_LE_DAT_COC.equals(maThamSo) && giaTri.compareTo(new BigDecimal("100")) > 0) {
            throw new NghiepVuException("Tỉ lệ đặt cọc phải từ 1 đến 100%.");
        }
    }
}
