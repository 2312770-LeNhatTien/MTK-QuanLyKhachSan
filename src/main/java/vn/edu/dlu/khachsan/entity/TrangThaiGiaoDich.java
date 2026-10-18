package vn.edu.dlu.khachsan.entity;

public enum TrangThaiGiaoDich {
    DANG_XU_LY("Đang xử lý"),
    THANH_CONG("Thành công"),
    THAT_BAI("Thất bại");

    private final String tenHienThi;

    TrangThaiGiaoDich(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}

