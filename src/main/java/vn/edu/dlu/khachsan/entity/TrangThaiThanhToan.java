package vn.edu.dlu.khachsan.entity;

public enum TrangThaiThanhToan {
    CHUA_THANH_TOAN("Chưa thanh toán"),
    DA_COC("Đã đặt cọc"),
    DA_HOAN_TAT("Đã thanh toán đủ");

    private final String tenHienThi;

    TrangThaiThanhToan(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}

