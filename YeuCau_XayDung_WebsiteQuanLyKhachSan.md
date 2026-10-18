# YÊU CẦU XÂY DỰNG WEBSITE QUẢN LÝ KHÁCH SẠN (Java – Spring Boot)

> Tài liệu này giao cho AI Agent (Claude Code, Cursor…) để xây dựng **website chạy được hoàn chỉnh dùng cho demo**.
> Website phải **áp dụng đúng 4 mẫu thiết kế ở Mục 6**: Builder, Decorator, State, Strategy. Đây là phần quan trọng nhất của dự án.
> **Không tự thêm chức năng ngoài danh sách bên dưới.**

---

## 0. Hướng dẫn cho Agent

1. Đọc hết tài liệu trước khi code.
2. Làm theo thứ tự ở **Mục 14 – Kế hoạch thực hiện**. Xong bước nào chạy thử (`./mvnw spring-boot:run`) bước đó.
3. Giữ đúng tên bảng, tên cột, mã trạng thái ở Mục 4. Tên class Java dùng PascalCase, tên field dùng camelCase, là tiếng Việt không dấu tương ứng với tên cột (VD cột `MaDatPhong` → field `maDatPhong`).
4. **Mục 6 bắt buộc làm đúng cấu trúc**: đủ các lớp, đúng tên lớp, đúng vai trò, đặt trong package `pattern`. Không được thay mẫu bằng cách viết khác (VD dùng `switch-case` thay cho State, dùng `if-else` theo phương thức thay cho Strategy). Không thêm mẫu thiết kế nào khác vào package `pattern`.
5. Nghiệp vụ liên quan đến 4 mẫu **phải đi qua các lớp mẫu**, không được viết tắt ở chỗ khác (xem "Nơi sử dụng" trong từng mẫu).
6. Giao diện **tiếng Việt có dấu**. Mã trạng thái lưu CSDL không dấu (VD `CHO_THANH_TOAN`), khi hiển thị đổi sang tiếng Việt (Mục 4.3).
7. **Không dùng Lombok**. Viết getter/setter, constructor bình thường để code dễ đọc khi trích dẫn.
8. Khi có điểm chưa rõ, chọn cách **đơn giản nhất** mà vẫn đúng quy định ở Mục 5, và ghi lại lựa chọn đó vào `README.md`.

---

## 1. Tổng quan

Website cho **một khách sạn quy mô vừa và nhỏ, một cơ sở**. Gồm 2 phân hệ dùng chung một CSDL:

- **Phân hệ Khách hàng:** tìm phòng trống, đặt phòng kèm dịch vụ thêm, thanh toán trực tuyến, hủy đơn, xem lịch sử, đánh giá.
- **Phân hệ Quản trị** (`/admin`): dành cho nhân viên với 3 vai trò: Lễ tân, Nhân viên buồng phòng, Quản lý khách sạn.

---

## 2. Công nghệ bắt buộc

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | **Java 17 trở lên** (khuyến nghị JDK 21) |
| Framework | **Spring Boot 3.x**, build bằng **Maven** (có Maven Wrapper `mvnw`) |
| Web | Spring MVC + **Thymeleaf** |
| Giao diện | HTML, CSS, JavaScript, **Bootstrap 5**, **Chart.js** (lưu file trong `static/vendor/` để chạy không cần Internet) |
| Truy cập dữ liệu | **Spring Data JPA** (Hibernate), `ddl-auto=validate` |
| CSDL | **MySQL 8** (charset `utf8mb4`) |
| Tạo bảng | **Flyway** (`db/migration/V1__tao_bang.sql`) |
| Xác thực | **Spring Security**, form login, mật khẩu băm bằng **BCrypt** |
| Thanh toán trực tuyến | **Cổng thanh toán giả lập** ngay trong website (không tích hợp cổng thật) |
| Email | Ghi nội dung email ra file `.html` trong thư mục `email-logs/` (không gửi thật) |
| Kiểm thử | JUnit 5 (test cho các lớp mẫu thiết kế) |

---

## 3. Kiến trúc và cấu trúc thư mục

Kiến trúc nhiều lớp: **Controller → Service → Repository → MySQL**. Controller **không** gọi Repository trực tiếp, mọi nghiệp vụ nằm trong Service.

```
khachsan/
├── pom.xml, mvnw, mvnw.cmd
├── README.md
├── GHI_CHU_MAU_THIET_KE.md          (Mục 6.5)
├── docs/uml/                         (sơ đồ lớp PlantUML cho 4 mẫu – Mục 6.5)
├── email-logs/
└── src/
    ├── main/java/vn/edu/dlu/khachsan/
    │   ├── KhachSanApplication.java
    │   ├── config/                   SecurityConfig, WebConfig
    │   ├── controller/               TrangChuController, TaiKhoanController, DatPhongController,
    │   │                             ThanhToanController, DanhGiaController
    │   ├── controller/admin/         AdminTrangChuController, AdminDatPhongController,
    │   │                             NhanTraPhongController, HoaDonController, PhongController,
    │   │                             NhanVienController, BaoCaoController, ThamSoController
    │   ├── service/                  TaiKhoanService, PhongService, DatPhongService,
    │   │                             ThanhToanService, NhanTraPhongService, DanhGiaService,
    │   │                             NhanVienService, BaoCaoService, ThamSoService,
    │   │                             EmailLogService, MaSinhService
    │   ├── repository/
    │   ├── entity/
    │   ├── dto/
    │   ├── exception/                NghiepVuException, xử lý lỗi chung (@ControllerAdvice)
    │   ├── scheduler/                HuyDonQuaHanJob
    │   ├── seed/                     DuLieuMauRunner
    │   └── pattern/
    │       ├── builder/              DatPhongBuilder, PhongChon, DichVuChon
    │       ├── decorator/            ChiPhiDatPhong, DongChiPhi, TienPhong, DichVuDecorator,
    │       │                         DichVuTheoDem, DichVuMotLan, GhepChiPhi
    │       ├── state/                TrangThaiPhong, ThaoTacPhong, TrangThaiPhongCoBan,
    │       │                         PhongTrong, PhongCoKhach, PhongDangDon, PhongBaoTri
    │       └── strategy/             PhuongThucThanhToan, DuLieuThanhToan, KetQuaXuLy,
    │                                 ThanhToanTrucTuyen, ThanhToanTienMat, ThanhToanThe
    ├── main/resources/
    │   ├── application.properties
    │   ├── db/migration/V1__tao_bang.sql
    │   ├── templates/                layout khách hàng, layout quản trị, các trang
    │   └── static/                   css, js, vendor/
    └── test/java/vn/edu/dlu/khachsan/pattern/   (Mục 6.6)
```

---

## 4. Thiết kế dữ liệu (BẮT BUỘC làm đúng)

### 4.1. Danh sách bảng

| Bảng | Ý nghĩa |
|---|---|
| VAI_TRO | Danh mục vai trò nhân viên |
| NHAN_VIEN | Nhân viên và tài khoản quản trị |
| KHACH_HANG | Khách hàng và tài khoản website |
| LOAI_PHONG | Loại phòng, giá mỗi đêm, sức chứa |
| PHONG | Phòng và tình trạng hiện tại |
| DAT_PHONG | Đơn đặt phòng |
| CHI_TIET_DAT_PHONG | Từng phòng trong đơn, giá chốt lúc đặt |
| DICH_VU | Danh mục dịch vụ thêm (ăn sáng, đưa đón…) |
| CHI_TIET_DICH_VU | Dịch vụ khách chọn trong đơn, giá chốt lúc đặt |
| HOA_DON | Hóa đơn của đơn đặt phòng |
| THANH_TOAN | Các giao dịch thanh toán của hóa đơn |
| DANH_GIA | Đánh giá của khách cho đơn đã hoàn tất |
| THAM_SO | Tham số quy định có thể thay đổi |

Tên bảng và tên cột trong MySQL phải **đúng như trên** (dùng `@Table(name = "...")`, `@Column(name = "...")`).

### 4.2. Chi tiết các bảng

**VAI_TRO**
| Cột | Kiểu | Ràng buộc |
|---|---|---|
| MaVaiTro | VARCHAR(10) | PK |
| TenVaiTro | VARCHAR(50) | NOT NULL, UNIQUE |

