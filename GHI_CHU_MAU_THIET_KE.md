# Ghi chú bốn mẫu thiết kế

Các lớp mẫu nằm trong `src/main/java/vn/edu/dlu/khachsan/pattern/`. Chỉ có bốn mẫu được yêu cầu, gồm 23 lớp/interface/record/enum. Controller gọi Service; Service sử dụng các mẫu trước khi lưu dữ liệu. Sơ đồ PlantUML ở [docs/uml](docs/uml/).

## 1. Builder – tạo đơn đặt phòng

Một đơn có khách, khoảng ngày, nhiều phòng và nhiều dịch vụ với giá chốt riêng. `DatPhongBuilder` kiểm tra các thành phần này trước khi tạo đơn, tránh lưu một đơn thiếu hoặc sai dữ liệu. Mã định danh và kiểm tra trùng lịch có khóa thuộc Service, vì cần truy cập CSDL.

| Lớp | Vai trò | File |
|---|---|---|
| DatPhongBuilder | Builder: kiểm tra và tạo DatPhong, chi tiết phòng/dịch vụ, chốt giá; gọi Decorator để tính tổng | [DatPhongBuilder.java](src/main/java/vn/edu/dlu/khachsan/pattern/builder/DatPhongBuilder.java) |
| PhongChon | Record chứa Phong và soKhach | [PhongChon.java](src/main/java/vn/edu/dlu/khachsan/pattern/builder/PhongChon.java) |
| DichVuChon | Record chứa DichVu và soLuong | [DichVuChon.java](src/main/java/vn/edu/dlu/khachsan/pattern/builder/DichVuChon.java) |

**Nơi sử dụng:** `DatPhongService.builder()`, `tamTinh()`, `taoDonTam()`; `DuLieuMauRunner` dùng Builder cho cả bốn đơn mẫu. Chỉ `DatPhongBuilder.build()` gọi `new DatPhong()`. Constructor nhận `Clock` phục vụ kiểm thử và tạo đơn mẫu quá khứ; constructor mặc định dùng múi giờ Việt Nam.

**Demo:** đăng nhập khách → tìm ngày nhận/trả hợp lệ → chọn phòng → Xác nhận đặt phòng → chọn số khách và dịch vụ → đồng ý quy định → xác nhận. Đơn mới ở Chờ thanh toán; tình trạng hiện tại của phòng không đổi. Thử số khách vượt sức chứa sẽ bị từ chối; hai yêu cầu cùng phòng/cùng ngày chỉ một yêu cầu được tạo đơn.

**Kiểm thử:** `DatPhongBuilderTest` (thiếu phòng/khách/ngày, ngày sai, sức chứa, bảo trì, trùng phòng/dịch vụ, chốt giá); `Buoc6NghiepVuIT.datNhieuPhongDichVuChotGiaVaKhongDoiTrangThai()` và kiểm thử đồng thời trong cùng lớp. Sơ đồ: [builder.puml](docs/uml/builder.puml).

## 2. Decorator – bảng chi phí thống nhất

Tiền phòng có thể được bổ sung nhiều dịch vụ tính theo đêm hoặc một lần. Decorator bọc chi phí gốc bằng từng dịch vụ, tạo cả tổng tiền và các dòng giải thích từ cùng một nguồn. Các trang tạm tính, chi tiết, email và hóa đơn đều dùng giá chốt trong chi tiết đơn.

| Lớp | Vai trò | File |
|---|---|---|
| ChiPhiDatPhong | Component: tinhTien(), chiTiet() | [ChiPhiDatPhong.java](src/main/java/vn/edu/dlu/khachsan/pattern/decorator/ChiPhiDatPhong.java) |
| DongChiPhi | Record dòng nội dung, cách tính, thành tiền | [DongChiPhi.java](src/main/java/vn/edu/dlu/khachsan/pattern/decorator/DongChiPhi.java) |
| TienPhong | ConcreteComponent: mỗi phòng một dòng, giá chốt × số đêm | [TienPhong.java](src/main/java/vn/edu/dlu/khachsan/pattern/decorator/TienPhong.java) |
| DichVuDecorator | Decorator trừu tượng, giữ biBoc và nối dòng dịch vụ | [DichVuDecorator.java](src/main/java/vn/edu/dlu/khachsan/pattern/decorator/DichVuDecorator.java) |
| DichVuTheoDem | ConcreteDecorator: đơn giá chốt × số lượng × số đêm | [DichVuTheoDem.java](src/main/java/vn/edu/dlu/khachsan/pattern/decorator/DichVuTheoDem.java) |
| DichVuMotLan | ConcreteDecorator: đơn giá chốt × số lượng | [DichVuMotLan.java](src/main/java/vn/edu/dlu/khachsan/pattern/decorator/DichVuMotLan.java) |
| GhepChiPhi | Ghép chuỗi Decorator từ chi tiết đơn, giữ thứ tự dịch vụ | [GhepChiPhi.java](src/main/java/vn/edu/dlu/khachsan/pattern/decorator/GhepChiPhi.java) |

