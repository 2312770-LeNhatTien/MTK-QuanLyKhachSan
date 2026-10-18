package vn.edu.dlu.khachsan.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class DinhDangTien {

    private DinhDangTien() {
    }

    public static String hienThi(BigDecimal soTien) {
        NumberFormat dinhDang = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
        dinhDang.setMinimumFractionDigits(0);
        dinhDang.setMaximumFractionDigits(2);
        return dinhDang.format(soTien) + " đ";
    }
}