**NHAN_VIEN**
| Cột | Kiểu | Ràng buộc |
|---|---|---|
| MaNV | VARCHAR(10) | PK |
| HoTen | VARCHAR(100) | NOT NULL |
| Email | VARCHAR(100) | NOT NULL, UNIQUE |
| SDT | VARCHAR(15) | NULL |
| MatKhau | VARCHAR(255) | NOT NULL (BCrypt) |
| MaVaiTro | VARCHAR(10) | NOT NULL, FK → VAI_TRO |

**KHACH_HANG**
| Cột | Kiểu | Ràng buộc |
|---|---|---|
| MaKH | VARCHAR(10) | PK |
| HoTen | VARCHAR(100) | NOT NULL |
| Email | VARCHAR(100) | NOT NULL, UNIQUE |
| SDT | VARCHAR(15) | NULL |
| MatKhau | VARCHAR(255) | NOT NULL (BCrypt) |
| DiaChi | VARCHAR(200) | NULL |
| NgayTao | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP |

**LOAI_PHONG**
| Cột | Kiểu | Ràng buộc |
|---|---|---|
| MaLoaiPhong | VARCHAR(10) | PK |
| TenLoaiPhong | VARCHAR(50) | NOT NULL, UNIQUE |
| GiaMoiDem | DECIMAL(12,2) | NOT NULL, CHECK > 0 |
| SucChua | INT | NOT NULL, CHECK > 0 |
| MoTa | VARCHAR(255) | NULL |

**PHONG**
| Cột | Kiểu | Ràng buộc |
|---|---|---|
| MaPhong | VARCHAR(10) | PK |
| SoPhong | VARCHAR(10) | NOT NULL, UNIQUE |
| Tang | INT | NULL |
| TrangThai | VARCHAR(20) | NOT NULL, DEFAULT 'TRONG', CHECK IN ('TRONG','CO_KHACH','DANG_DON','BAO_TRI') |
| MaLoaiPhong | VARCHAR(10) | NOT NULL, FK → LOAI_PHONG |

**DAT_PHONG**
| Cột | Kiểu | Ràng buộc |
|---|---|---|
| MaDatPhong | VARCHAR(10) | PK |
| NgayDat | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| NgayNhanPhong | DATE | NOT NULL |
| NgayTraPhong | DATE | NOT NULL, CHECK NgayTraPhong > NgayNhanPhong |
| TrangThai | VARCHAR(20) | NOT NULL, DEFAULT 'CHO_THANH_TOAN', CHECK IN ('CHO_THANH_TOAN','DA_XAC_NHAN','DA_HUY','HOAN_TAT') |
| TongTien | DECIMAL(12,2) | NOT NULL, DEFAULT 0 |
| MaKH | VARCHAR(10) | NOT NULL, FK → KHACH_HANG |
| MaNV | VARCHAR(10) | **NULL**, FK → NHAN_VIEN (lễ tân phụ trách) |

**CHI_TIET_DAT_PHONG**
| Cột | Kiểu | Ràng buộc |
|---|---|---|
| MaCTDP | VARCHAR(10) | PK |
| SoLuongKhach | INT | NOT NULL, CHECK > 0 |
| GiaThoiDiemDat | DECIMAL(12,2) | NOT NULL |
| MaDatPhong | VARCHAR(10) | NOT NULL, FK → DAT_PHONG |
| MaPhong | VARCHAR(10) | NOT NULL, FK → PHONG |

Ràng buộc bảng: UNIQUE (MaDatPhong, MaPhong).

**DICH_VU**
| Cột | Kiểu | Ràng buộc |
|---|---|---|
| MaDichVu | VARCHAR(10) | PK |
| TenDichVu | VARCHAR(100) | NOT NULL, UNIQUE |
| DonGia | DECIMAL(12,2) | NOT NULL, CHECK > 0 |
| CachTinh | VARCHAR(20) | NOT NULL, CHECK IN ('THEO_DEM','MOT_LAN') |
| DonViTinh | VARCHAR(30) | NOT NULL (VD "người/đêm", "lượt", "lần") |
| MoTa | VARCHAR(255) | NULL |

**CHI_TIET_DICH_VU**
| Cột | Kiểu | Ràng buộc |
|---|---|---|
| MaCTDV | VARCHAR(10) | PK |
| SoLuong | INT | NOT NULL, CHECK BETWEEN 1 AND 10 |
| DonGiaThoiDiemDat | DECIMAL(12,2) | NOT NULL |
| MaDatPhong | VARCHAR(10) | NOT NULL, FK → DAT_PHONG |
| MaDichVu | VARCHAR(10) | NOT NULL, FK → DICH_VU |

Ràng buộc bảng: UNIQUE (MaDatPhong, MaDichVu).

**HOA_DON**
| Cột | Kiểu | Ràng buộc |
|---|---|---|
| MaHoaDon | VARCHAR(10) | PK |
| NgayLap | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| TongTien | DECIMAL(12,2) | NOT NULL |
| TrangThaiThanhToan | VARCHAR(20) | NOT NULL, DEFAULT 'CHUA_THANH_TOAN', CHECK IN ('CHUA_THANH_TOAN','DA_COC','DA_HOAN_TAT') |
| MaDatPhong | VARCHAR(10) | NOT NULL, **UNIQUE**, FK → DAT_PHONG |

**THANH_TOAN**
| Cột | Kiểu | Ràng buộc |
|---|---|---|
| MaThanhToan | VARCHAR(10) | PK |
| PhuongThuc | VARCHAR(20) | NOT NULL, CHECK IN ('TRUC_TUYEN','TIEN_MAT','THE') |
| SoTien | DECIMAL(12,2) | NOT NULL, CHECK > 0 |
| NgayThanhToan | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| TrangThai | VARCHAR(20) | NOT NULL, DEFAULT 'DANG_XU_LY', CHECK IN ('DANG_XU_LY','THANH_CONG','THAT_BAI') |
| GhiChu | VARCHAR(200) | NULL |
| MaHoaDon | VARCHAR(10) | NOT NULL, FK → HOA_DON |

**DANH_GIA**
| Cột | Kiểu | Ràng buộc |
|---|---|---|
| MaDanhGia | VARCHAR(10) | PK |
| SoSao | INT | NOT NULL, CHECK BETWEEN 1 AND 5 |
| NoiDung | VARCHAR(500) | NULL |
| NgayDanhGia | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| MaDatPhong | VARCHAR(10) | NOT NULL, **UNIQUE**, FK → DAT_PHONG |

**THAM_SO**
| Cột | Kiểu | Ràng buộc |
|---|---|---|
| MaThamSo | VARCHAR(30) | PK |
| TenThamSo | VARCHAR(100) | NOT NULL |
| GiaTri | DECIMAL(12,2) | NOT NULL |
| DonVi | VARCHAR(20) | NULL |

Tất cả khóa ngoại dùng `ON DELETE RESTRICT`, **không xóa dây chuyền**.

### 4.3. Mã trạng thái và cách hiển thị

| Cột | Mã lưu CSDL | Hiển thị |
|---|---|---|
| PHONG.TrangThai | TRONG / CO_KHACH / DANG_DON / BAO_TRI | Trống / Đang có khách / Đang dọn / Bảo trì |
| DAT_PHONG.TrangThai | CHO_THANH_TOAN / DA_XAC_NHAN / DA_HUY / HOAN_TAT | Chờ thanh toán / Đã xác nhận / Đã hủy / Hoàn tất |
| HOA_DON.TrangThaiThanhToan | CHUA_THANH_TOAN / DA_COC / DA_HOAN_TAT | Chưa thanh toán / Đã đặt cọc / Đã thanh toán đủ |
| THANH_TOAN.TrangThai | DANG_XU_LY / THANH_CONG / THAT_BAI | Đang xử lý / Thành công / Thất bại |
| THANH_TOAN.PhuongThuc | TRUC_TUYEN / TIEN_MAT / THE | Trực tuyến / Tiền mặt / Thẻ ngân hàng |
| DICH_VU.CachTinh | THEO_DEM / MOT_LAN | Tính theo đêm / Tính một lần |

- Trạng thái phòng **được quản lý bằng mẫu State** (Mục 6.3). Tên hiển thị lấy từ `TrangThaiPhong.getTenHienThi()`.
- Tên hiển thị phương thức thanh toán lấy từ `PhuongThucThanhToan.getTenHienThi()` (Mục 6.4).
- Các trạng thái còn lại dùng `enum` Java, lưu dạng chuỗi (`@Enumerated(EnumType.STRING)`).