**Nơi sử dụng:** `DatPhongBuilder.build()`, `DatPhongService.chiTiet()`, `PhongService.timPhong()` (giá dự kiến qua `TienPhong`). `DatPhongController.tamTinh()` trả các dòng từ Decorator cho JavaScript; JavaScript chỉ định dạng/hiển thị. Fragment `layout/chi-phi.html` dùng `chiTiet()` ở xác nhận, chi tiết và hóa đơn; email nhận cùng đối tượng chi phí.

**Demo:** chọn một phòng Deluxe, hai đêm; chọn Ăn sáng buffet số lượng 2 và Đưa đón sân bay số lượng 1. Bảng có ba dòng theo thứ tự phòng → buffet → đưa đón, tổng **2.500.000 đ**. Bỏ chọn dịch vụ sẽ bỏ dòng và cập nhật tổng; đặt và thanh toán thành công thì email/chi tiết giữ các giá đã chốt. Quản lý đổi giá loại phòng không làm thay đổi đơn cũ.

**Kiểm thử:** `ChiPhiDatPhongTest.bocDichVuChoDeluxeHaiDemTinhDung2500000()`, `ghepTuDonGiuThuTuVaGiaDaChot()`, `Buoc6NghiepVuIT.tamTinhKhongLuuVaCoCsrf()`, `Buoc9QuanLyIT.doiGiaKhongDoiGiaDaChot()`. Sơ đồ: [decorator.puml](docs/uml/decorator.puml).

## 3. State – tình trạng hiện tại của phòng

Mỗi tình trạng chỉ cho phép một số thao tác; ví dụ Đang dọn không được check-in. State đặt quy tắc chuyển và thông báo từ chối trong từng lớp trạng thái, để Service không tự gán mã tình trạng. Lịch đặt theo khoảng ngày được kiểm tra riêng, không đồng nhất với tình trạng phòng hiện tại.

| Lớp | Vai trò | File |
|---|---|---|
| TrangThaiPhong | State: hành vi, tên, mã, khả năng nhận đặt và thao tác hợp lệ; tuMa() tra Map | [TrangThaiPhong.java](src/main/java/vn/edu/dlu/khachsan/pattern/state/TrangThaiPhong.java) |
| ThaoTacPhong | Enum mô tả năm thao tác | [ThaoTacPhong.java](src/main/java/vn/edu/dlu/khachsan/pattern/state/ThaoTacPhong.java) |
| TrangThaiPhongCoBan | Từ chối mặc định, sinh đúng thông báo tiếng Việt | [TrangThaiPhongCoBan.java](src/main/java/vn/edu/dlu/khachsan/pattern/state/TrangThaiPhongCoBan.java) |
| PhongTrong | Cho check-in, bảo trì | [PhongTrong.java](src/main/java/vn/edu/dlu/khachsan/pattern/state/PhongTrong.java) |
| PhongCoKhach | Cho check-out | [PhongCoKhach.java](src/main/java/vn/edu/dlu/khachsan/pattern/state/PhongCoKhach.java) |
| PhongDangDon | Cho dọn xong, bảo trì | [PhongDangDon.java](src/main/java/vn/edu/dlu/khachsan/pattern/state/PhongDangDon.java) |
| PhongBaoTri | Cho kết thúc bảo trì; không nhận đặt | [PhongBaoTri.java](src/main/java/vn/edu/dlu/khachsan/pattern/state/PhongBaoTri.java) |

`Phong` ở package entity là Context: `layTrangThai()` ánh xạ chuỗi lưu CSDL thành State; `checkIn()`, `checkOut()`, `donXong()`, `baoTri()`, `ketThucBaoTri()` ủy quyền cho State. Chỉ các lớp State gọi `Phong.chuyenSang()`; không có setter trạng thái công khai.

**Nơi sử dụng:** `NhanTraPhongService.checkIn()`/`checkOut()` khóa và thao tác mọi phòng trong một transaction; `PhongService.thaoTac()` gọi hành vi buồng phòng; `PhongService.timPhong()` và Builder gọi `coTheNhanDat()`. Trang buồng phòng lấy nút từ `thaoTacHopLe()`, chỉ hiện ba thao tác của buồng phòng.

**Demo:** lễ tân check-in DP00000002 → phòng 201 Đang có khách. Check-out DP00000003 sau khi thu đủ → phòng 202 Đang dọn. Đăng nhập buồng phòng → Tình trạng phòng → Dọn xong → Trống; thử Bảo trì rồi Kết thúc bảo trì. Trường hợp đơn nhiều phòng có phòng 104 Đang dọn được kiểm thử tích hợp: từ chối đúng thông báo State và rollback tất cả phòng.

