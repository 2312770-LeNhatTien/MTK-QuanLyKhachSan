package vn.edu.dlu.khachsan.entity;

public enum CachTinhDichVu {
    THEO_DEM("Tính theo đêm"),
    MOT_LAN("Tính một lần");

    private final String tenHienThi;

    CachTinhDichVu(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}