> Lưu ý: `PHONG.TrangThai` là **tình trạng hiện tại của phòng** (có khách đang ở hay không), **không** phải "đã có người đặt". Việc phòng còn trống theo khoảng ngày tính theo quy định QĐ3.

### 4.4. Quy tắc sinh mã

Mã có dạng **tiền tố + số thứ tự**, tối đa 10 ký tự, sinh trong `MaSinhService` (lấy mã lớn nhất hiện có + 1):

| Bảng | Mẫu | Ví dụ |
|---|---|---|
| KHACH_HANG | KH + 6 số | KH000001 |
| NHAN_VIEN | NV + 4 số | NV0001 |
| LOAI_PHONG | LP + 3 số | LP001 |
| PHONG | P + số phòng | P101 |
| DICH_VU | DV + 2 số | DV01 |
| DAT_PHONG | DP + 8 số | DP00000001 |
| CHI_TIET_DAT_PHONG | CT + 8 số | CT00000001 |
| CHI_TIET_DICH_VU | CV + 8 số | CV00000001 |
| HOA_DON | HD + 8 số | HD00000001 |
| THANH_TOAN | TT + 8 số | TT00000001 |
| DANH_GIA | DG + 8 số | DG00000001 |

### 4.5. Dữ liệu mẫu

Do `DuLieuMauRunner` (CommandLineRunner) tạo khi khởi động **nếu bảng NHAN_VIEN đang trống**. Ngày trong đơn mẫu tính **tương đối theo ngày hiện tại** để lúc nào chạy demo cũng dùng được. Các đơn mẫu phải được tạo bằng `DatPhongBuilder` (Mục 6.1).

- **VAI_TRO:** VT01 – Lễ tân; VT02 – Quản lý; VT03 – Buồng phòng.
- **THAM_SO:**

| MaThamSo | TenThamSo | GiaTri | DonVi |
|---|---|---|---|
| THOI_GIAN_GIU_PHONG | Thời gian giữ phòng chờ thanh toán | 15 | phút |
| TI_LE_DAT_COC | Tỉ lệ đặt cọc tối thiểu | 30 | % |
| THOI_HAN_HUY_DON | Số ngày tối thiểu trước ngày nhận phòng được hủy đơn | 1 | ngày |

- **NHAN_VIEN** (mật khẩu đều là `123456`):
  - NV0001 – Trần Thị Lễ Tân – `letan@khachsan.vn` – VT01
  - NV0002 – Nguyễn Văn Quản Lý – `quanly@khachsan.vn` – VT02
  - NV0003 – Lê Thị Buồng Phòng – `buongphong@khachsan.vn` – VT03
- **KHACH_HANG:** KH000001 – Nguyễn Văn A – `khach@gmail.com` – mật khẩu `123456`.
- **LOAI_PHONG:** LP001 Standard (500.000 đ, 2 người); LP002 Deluxe (800.000 đ, 2 người); LP003 Family (1.200.000 đ, 4 người).
- **PHONG:** 12 phòng. Tầng 1: 101–104 (Standard), tầng 2: 201–204 (Deluxe), tầng 3: 301–304 (Family). Tất cả `TRONG`, riêng 304 là `BAO_TRI`, 104 là `DANG_DON`.
- **DICH_VU:**

| Mã | Tên | Đơn giá | Cách tính | Đơn vị tính |
|---|---|---|---|---|
| DV01 | Ăn sáng buffet | 150.000 | THEO_DEM | người/đêm |
| DV02 | Giường phụ | 200.000 | THEO_DEM | giường/đêm |
| DV03 | Đưa đón sân bay Liên Khương | 300.000 | MOT_LAN | lượt |
| DV04 | Giặt ủi | 80.000 | MOT_LAN | lần |

- **Đơn mẫu** để demo đủ chức năng:
  1. Đơn `HOAN_TAT` (phòng 101, 2 đêm, có ăn sáng 2 người), hóa đơn `DA_HOAN_TAT`, chưa đánh giá.
  2. Đơn `DA_XAC_NHAN`, ngày nhận = hôm nay, phòng 201, đã cọc 30% trực tuyến (để demo check-in).
  3. Đơn `DA_XAC_NHAN` đã check-in (phòng 202 đang `CO_KHACH`), nhận phòng hôm qua, trả phòng ngày mai, có dịch vụ đưa đón 1 lượt, đã cọc 30% (để demo check-out).
  4. Đơn `DA_HUY`.

---

## 5. Quy định nghiệp vụ (BẮT BUỘC kiểm tra)

| Mã | Nội dung |
|---|---|
| QĐ1 | Email khách hàng là duy nhất. Mật khẩu tối thiểu 6 ký tự, lưu dạng băm. |
| QĐ2 | Ngày nhận ≥ hôm nay; ngày trả > ngày nhận. Số khách mỗi phòng ≤ SucChua của loại phòng. |
| QĐ3 | Phòng được đặt khi **không ở trạng thái Bảo trì** và **không trùng lịch** với đơn khác đang `CHO_THANH_TOAN` hoặc `DA_XAC_NHAN`. Trùng lịch khi `a.NgayNhanPhong < b.NgayTraPhong AND a.NgayTraPhong > b.NgayNhanPhong`. |
| QĐ4 | Đơn mới ở `CHO_THANH_TOAN`, giữ phòng **THOI_GIAN_GIU_PHONG** phút tính từ NgayDat. Quá hạn mà chưa thanh toán thành công thì tự chuyển `DA_HUY`. |
| QĐ5 | TongTien = tiền phòng + tiền dịch vụ. Tiền phòng = Σ (GiaThoiDiemDat × số đêm). Dịch vụ `THEO_DEM` = DonGiaThoiDiemDat × SoLuong × số đêm; dịch vụ `MOT_LAN` = DonGiaThoiDiemDat × SoLuong. Số đêm = NgayTraPhong − NgayNhanPhong. Giá được chốt lúc đặt. **Chỉ được tính bằng mẫu Decorator (Mục 6.2).** |
| QĐ6 | Thanh toán trực tuyến tối thiểu **TI_LE_DAT_COC**% TongTien, hoặc toàn bộ. Phần còn lại trả tại quầy khi check-out. |
| QĐ7 | Đơn `CHO_THANH_TOAN` hủy bất cứ lúc nào. Đơn `DA_XAC_NHAN` chỉ hủy khi hôm nay ≤ NgayNhanPhong − **THOI_HAN_HUY_DON** ngày. Không hoàn tiền trong hệ thống (chỉ hiển thị dòng "Khách sạn sẽ liên hệ với bạn về việc hoàn tiền"). |
| QĐ8 | Check-in chỉ cho đơn `DA_XAC_NHAN`, khi NgayNhanPhong ≤ hôm nay < NgayTraPhong, và mọi phòng trong đơn đang `TRONG`. |
| QĐ9 | Check-out chỉ cho đơn đã check-in (đơn `DA_XAC_NHAN` và các phòng đang `CO_KHACH`). Phải thu đủ số tiền còn lại bằng tiền mặt hoặc thẻ. |
| QĐ10 | Chỉ đánh giá đơn `HOAN_TAT` của chính khách đó, mỗi đơn 1 lần, 1–5 sao. |
| QĐ11 | Mỗi nhân viên có đúng 1 vai trò, chỉ dùng chức năng của vai trò đó (Mục 8). |
| QĐ12 | Dịch vụ thêm chỉ chọn lúc đặt phòng, mỗi dịch vụ tối đa 1 dòng trong đơn, số lượng từ 1 đến 10. |

Các tham số QĐ4, QĐ6, QĐ7 **phải đọc từ bảng THAM_SO**, không viết cứng.

### 5.1. Chuyển trạng thái hợp lệ

- **DAT_PHONG:** `CHO_THANH_TOAN → DA_XAC_NHAN` (thanh toán thành công) · `CHO_THANH_TOAN → DA_HUY` (khách hủy / quá hạn) · `DA_XAC_NHAN → DA_HUY` (khách hủy theo QĐ7) · `DA_XAC_NHAN → HOAN_TAT` (check-out).
- **PHONG** (cài bằng mẫu State, Mục 6.3):