**Kiểm thử:** `TrangThaiPhongTest.diDuVongTrangThaiQuaContext()`, `checkInKhiDangDonTraDungThongBaoVaKhongDoiTrangThai()`, toàn bộ 20 cặp tình trạng/thao tác; `Buoc8LeTanIT.checkInNhieuPhongLoiPhaiRollbackTatCa()`, `Buoc9QuanLyIT.donXongBaoTriVaKhongChoBuongPhongCheckIn()`. Sơ đồ: [state.puml](docs/uml/state.puml), [state_phong.puml](docs/uml/state_phong.puml).

## 4. Strategy – xử lý từng phương thức thanh toán

Tiền mặt, thẻ và trực tuyến có dữ liệu đầu vào và cách xử lý khác nhau. Mỗi Strategy chịu trách nhiệm xác thực dữ liệu riêng, ghi chú và trả kết quả xử lý. `ThanhToanService` là Context, chọn Strategy trong Map theo mã; không rẽ nhánh if/switch theo mã phương thức.

| Lớp | Vai trò | File |
|---|---|---|
| PhuongThucThanhToan | Strategy: mã, tên, phạm vi sử dụng, xuLy() | [PhuongThucThanhToan.java](src/main/java/vn/edu/dlu/khachsan/pattern/strategy/PhuongThucThanhToan.java) |
| DuLieuThanhToan | Record tiền khách đưa và mã chuẩn chi giả lập | [DuLieuThanhToan.java](src/main/java/vn/edu/dlu/khachsan/pattern/strategy/DuLieuThanhToan.java) |
| KetQuaXuLy | Record trạng thái, thông báo, URL chuyển hướng | [KetQuaXuLy.java](src/main/java/vn/edu/dlu/khachsan/pattern/strategy/KetQuaXuLy.java) |
| ThanhToanTrucTuyen | ConcreteStrategy: Đang xử lý, URL cổng giả lập | [ThanhToanTrucTuyen.java](src/main/java/vn/edu/dlu/khachsan/pattern/strategy/ThanhToanTrucTuyen.java) |
| ThanhToanTienMat | ConcreteStrategy: kiểm tra đủ tiền, ghi và thông báo tiền thối | [ThanhToanTienMat.java](src/main/java/vn/edu/dlu/khachsan/pattern/strategy/ThanhToanTienMat.java) |
| ThanhToanThe | ConcreteStrategy: kiểm tra mã chuẩn chi đúng sáu chữ số | [ThanhToanThe.java](src/main/java/vn/edu/dlu/khachsan/pattern/strategy/ThanhToanThe.java) |

**Nơi sử dụng:** `ThanhToanService.layPhuongThuc()` lấy từ `Map<String,PhuongThucThanhToan>`; `phuongThucTrucTuyen()`/`phuongThucTaiQuay()` lọc theo `dungTaiQuay()`; `batDauTrucTuyen()` và `thanhToan()` gọi `xuLy()`. Template lấy tên từ `getTenHienThi()` qua Service. Callback giả lập idempotent, tính lại hóa đơn từ tổng giao dịch Thành công.

**Demo:** khách thanh toán trực tuyến → cổng giả lập → thất bại → thử lại → thành công, một hóa đơn/hai giao dịch. Lễ tân mở check-out phòng 202: tiền mặt nhập thiếu bị từ chối; nhập 1.400.000 đ cho phần còn lại 1.330.000 đ thấy tiền thối 70.000 đ. Với một lượt demo chưa thu tiền, chọn thẻ, nhập `abc` bị từ chối; nhập `123456` được chấp nhận. Không lưu số thẻ hoặc thông tin ngân hàng.

**Kiểm thử:** `PhuongThucThanhToanTest` (tiền mặt, tiền lẻ, mã chuẩn chi, trực tuyến, Spring nhận đủ ba bean); `Buoc6NghiepVuIT.thatBaiThuLaiDungMotHoaDonVaHaiGiaoDichThanhCongIdempotent()`, `Buoc8LeTanIT.thuTienMatDuHienTienThoiVaHoanTat()`, `thieuTienVaMaTheSaiBiTuChoi()`. Sơ đồ: [strategy.puml](docs/uml/strategy.puml).

## Chạy kiểm thử

```powershell
.\mvnw.cmd test
# Cần MySQL Docker cho các kiểm thử tích hợp:
.\mvnw.cmd '-Dtest=*Test,*IT' test
```

JUnit cho mẫu không cần CSDL. Kiểm thử luồng bước 6–9 dùng CSDL `quan_ly_khach_san_kiem_thu` và email trong `.tools/email-kiem-thu/`; không thay đổi các đơn mẫu ở CSDL demo.
