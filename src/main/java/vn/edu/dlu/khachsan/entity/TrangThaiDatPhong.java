package vn.edu.dlu.khachsan.entity;

public enum TrangThaiDatPhong {
    CHO_THANH_TOAN("Chờ thanh toán"),
    DA_XAC_NHAN("Đã xác nhận"),
    DA_HUY("Đã hủy"),
    HOAN_TAT("Hoàn tất");

    private final String tenHienThi;

    TrangThaiDatPhong(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}