| Trạng thái hiện tại | Thao tác hợp lệ | Trạng thái mới | Người thực hiện |
|---|---|---|---|
| Trống | Check-in | Đang có khách | Lễ tân |
| Trống | Bảo trì | Bảo trì | Buồng phòng |
| Đang có khách | Check-out | Đang dọn | Lễ tân |
| Đang dọn | Dọn xong | Trống | Buồng phòng |
| Đang dọn | Bảo trì | Bảo trì | Buồng phòng |
| Bảo trì | Kết thúc bảo trì | Trống | Buồng phòng |

  Mọi thao tác khác đều bị từ chối kèm thông báo lỗi. **Đặt phòng không làm đổi trạng thái phòng.**
- **HOA_DON:** tính lại sau mỗi giao dịch. Đã trả = Σ SoTien của THANH_TOAN `THANH_CONG`. Bằng 0 → `CHUA_THANH_TOAN`; 0 < đã trả < TongTien → `DA_COC`; ≥ TongTien → `DA_HOAN_TAT`.

---

## 6. Mẫu thiết kế áp dụng (BẮT BUỘC – phần quan trọng nhất)

| Mẫu | Nhóm | Áp dụng cho | Package |
|---|---|---|---|
| **Builder** | Creational (khởi tạo) | Tạo đơn đặt phòng gồm nhiều phòng và dịch vụ | `pattern.builder` |
| **Decorator** | Structural (cấu trúc) | Cộng tiền dịch vụ thêm vào tiền phòng | `pattern.decorator` |
| **State** | Behavioral (hành vi) | Trạng thái phòng | `pattern.state` |
| **Strategy** | Behavioral (hành vi) | Phương thức thanh toán | `pattern.strategy` |

Quy ước chung cho cả 4 mẫu:
- Mỗi lớp trong package `pattern` có Javadoc 1–2 dòng tiếng Việt ghi rõ **vai trò của lớp trong mẫu**. VD: `/** Vai trò trong mẫu Decorator: ConcreteDecorator – dịch vụ tính tiền theo số đêm. */`
- Lớp trong `pattern` **không gọi Repository, không dùng annotation JPA**, trừ lớp Strategy được phép là `@Component` (Mục 6.4). Dữ liệu được truyền vào qua tham số.
- Lỗi nghiệp vụ ném `NghiepVuException` với thông báo tiếng Việt dễ hiểu, Controller bắt và hiển thị bằng alert.

### 6.1. Builder – tạo đơn đặt phòng

**Vấn đề:** một đơn đặt phòng gồm khách hàng, khoảng ngày, nhiều phòng (mỗi phòng có số khách riêng) và nhiều dịch vụ. Nếu dùng constructor nhiều tham số hoặc gán từng setter rải rác thì dễ quên bước, dễ tạo ra đơn sai (thiếu phòng, giá chưa chốt, tổng tiền chưa tính).

**Các lớp:**

```java
/** Vai trò trong mẫu Builder: dữ liệu một phòng được chọn. */
public record PhongChon(Phong phong, int soKhach) {}

/** Vai trò trong mẫu Builder: dữ liệu một dịch vụ được chọn. */
public record DichVuChon(DichVu dichVu, int soLuong) {}

/** Vai trò trong mẫu Builder: Builder – xây dựng đơn đặt phòng từng bước. */
public class DatPhongBuilder {
    private KhachHang khachHang;
    private LocalDate ngayNhanPhong;
    private LocalDate ngayTraPhong;
    private final List<PhongChon> dsPhong = new ArrayList<>();
    private final List<DichVuChon> dsDichVu = new ArrayList<>();

    public DatPhongBuilder choKhachHang(KhachHang kh) { ... return this; }
    public DatPhongBuilder ngayNhanPhong(LocalDate ngay) { ... return this; }
    public DatPhongBuilder ngayTraPhong(LocalDate ngay) { ... return this; }
    public DatPhongBuilder themPhong(Phong phong, int soKhach) { ... return this; }
    public DatPhongBuilder themDichVu(DichVu dichVu, int soLuong) { ... return this; }

    /** Kiểm tra dữ liệu, tạo đơn kèm chi tiết, chốt giá và tính tổng tiền. */
    public DatPhong build() { ... }
}
```

**`build()` phải làm đúng thứ tự:**
1. Kiểm tra: đã có khách hàng; ngày hợp lệ theo QĐ2; có ít nhất 1 phòng; không chọn trùng phòng; số khách mỗi phòng từ 1 đến SucChua; phòng không ở trạng thái Bảo trì (gọi `phong.layTrangThai().coTheNhanDat()` của mẫu State); dịch vụ theo QĐ12. Sai thì ném `NghiepVuException`.
2. Tạo `DatPhong` trạng thái `CHO_THANH_TOAN`, `ngayDat = now`.
3. Tạo `ChiTietDatPhong` cho từng phòng, chốt `giaThoiDiemDat = loaiPhong.giaMoiDem`.
4. Tạo `ChiTietDichVu` cho từng dịch vụ, chốt `donGiaThoiDiemDat = dichVu.donGia`.
5. Tính `tongTien` bằng `GhepChiPhi.tuDonDatPhong(don).tinhTien()` (mẫu Decorator, Mục 6.2).

**Không** làm trong Builder: sinh mã (Service gán bằng `MaSinhService` sau khi `build()`), kiểm tra trùng lịch QĐ3 (cần truy vấn CSDL nên làm ở Service), lưu CSDL.

**Nơi sử dụng:** `DatPhongService.taoDonTam(...)`, `DuLieuMauRunner` (tạo đơn mẫu). Không được tạo `DatPhong` mới bằng `new DatPhong()` ở bất kỳ nơi nào khác.

### 6.2. Decorator – tính tiền phòng và dịch vụ thêm

**Vấn đề:** khách chọn dịch vụ thêm theo tổ hợp bất kỳ (ăn sáng, giường phụ, đưa đón, giặt ủi…). Nếu tạo lớp con cho từng tổ hợp thì số lớp tăng rất nhanh; nếu dồn hết vào một hàm `if-else` thì mỗi lần thêm cách tính mới phải sửa hàm cũ. Decorator cho phép "bọc" từng dịch vụ lên tiền phòng lúc chạy.

**Các lớp:**

```java
/** Vai trò trong mẫu Decorator: một dòng chi phí để hiển thị trên hóa đơn. */
public record DongChiPhi(String noiDung, String cachTinh, BigDecimal thanhTien) {}

/** Vai trò trong mẫu Decorator: Component. */
public interface ChiPhiDatPhong {
    BigDecimal tinhTien();
    List<DongChiPhi> chiTiet();
}

/** Vai trò trong mẫu Decorator: ConcreteComponent – tiền phòng của đơn. */
public class TienPhong implements ChiPhiDatPhong {
    public TienPhong(List<ChiTietDatPhong> dsChiTiet, long soDem) { ... }
    // tinhTien() = Σ giaThoiDiemDat × soDem
    // chiTiet() = mỗi phòng 1 dòng, VD: "Phòng 201 (Deluxe)", "800.000 đ × 2 đêm", 1.600.000
}

/** Vai trò trong mẫu Decorator: Decorator trừu tượng. */
public abstract class DichVuDecorator implements ChiPhiDatPhong {
    protected final ChiPhiDatPhong biBoc;
    protected final String tenDichVu;
    protected final BigDecimal donGia;
    protected final int soLuong;

    protected DichVuDecorator(ChiPhiDatPhong biBoc, String tenDichVu, BigDecimal donGia, int soLuong) { ... }

    @Override
    public BigDecimal tinhTien() {
        return biBoc.tinhTien().add(tienDichVu());
    }

    @Override
    public List<DongChiPhi> chiTiet() {
        List<DongChiPhi> ds = new ArrayList<>(biBoc.chiTiet());
        ds.add(new DongChiPhi(tenDichVu, moTaCachTinh(), tienDichVu()));
        return ds;
    }

    protected abstract BigDecimal tienDichVu();
    protected abstract String moTaCachTinh();
}

/** Vai trò trong mẫu Decorator: ConcreteDecorator – dịch vụ tính theo đêm. */
public class DichVuTheoDem extends DichVuDecorator {
    // tienDichVu() = donGia × soLuong × soDem ; moTaCachTinh() VD "150.000 đ × 2 × 3 đêm"
}

/** Vai trò trong mẫu Decorator: ConcreteDecorator – dịch vụ tính một lần. */
public class DichVuMotLan extends DichVuDecorator {
    // tienDichVu() = donGia × soLuong ; moTaCachTinh() VD "300.000 đ × 1"
}

/** Ghép chuỗi chi phí từ một đơn đặt phòng: bắt đầu bằng TienPhong, bọc lần lượt từng dịch vụ. */
public final class GhepChiPhi {
    public static ChiPhiDatPhong tuDonDatPhong(DatPhong don) {
        long soDem = ChronoUnit.DAYS.between(don.getNgayNhanPhong(), don.getNgayTraPhong());
        ChiPhiDatPhong chiPhi = new TienPhong(don.getDsChiTietDatPhong(), soDem);
        for (ChiTietDichVu ct : don.getDsChiTietDichVu()) {
            // CachTinh = THEO_DEM → new DichVuTheoDem(chiPhi, ...); MOT_LAN → new DichVuMotLan(chiPhi, ...)
        }
        return chiPhi;
    }
}
```

**Nơi sử dụng (bắt buộc dùng Decorator, không tự cộng tiền ở chỗ khác):**
- `DatPhongBuilder.build()` để tính TongTien.
- Trang Xác nhận đặt phòng: phần **tạm tính** cập nhật khi khách tick dịch vụ (gọi API nhỏ `POST /dat-phong/tam-tinh` trả JSON gồm các dòng `chiTiet()` và `tinhTien()`, server dùng `DatPhongBuilder` + `GhepChiPhi` nhưng **không lưu**).
- Trang chi tiết đơn của khách, trang check-out, trang hóa đơn: bảng chi phí lấy từ `chiTiet()`.

### 6.3. State – trạng thái phòng

**Vấn đề:** cùng một thao tác (check-in, check-out, dọn xong, bảo trì) nhưng mỗi trạng thái phòng cho phép hoặc từ chối khác nhau. Viết bằng `if (trangThai.equals(...))` rải rác ở nhiều Service sẽ rối và dễ sót trường hợp.

**Các lớp:**

```java
/** Các thao tác có thể thực hiện trên phòng. */
public enum ThaoTacPhong { CHECK_IN, CHECK_OUT, DON_XONG, BAO_TRI, KET_THUC_BAO_TRI }

/** Vai trò trong mẫu State: State. */
public interface TrangThaiPhong {
    String getMa();               // TRONG, CO_KHACH, DANG_DON, BAO_TRI (giá trị lưu CSDL)
    String getTenHienThi();       // Trống, Đang có khách, Đang dọn, Bảo trì
    boolean coTheNhanDat();       // false với Bảo trì (dùng cho QĐ3)
    Set<ThaoTacPhong> thaoTacHopLe();

    void checkIn(Phong phong);
    void checkOut(Phong phong);
    void donXong(Phong phong);
    void baoTri(Phong phong);
    void ketThucBaoTri(Phong phong);

    /** Đổi mã lưu trong CSDL thành đối tượng trạng thái tương ứng. */
    static TrangThaiPhong tuMa(String ma) { ... }
}

/** Mặc định mọi thao tác đều bị từ chối; lớp con chỉ ghi đè thao tác hợp lệ. */
public abstract class TrangThaiPhongCoBan implements TrangThaiPhong {
    // VD lỗi: "Phòng 104 đang ở trạng thái Đang dọn, không thể check-in."
}

/** Vai trò trong mẫu State: ConcreteState. */
public class PhongTrong extends TrangThaiPhongCoBan { ... }    // checkIn → PhongCoKhach; baoTri → PhongBaoTri
public class PhongCoKhach extends TrangThaiPhongCoBan { ... }  // checkOut → PhongDangDon
public class PhongDangDon extends TrangThaiPhongCoBan { ... }  // donXong → PhongTrong; baoTri → PhongBaoTri
public class PhongBaoTri extends TrangThaiPhongCoBan { ... }   // ketThucBaoTri → PhongTrong; coTheNhanDat() = false
```

**Entity `Phong` đóng vai trò Context:**

```java
@Entity @Table(name = "PHONG")
public class Phong {
    @Column(name = "TrangThai") private String trangThai;   // KHÔNG có setter public cho field này

    public TrangThaiPhong layTrangThai() { return TrangThaiPhong.tuMa(trangThai); }
    public void chuyenSang(TrangThaiPhong moi) { this.trangThai = moi.getMa(); } // chỉ lớp State gọi

    public void checkIn()       { layTrangThai().checkIn(this); }
    public void checkOut()      { layTrangThai().checkOut(this); }
    public void donXong()       { layTrangThai().donXong(this); }
    public void baoTri()        { layTrangThai().baoTri(this); }
    public void ketThucBaoTri() { layTrangThai().ketThucBaoTri(this); }
}
```

Phòng mới tạo ở chức năng quản lý phòng mặc định là `PhongTrong`. Quản lý không được sửa trạng thái phòng trực tiếp trong form sửa phòng.

**Nơi sử dụng:** check-in, check-out (`NhanTraPhongService`), trang tình trạng phòng của buồng phòng (`PhongService`), kiểm tra Bảo trì khi đặt phòng và tìm phòng (QĐ3). Trên trang tình trạng phòng, **chỉ hiện các nút thao tác có trong `thaoTacHopLe()`** của từng phòng (buồng phòng chỉ thấy Dọn xong / Bảo trì / Kết thúc bảo trì). Server vẫn phải gọi qua State để chặn khi người dùng gửi request trực tiếp.

### 6.4. Strategy – phương thức thanh toán

**Vấn đề:** mỗi phương thức thanh toán xử lý khác nhau: trực tuyến phải chuyển sang cổng thanh toán và chờ kết quả; tiền mặt phải nhận tiền khách đưa và tính tiền thối; thẻ phải nhập mã chuẩn chi. Nếu viết bằng `if-else` theo phương thức thì mỗi lần thêm phương thức mới phải sửa Service.

**Các lớp:**

```java
/** Vai trò trong mẫu Strategy: dữ liệu nhập thêm tùy phương thức. */
public record DuLieuThanhToan(BigDecimal tienKhachDua, String maChuanChi) {}

/** Vai trò trong mẫu Strategy: kết quả xử lý. */
public record KetQuaXuLy(TrangThaiGiaoDich trangThai, String thongBao, String urlChuyenHuong) {}

/** Vai trò trong mẫu Strategy: Strategy. */
public interface PhuongThucThanhToan {
    String getMa();                // TRUC_TUYEN, TIEN_MAT, THE (giá trị lưu cột PhuongThuc)
    String getTenHienThi();
    boolean dungTaiQuay();         // true: lễ tân dùng khi check-out; false: khách dùng trên website
    KetQuaXuLy xuLy(ThanhToan giaoDich, DuLieuThanhToan duLieu);
}
```

| ConcreteStrategy (`@Component`) | `dungTaiQuay()` | Cách xử lý trong `xuLy()` |
|---|---|---|
| `ThanhToanTrucTuyen` | false | Trả về `DANG_XU_LY` và `urlChuyenHuong = /thanh-toan/gia-lap/{maThanhToan}`. Kết quả cuối cùng do cổng giả lập báo về. |
| `ThanhToanTienMat` | true | Bắt buộc `tienKhachDua ≥ SoTien`, nếu thiếu báo lỗi. Trả về `THANH_CONG`, thông báo "Tiền thối lại: … đ", ghi GhiChu "Khách đưa … đ, thối … đ". |
| `ThanhToanThe` | true | Bắt buộc `maChuanChi` gồm đúng 6 chữ số (giả lập máy POS). Trả về `THANH_CONG`, ghi GhiChu "Mã chuẩn chi: …". |

**Context = `ThanhToanService`:**

```java
@Service
public class ThanhToanService {
    private final Map<String, PhuongThucThanhToan> dsPhuongThuc;

    public ThanhToanService(List<PhuongThucThanhToan> ds, ...) {
        this.dsPhuongThuc = ds.stream().collect(Collectors.toMap(PhuongThucThanhToan::getMa, p -> p));
    }

    public List<PhuongThucThanhToan> phuongThucTrucTuyen() { ... }  // lọc dungTaiQuay() == false
    public List<PhuongThucThanhToan> phuongThucTaiQuay()   { ... }  // lọc dungTaiQuay() == true

    // Chọn strategy theo mã người dùng chọn trên form, gọi xuLy(), lưu kết quả
    public KetQuaXuLy thanhToan(String maHoaDon, BigDecimal soTien, String maPhuongThuc, DuLieuThanhToan duLieu) { ... }
}
```

- **Không có** `if/switch` theo mã phương thức trong Service hay Controller. Thêm phương thức mới chỉ cần thêm 1 lớp `@Component` cài `PhuongThucThanhToan`.
- Danh sách phương thức trên giao diện lấy từ `phuongThucTrucTuyen()` / `phuongThucTaiQuay()`, không viết cứng trong HTML. Ô "Tiền khách đưa" chỉ hiện khi chọn Tiền mặt, ô "Mã chuẩn chi" chỉ hiện khi chọn Thẻ.

**Nơi sử dụng:** trang Thanh toán của khách (trực tuyến), trang Check-out của lễ tân (tiền mặt, thẻ).

### 6.5. Tài liệu đi kèm cho phần mẫu thiết kế

Agent phải tạo thêm:

1. **`GHI_CHU_MAU_THIET_KE.md`**, mỗi mẫu một mục gồm:
   - Vấn đề trong hệ thống mà mẫu giải quyết (2–3 câu).
   - Bảng các lớp: tên lớp, vai trò trong mẫu, đường dẫn file.
   - Nơi sử dụng: `TenLop.tenPhuongThuc()` cụ thể.
   - Cách demo trên giao diện: từng bước bấm gì, thấy gì.
   - Tên các test liên quan.
2. **`docs/uml/`**: mỗi mẫu 1 file PlantUML sơ đồ lớp (`builder.puml`, `decorator.puml`, `state.puml`, `strategy.puml`) đúng tên lớp, thuộc tính, phương thức chính như code thật. Thêm 1 file `state_phong.puml` là sơ đồ trạng thái của phòng.

### 6.6. Kiểm thử các lớp mẫu (JUnit 5)

Đặt trong `src/test/java/.../pattern/`, không cần CSDL:
- **Builder:** thiếu phòng → lỗi; ngày trả ≤ ngày nhận → lỗi; số khách vượt sức chứa → lỗi; phòng Bảo trì → lỗi; đơn hợp lệ có giá được chốt và TongTien đúng.
- **Decorator:** phòng Deluxe 2 đêm + ăn sáng 2 người + đưa đón 1 lượt = 1.600.000 + 600.000 + 300.000 = **2.500.000**; `chiTiet()` có đủ 3 dòng đúng thứ tự.
- **State:** đi đủ vòng Trống → Đang có khách → Đang dọn → Trống; check-in khi Đang dọn → lỗi; `coTheNhanDat()` của Bảo trì = false.
- **Strategy:** tiền mặt thiếu tiền → lỗi; tiền mặt đủ → thông báo tiền thối đúng; thẻ mã chuẩn chi sai định dạng → lỗi; trực tuyến trả về `DANG_XU_LY` và có URL chuyển hướng.

---

## 7. Chức năng chi tiết

### CN01 – Đăng ký / Đăng nhập
- Khách: `/dang-ky`, `/dang-nhap`, `/dang-xuat`, `/tai-khoan/doi-mat-khau`.
- Nhân viên: `/admin/dang-nhap`, `/admin/dang-xuat` (xác thực từ bảng NHAN_VIEN).
- Chi tiết cấu hình Spring Security ở Mục 8.

### CN02 – Tìm kiếm phòng trống (không cần đăng nhập)
- Form: ngày nhận, ngày trả, số khách, loại phòng (tùy chọn).
- Kiểm tra QĐ2, lọc theo QĐ3 (dùng `coTheNhanDat()` của State cho điều kiện Bảo trì), SucChua ≥ số khách.
- Kết quả gom theo loại phòng: tên loại, giá/đêm, sức chứa, mô tả, số phòng còn trống, danh sách phòng để chọn (checkbox), giá dự kiến cho cả khoảng ngày.
- Lễ tân và quản lý dùng cùng chức năng ở `/admin/tim-phong` (chỉ xem).

### CN03 – Đặt phòng trực tuyến (bắt buộc đăng nhập)
1. Khách chọn 1 hoặc nhiều phòng → trang **Xác nhận đặt phòng**: thông tin khách, ngày, danh sách phòng (chọn số khách từng phòng), **danh sách dịch vụ thêm** (checkbox + số lượng), **bảng tạm tính** từ Decorator (cập nhật ngay khi đổi lựa chọn), checkbox đồng ý quy định.
2. Bấm "Xác nhận đặt phòng" → `DatPhongService.taoDonTam(...)` trong `@Transactional`:
   - Khóa các dòng PHONG được chọn bằng `@Lock(LockModeType.PESSIMISTIC_WRITE)` (lấy theo thứ tự MaPhong tăng dần để tránh deadlock), rồi **kiểm tra lại QĐ3**.
   - Phòng vừa bị đặt → báo "Phòng … không còn trống", quay lại kết quả tìm kiếm.
   - Tạo đơn bằng `DatPhongBuilder` (Mục 6.1), gán mã bằng `MaSinhService`, lưu.
3. Chuyển sang trang **Thanh toán** có đồng hồ đếm ngược thời gian giữ phòng.

### CN04 – Thanh toán trực tuyến
1. Khách chọn **đặt cọc** (nhập số tiền ≥ TI_LE_DAT_COC% TongTien và ≤ số còn lại) hoặc **thanh toán toàn bộ**, chọn phương thức (danh sách từ `phuongThucTrucTuyen()`).
2. `ThanhToanService`:
   - Kiểm tra đơn còn `CHO_THANH_TOAN` và chưa quá hạn.
   - Đơn **chưa có HOA_DON** → tạo HOA_DON (`CHUA_THANH_TOAN`, TongTien = DAT_PHONG.TongTien).
   - Tạo THANH_TOAN, gọi strategy `ThanhToanTrucTuyen` → chuyển hướng sang trang cổng giả lập.
3. **Trang cổng giả lập** `/thanh-toan/gia-lap/{maThanhToan}`: giao diện giống trang cổng thanh toán (logo chữ "Cổng thanh toán giả lập", không dùng logo ngân hàng thật), hiện số tiền, mã giao dịch, 2 nút **"Thanh toán thành công"** và **"Thanh toán thất bại"**.
4. Xử lý kết quả `ThanhToanService.xacNhanTrucTuyen(maThanhToan, thanhCong)`:
   - Thành công → THANH_TOAN `THANH_CONG`, cập nhật HOA_DON (Mục 5.1), DAT_PHONG `DA_XAC_NHAN`, ghi email xác nhận (mã đặt phòng, ngày, phòng, bảng chi phí từ Decorator, số đã trả) ra `email-logs/`.
   - Thất bại → THANH_TOAN `THAT_BAI`, đơn giữ `CHO_THANH_TOAN`, cho thanh toán lại nếu còn hạn (lần sau **dùng lại HOA_DON cũ**, tạo THANH_TOAN mới).
   - Idempotent: giao dịch đã `THANH_CONG`/`THAT_BAI` thì bỏ qua lần gọi lặp.
5. Trang **Kết quả thanh toán** hiển thị thành công / thất bại và nút đi tiếp.

### Tác vụ nền – Hủy đơn quá hạn
- `HuyDonQuaHanJob` dùng `@Scheduled(fixedRate = 60000)` (bật `@EnableScheduling`), gọi `DatPhongService.huyDonQuaHan()`: đơn `CHO_THANH_TOAN` có NgayDat + THOI_GIAN_GIU_PHONG < hiện tại → `DA_HUY`. Giao dịch `DANG_XU_LY` của đơn đó chuyển `THAT_BAI`.

### CN05 – Hủy đặt phòng
- Ở trang chi tiết đơn, nút "Hủy đặt phòng" chỉ hiện khi thỏa QĐ7, có hộp thoại xác nhận. Chuyển `DA_HUY`, **không xóa dữ liệu**.

### CN06 – Xem lịch sử đặt phòng
- Danh sách đơn của khách: mã, ngày đặt, ngày nhận – trả, số phòng, tổng tiền, trạng thái. Lọc theo trạng thái.
- Trang chi tiết đơn: danh sách phòng, **bảng chi phí từ `chiTiet()` của Decorator**, hóa đơn, các giao dịch, nút Thanh toán tiếp (nếu còn hạn), Hủy (nếu được), Đánh giá (nếu được).

### CN07 – Đánh giá dịch vụ
- Form chọn 1–5 sao + nội dung. Kiểm tra QĐ10. Trang chủ hiển thị 6 đánh giá mới nhất.

### CN08 – Xác nhận đặt phòng (Lễ tân)
- Danh sách đơn `DA_XAC_NHAN` có ngày nhận trong 7 ngày tới và **chưa có MaNV**. Nút "Nhận phụ trách" → gán MaNV = nhân viên đang đăng nhập.

### CN09 – Check-in (Lễ tân)
- Nhập mã đặt phòng → hiển thị thông tin đơn, phòng (kèm trạng thái hiện tại), số tiền đã trả.
- Nút "Check-in" → kiểm tra điều kiện đơn và ngày theo QĐ8 trong `NhanTraPhongService`, sau đó gọi `phong.checkIn()` (State) cho **từng phòng** trong cùng 1 transaction. Phòng nào không ở trạng thái Trống thì State ném lỗi, hiển thị đúng thông báo đó (VD "Phòng 104 đang ở trạng thái Đang dọn, không thể check-in.") và không phòng nào bị đổi.

### CN10 – Check-out (Lễ tân)
- Nhập mã đặt phòng hoặc số phòng → hiển thị chi tiết đơn, **bảng chi phí từ Decorator**, tổng tiền, đã trả, **còn lại**.
- Còn lại > 0: form thu tiền tại quầy, chọn phương thức từ `phuongThucTaiQuay()` (Tiền mặt / Thẻ), số tiền = số còn lại (không cho sửa). Gọi `ThanhToanService.thanhToan(...)` (Strategy). Hiển thị thông báo từ `KetQuaXuLy` (VD tiền thối).
- Đã thu đủ: nút "Hoàn tất trả phòng" → HOA_DON `DA_HOAN_TAT`, DAT_PHONG `HOAN_TAT`, gọi `phong.checkOut()` (State) cho từng phòng → chuyển tới trang in hóa đơn.

### CN11 – Lập hóa đơn (Lễ tân)
- Trang hóa đơn in được (`window.print()`, có CSS riêng cho bản in): tên khách sạn, mã hóa đơn, ngày lập, thông tin khách, mã đặt phòng, ngày nhận – trả, **bảng chi phí từ `chiTiet()` của Decorator** (nội dung, cách tính, thành tiền), tổng tiền, các lần thanh toán (phương thức hiển thị bằng `getTenHienThi()`), số còn lại, trạng thái. Không ghi thuế VAT, không ghi mã số thuế.

### CN12 – Cập nhật tình trạng phòng (Buồng phòng)
- Lưới phòng theo tầng, mỗi ô hiển thị số phòng, loại phòng, trạng thái bằng **chữ và biểu tượng** (không chỉ dựa vào màu). Mục "Phòng cần dọn" ở đầu trang.
- Nút thao tác hiện theo `thaoTacHopLe()` (Mục 6.3), chỉ gồm Dọn xong / Bảo trì / Kết thúc bảo trì. Buồng phòng không có nút check-in / check-out.

### CN13 – Quản lý loại phòng và phòng (Quản lý)
- CRUD LOAI_PHONG và PHONG. Không cho xóa loại phòng đang có phòng; không cho xóa phòng đã xuất hiện trong CHI_TIET_DAT_PHONG (thông báo dùng chức năng Bảo trì thay thế).
- Form sửa phòng không có ô trạng thái (trạng thái chỉ đổi qua State).
- Đổi giá không ảnh hưởng đơn cũ (giá đã chốt theo QĐ5).

### CN14 – Quản lý nhân viên (Quản lý)
- CRUD nhân viên, chọn vai trò, đặt lại mật khẩu. Không cho quản lý tự xóa chính mình. Không xóa nhân viên đã phụ trách đơn.

### CN15 – Báo cáo doanh thu (Quản lý)
- Chọn từ ngày – đến ngày. Hiển thị:
  - Doanh thu = Σ SoTien của THANH_TOAN `THANH_CONG` theo NgayThanhToan, theo từng ngày (bảng + biểu đồ cột Chart.js).
  - Doanh thu theo phương thức thanh toán (bảng nhỏ).
  - Số đơn theo từng trạng thái.
  - Công suất phòng (%) = số đêm-phòng đã bán (đơn `DA_XAC_NHAN`/`HOAN_TAT`) / (số phòng không Bảo trì × số ngày).

### CN16 – Quản lý đặt phòng (Quản lý)
- Danh sách toàn bộ đơn, lọc theo trạng thái, khoảng ngày, mã đơn, email khách; xem chi tiết (chỉ xem).

### CN17 – Cấu hình tham số (Quản lý)
- Trang sửa GiaTri của 3 tham số trong THAM_SO (số nguyên dương; tỉ lệ cọc từ 1 đến 100). Có hiệu lực ngay, không cần khởi động lại.

---

## 8. Phân quyền

| Chức năng | Khách hàng | Lễ tân (VT01) | Quản lý (VT02) | Buồng phòng (VT03) |
|---|:-:|:-:|:-:|:-:|
| CN01 Đăng nhập | ✔ | ✔ | ✔ | ✔ |
| CN02 Tìm phòng trống | ✔ (cả khi chưa đăng nhập) | ✔ | ✔ | |
| CN03 – CN07 | ✔ | | | |
| CN08 – CN11 | | ✔ | | |
| CN12 Tình trạng phòng | | | | ✔ |
| CN13 – CN17 | | | ✔ | |

**Cấu hình Spring Security:**
- 2 `SecurityFilterChain`:
  - `@Order(1)`: `securityMatcher("/admin/**")`, trang đăng nhập `/admin/dang-nhap`, `UserDetailsService` đọc bảng NHAN_VIEN, quyền = `ROLE_` + MaVaiTro (VD `ROLE_VT01`).
  - `@Order(2)`: phần còn lại, trang đăng nhập `/dang-nhap`, `UserDetailsService` đọc bảng KHACH_HANG, quyền `ROLE_KHACH`.
- Đăng nhập bằng email + mật khẩu. `PasswordEncoder` = `BCryptPasswordEncoder`.
- Phân quyền theo URL bằng `requestMatchers(...).hasRole(...)` đúng bảng trên. Gõ URL trực tiếp không đúng vai trò → trang 403 tiếng Việt.
- Menu quản trị chỉ hiện chức năng của vai trò đang đăng nhập.
- Khách chỉ xem / hủy / thanh toán / đánh giá **đơn của chính mình** (kiểm tra trong Service).
- Giữ CSRF mặc định (Thymeleaf tự chèn token vào form; request `fetch` của trang tạm tính phải gửi kèm token).

---

## 9. Danh sách màn hình

| # | Màn hình | Route | Vai trò |
|---|---|---|---|
| 1 | Trang chủ (form tìm phòng, giới thiệu loại phòng, dịch vụ, đánh giá) | `/` | Tất cả |
| 2 | Kết quả tìm phòng | `/dat-phong/tim-kiem` | Tất cả |
| 3 | Đăng ký | `/dang-ky` | Khách |
| 4 | Đăng nhập khách hàng | `/dang-nhap` | Khách |
| 5 | Xác nhận đặt phòng (chọn dịch vụ, tạm tính) | `/dat-phong/xac-nhan` | Khách |
| 6 | Thanh toán (có đếm ngược) | `/thanh-toan/{maDatPhong}` | Khách |
| 7 | Cổng thanh toán giả lập | `/thanh-toan/gia-lap/{maThanhToan}` | Khách |
| 8 | Kết quả thanh toán | `/thanh-toan/ket-qua/{maThanhToan}` | Khách |
| 9 | Lịch sử đặt phòng | `/dat-phong/lich-su` | Khách |
| 10 | Chi tiết đơn | `/dat-phong/{maDatPhong}` | Khách |
| 11 | Đánh giá | `/danh-gia/{maDatPhong}` | Khách |
| 12 | Đổi mật khẩu | `/tai-khoan/doi-mat-khau` | Khách |
| 13 | Đăng nhập nhân viên | `/admin/dang-nhap` | Nhân viên |
| 14 | Trang chủ quản trị (thẻ tóm tắt theo vai trò) | `/admin` | Nhân viên |
| 15 | Tìm phòng trống | `/admin/tim-phong` | Lễ tân, Quản lý |
| 16 | Đơn cần xác nhận | `/admin/dat-phong/can-xac-nhan` | Lễ tân |
| 17 | Check-in | `/admin/nhan-tra-phong/check-in` | Lễ tân |
| 18 | Check-out | `/admin/nhan-tra-phong/check-out` | Lễ tân |
| 19 | Hóa đơn (in được) | `/admin/hoa-don/{maHoaDon}` | Lễ tân |
| 20 | Tình trạng phòng | `/admin/phong/tinh-trang` | Buồng phòng |
| 21 | Quản lý loại phòng & phòng | `/admin/phong` | Quản lý |
| 22 | Quản lý nhân viên | `/admin/nhan-vien` | Quản lý |
| 23 | Báo cáo doanh thu | `/admin/bao-cao` | Quản lý |
| 24 | Quản lý đặt phòng | `/admin/dat-phong` | Quản lý |
| 25 | Cấu hình tham số | `/admin/tham-so` | Quản lý |

**Yêu cầu giao diện:**
- Bootstrap 5, gọn gàng, một phong cách thống nhất. Trang khách hàng responsive (dùng tốt trên điện thoại). Trang quản trị có sidebar.
- Dùng layout chung (Thymeleaf fragment) cho trang khách và trang quản trị.
- Định dạng tiền `1.250.000 đ`, ngày `dd/MM/yyyy`, múi giờ `Asia/Ho_Chi_Minh`.
- Validate phía client (HTML5 + JS) **và** phía server (Bean Validation `@Valid` trong DTO).
- Thông báo lỗi / thành công bằng alert Bootstrap, câu chữ tiếng Việt rõ ràng.
- Không dùng ảnh có bản quyền; ảnh phòng dùng khối màu kèm tên loại phòng hoặc ảnh tự tạo bằng CSS/SVG.

---

## 10. Cấu hình (`application.properties`)

```properties
spring.application.name=khachsan
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/quan_ly_khach_san?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Ho_Chi_Minh
spring.datasource.username=root
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false
spring.flyway.enabled=true

spring.thymeleaf.cache=false
spring.jackson.time-zone=Asia/Ho_Chi_Minh

app.email-log-dir=email-logs
```

---

## 11. Yêu cầu phi chức năng

- Mọi thao tác ghi nhiều bảng (tạo đơn, xử lý kết quả thanh toán, check-in, check-out) chạy trong `@Transactional`.
- Tiền dùng `BigDecimal`, không dùng `double`.
- Không xóa vật lý DAT_PHONG, HOA_DON, THANH_TOAN.
- Không lưu thông tin thẻ (chỉ lưu mã chuẩn chi giả lập trong GhiChu).
- Không để lộ lỗi hệ thống ra giao diện; có trang lỗi 403, 404, 500 tiếng Việt.
- Code có comment tiếng Việt ở những chỗ nghiệp vụ quan trọng, ngắn gọn.

---

## 12. Không được làm

- Không đổi tên lớp ở Mục 6 sang tiếng Anh, không gộp các lớp lại.
- Không cài thêm mẫu thiết kế khác vào package `pattern`.
- Không tính tiền ngoài Decorator, không đổi trạng thái phòng ngoài State, không rẽ nhánh theo phương thức thanh toán ngoài Strategy, không tạo đơn đặt phòng ngoài Builder.

---

## 13. Ngoài phạm vi (KHÔNG làm)

Tích hợp cổng thanh toán thật, gửi email thật, hoàn tiền trực tuyến, khuyến mãi / mã giảm giá, thuế VAT, trang quản lý danh mục dịch vụ (dịch vụ chỉ lấy từ dữ liệu mẫu), thêm dịch vụ sau khi đã đặt, nhiều chi nhánh, đăng nhập bằng Google/Facebook, ứng dụng di động, đa ngôn ngữ, chat.

---

## 14. Kế hoạch thực hiện (làm theo thứ tự)

1. Tạo project Spring Boot (Maven) với các dependency: Web, Thymeleaf, Data JPA, Security, Validation, MySQL Driver, Flyway (`flyway-core` + `flyway-mysql`), DevTools, Test. Tải Bootstrap 5, Bootstrap Icons, Chart.js vào `static/vendor/`.
2. Viết `V1__tao_bang.sql` đủ 13 bảng, ràng buộc như Mục 4 → tạo entity, repository → chạy thử để `ddl-auto=validate` không báo lỗi.
3. Cài **4 mẫu thiết kế** trong package `pattern` (Mục 6.1 – 6.4) và **test JUnit** (Mục 6.6). Chạy `./mvnw test` phải pass hết.
4. `MaSinhService`, `ThamSoService`, `DuLieuMauRunner` (Mục 4.5, đơn mẫu tạo bằng Builder).
5. Spring Security 2 phân hệ + phân quyền (Mục 8) + layout khách và layout quản trị.
6. CN02 → CN03 (có tạm tính) → CN04 (cổng giả lập) → tác vụ nền hủy đơn quá hạn.
7. CN06, CN05, CN07.
8. CN09, CN10, CN11, CN08.
9. CN12, CN13, CN14, CN16, CN17, CN15.
10. Viết `GHI_CHU_MAU_THIET_KE.md` và các file `docs/uml/*.puml` (Mục 6.5), đối chiếu lại với code thật.
11. Viết `README.md`: yêu cầu cài đặt (JDK, MySQL), cách tạo CSDL và sửa mật khẩu MySQL, cách chạy (`mvnw spring-boot:run` trên Windows là `mvnw.cmd spring-boot:run`), tài khoản demo, các giả định đã chọn.
12. Chạy lại toàn bộ checklist Mục 15.

---

## 15. Kiểm tra nghiệm thu

**Dữ liệu**
- [ ] CSDL có đúng 13 bảng, đúng tên bảng, tên cột, kiểu, ràng buộc (kể cả CHECK, UNIQUE, DEFAULT).
- [ ] Dữ liệu mẫu đủ như Mục 4.5, đăng nhập được 4 tài khoản demo.

**Nghiệp vụ**
- [ ] Tìm phòng không trả về phòng Bảo trì và phòng trùng lịch với đơn Chờ thanh toán / Đã xác nhận.
- [ ] Hai tab cùng đặt một phòng cùng khoảng ngày: chỉ 1 đơn được tạo.
- [ ] Đặt phòng không làm đổi trạng thái phòng.
- [ ] Không thanh toán trong thời gian giữ phòng → đơn tự chuyển Đã hủy.
- [ ] Thanh toán thất bại rồi thanh toán lại: vẫn 1 HOA_DON, 2 THANH_TOAN (1 thất bại, 1 thành công).
- [ ] Đặt cọc dưới tỉ lệ bị từ chối; đặt cọc hợp lệ → hóa đơn Đã đặt cọc, đơn Đã xác nhận, có file email trong `email-logs/`.
- [ ] Hủy đơn đúng / sai thời hạn QĐ7.
- [ ] Chỉ đánh giá được đơn Hoàn tất, mỗi đơn 1 lần.
- [ ] Đổi tham số trong trang cấu hình có hiệu lực ngay.
- [ ] Mỗi vai trò chỉ thấy và truy cập được chức năng của mình (thử gõ URL trực tiếp).
- [ ] Báo cáo doanh thu chỉ tính giao dịch thành công.

**Mẫu thiết kế**
- [ ] `./mvnw test` pass toàn bộ test ở Mục 6.6.
- [ ] **Builder:** tìm trong code không còn chỗ nào tạo `DatPhong` mới ngoài `DatPhongBuilder`.
- [ ] **Decorator:** trang Xác nhận đặt phòng, tick / bỏ tick dịch vụ → bảng tạm tính thêm / bớt dòng và tổng tiền đổi đúng; hóa đơn hiển thị đúng các dòng đó.
- [ ] **State:** check-in đơn có phòng Đang dọn bị từ chối với đúng thông báo của State; check-in đúng → phòng Đang có khách; check-out → Đang dọn; buồng phòng bấm Dọn xong → Trống; trang tình trạng phòng chỉ hiện nút hợp lệ.
- [ ] **Strategy:** check-out bằng tiền mặt nhập thiếu tiền bị từ chối, nhập dư hiện đúng tiền thối; bằng thẻ nhập mã chuẩn chi sai bị từ chối; trong Service/Controller không có `if/switch` theo mã phương thức.
- [ ] `GHI_CHU_MAU_THIET_KE.md` và `docs/uml/*.puml` khớp với tên lớp, phương thức trong code.
