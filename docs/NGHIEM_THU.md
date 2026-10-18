# Nghiệm thu theo Mục 15

Ngày kiểm tra: **18/10/2026**. Hoàn thành các bước 1–12, Mục 14. Các bước 6–9 được kiểm thử riêng trước khi chuyển bước; từ bước 7 tiếp tục đến hết theo chỉ dẫn mới của người dùng.

## Lệnh và kết quả

```powershell
.\mvnw.cmd test
.\mvnw.cmd '-Dtest=*Test,*IT' test
.\mvnw.cmd '-Dtest=*Test,*IT' package
```

**Bản cuối: 210 test đạt, 0 thất bại, 0 lỗi, 0 bỏ qua; BUILD SUCCESS.**

| Nhóm | Số test |
|---|---:|
| Builder | 19 |
| Decorator | 6 |
| State | 26 |
| Strategy | 22 |
| Buoc4MySqlIT | 20 |
| Buoc5SecurityIT | 89 |
| Buoc6NghiepVuIT | 8 |
| Buoc7KhachHangIT | 5 |
| Buoc8LeTanIT | 6 |
| Buoc9QuanLyIT | 9 |
| **Tổng** | **210** |

Đã chạy thử bản `target/khachsan-0.0.1-SNAPSHOT.jar` bằng JDK 21 trên cổng 8081, sau khi xác nhận `khachsan-mysql` healthy. Flyway/JPA validate thành công, seed bỏ qua đúng điều kiện, đăng nhập và trang chủ hoạt động. Lệnh:

```powershell
java -jar target/khachsan-0.0.1-SNAPSHOT.jar --server.port=8081
```

## Checklist dữ liệu

- [x] Đúng 13 bảng nghiệp vụ, 70 cột; đối chiếu tên/kiểu/nullability trên `information_schema` với Mục 4.
- [x] 13 PK, 10 UNIQUE, 11 FK, toàn bộ FK `ON DELETE RESTRICT`, 14 CHECK.
- [x] DEFAULT đúng: thời điểm hiện tại, phòng Trống, đơn Chờ thanh toán/tổng 0, hóa đơn Chưa thanh toán, giao dịch Đang xử lý.
- [x] Dữ liệu mẫu đủ 3 vai trò, 3 nhân viên, 1 khách, 3 loại, 12 phòng, 4 dịch vụ, 3 tham số, 4 đơn, 3 hóa đơn/3 giao dịch. Kiểm thử nền chạy trên schema riêng chưa dùng cho demo.
- [x] Đăng nhập được cả bốn tài khoản bằng mật khẩu thật từ MySQL, BCrypt; hai phân hệ không xác thực nhầm bảng. Kiểm chứng bằng `Buoc5SecurityIT`.

Bảng `flyway_schema_history` là bảng kỹ thuật, không tính vào 13 bảng nghiệp vụ. Bằng chứng metadata: [schema-mysql.txt](kiem-tra/schema-mysql.txt).

## Checklist nghiệp vụ

- [x] Tìm phòng loại Bảo trì và loại đơn trùng lịch Chờ thanh toán/Đã xác nhận; nhận mới đúng ngày trả của đơn cũ được phép. `Buoc6NghiepVuIT`.
- [x] Hai yêu cầu đồng thời cùng phòng/khoảng ngày chỉ một đơn được tạo; kiểm thử dùng hai thread và transaction thực, đơn thử còn lại bị từ chối.
- [x] Đặt phòng không thay đổi tình trạng hiện tại; kiểm tra cả service và trang chi tiết đơn thử trên trình duyệt.
- [x] Hết hạn tự hủy đơn và chuyển giao dịch Đang xử lý thành Thất bại; có kiểm thử nghiệp vụ và kiểm tra scheduler chạy thật trên bản JAR (chi tiết bên dưới).
- [x] Thất bại rồi thử lại: cùng một hóa đơn, hai giao dịch khác nhau, một thất bại/một thành công; callback lặp không ghi nhận thêm tiền.
- [x] Cọc dưới mức tham số bị từ chối; cọc hợp lệ xác nhận đơn, hóa đơn Đã đặt cọc, có file HTML email.
- [x] Hủy đơn chờ, hủy đúng ngày biên/ngoài thời hạn, không được hủy đơn người khác; không xóa lịch sử hay hoàn tiền tự động. `Buoc7KhachHangIT`.
- [x] Chỉ chủ đơn Hoàn tất đánh giá được, 1–5 sao, mỗi đơn một lần; hiển thị đánh giá trên trang chủ. `Buoc7KhachHangIT`.
- [x] Cập nhật tham số qua controller có hiệu lực ngay; tỉ lệ cọc 50% được đọc ngay ở lần tính tiếp theo; ngoài 1–100 bị từ chối. `Buoc9QuanLyIT`.
- [x] Vai trò chỉ thấy/truy cập đúng chức năng; ma trận 64 cặp URL/vai trò, CSRF, đăng xuất, đăng ký/đổi mật khẩu và quyền sở hữu đơn. `Buoc5SecurityIT` và các test luồng khách.
- [x] Báo cáo chỉ tính giao dịch Thành công theo ngày thanh toán; kiểm tra giao khoảng đêm-phòng, mẫu số loại Bảo trì và phần trăm. `Buoc9QuanLyIT`.
- [x] Nhận phụ trách, điều kiện ngày check-in, thu đủ phần còn lại, check-out hoàn tất, hóa đơn và tình trạng phòng đúng. `Buoc8LeTanIT`.
- [x] CRUD và ràng buộc xóa, mật khẩu BCrypt, giữ giá chốt cũ, lọc đơn và render mọi trang quản lý. `Buoc9QuanLyIT`.

## Checklist mẫu thiết kế

- [x] 73 test mẫu JUnit 5 đạt, không cần CSDL.
- [x] Builder: tìm toàn bộ `src/main/java` chỉ có một `new DatPhong()` trong `DatPhongBuilder.build()`.
- [x] Decorator: tạm tính thêm/bớt dịch vụ và tổng đổi đúng; hóa đơn/email/chi tiết cùng dùng `chiTiet()`. Kiểm tra số **2.500.000 đ** trên trình duyệt, giảm còn **2.200.000 đ** khi bỏ đưa đón, chọn lại trở về **2.500.000 đ**.
- [x] State: đi đủ vòng Trống → Đang có khách → Đang dọn → Trống; từ chối check-in Đang dọn đúng thông báo; kiểm thử transaction độc lập chứng minh rollback tất cả phòng khi một phòng lỗi. Chỉ các lớp State gọi `Phong.chuyenSang()`.
- [x] State: lưới phòng theo tầng, chữ + biểu tượng, mục cần dọn và nút lấy từ thao tác hợp lệ; buồng phòng không có check-in/check-out.
- [x] Strategy: tiền mặt thiếu bị từ chối, nhập dư trả thông báo tiền thối đúng; thẻ yêu cầu sáu chữ số; trực tuyến trả URL giả lập. Service chọn từ Map, không if/switch theo mã phương thức.
- [x] Ghi chú và năm nguồn PlantUML đủ tên 23 lớp và phương thức chính, đã đối chiếu code; bàn giao nguồn `.puml`, không yêu cầu phần mềm PlantUML để chạy website.
- [x] Controller chỉ gọi Service; không dùng Repository trực tiếp, không tính chi phí phòng/dịch vụ ở Controller/JavaScript.

## Kiểm tra trực tiếp trên trình duyệt

Trình duyệt trong ứng dụng có khung hẹp khoảng 397 px; đã kiểm tra form tìm kiếm, chọn phòng, xác nhận dịch vụ, bảng tạm tính, đồng hồ giữ phòng, cổng giả lập, kết quả và chi tiết; kiểm tra đăng nhập quản lý, báo cáo cột/bảng theo ngày và menu theo vai trò.

Đơn demo phát sinh khi nghiệm thu:

| Dữ liệu | Kết quả |
|---|---|
| DP00000005, phòng 201, 01/11–03/11/2026, buffet 2 và đưa đón 1 | Tổng 2.500.000 đ; tình trạng phòng vẫn Trống; đơn Đã xác nhận |
| HD00000004 | Một hóa đơn, Đã đặt cọc |
| TT00000004 / TT00000005 | Thất bại / Thành công, mỗi giao dịch 750.000 đ; chỉ cộng 750.000 đ đã trả |
| Email | `email-logs/xac-nhan-DP00000005.html`, đủ mã/ngày/phòng/ba dòng chi phí/số đã trả |
| DP00000006 / TT00000006 | Đơn riêng dùng để kiểm tra hết hạn thật, không bấm nút kết quả thanh toán |

Để kiểm tra scheduler mà không chờ đủ 15 phút, chỉ cập nhật NgayDat của **đơn thử DP00000006 vừa tạo** về 16 phút trước. Trước tác vụ: đơn Chờ thanh toán/giao dịch Đang xử lý. Để tác vụ mặc định chạy tự động trong chu kỳ 60 giây; không gọi phương thức hủy từ controller hoặc test thay thế ở lần kiểm tra trực tiếp này.

Kết quả thực tế: **DP00000006 = DA_HUY, TT00000006 = THAT_BAI**. Trang chi tiết hiển thị Đã hủy, phòng 203 vẫn Trống. Bằng chứng: [trước tác vụ](kiem-tra/job-truoc.txt), [sau tác vụ](kiem-tra/job-sau.txt), [ảnh tự hủy](kiem-tra/buoc12-tu-huy.png).

Ảnh: [tạm tính](kiem-tra/buoc12-tam-tinh.png), [thanh toán thành công](kiem-tra/buoc12-thanh-toan.png). Bốn đơn mẫu gốc được giữ để tiếp tục demo; hai đơn nghiệm thu được giữ đúng quy định không xóa vật lý đơn/hóa đơn/giao dịch.

Bản tóm tắt log Maven: [ket-qua-test.txt](kiem-tra/ket-qua-test.txt). Hiện ứng dụng chạy bằng bản JAR cuối tại [http://localhost:8081](http://localhost:8081/).

## Phạm vi xác minh

Các test tích hợp bước 4–5 dùng `quan_ly_khach_san_kiem_thu_nen`; bước 6–9 dùng `quan_ly_khach_san_kiem_thu`, email thử trong `.tools/email-kiem-thu/`. Không cần giữ schema demo nguyên bản để chạy lại toàn bộ test. Các test đồng thời có transaction commit độc lập để kiểm tra khóa thực; sau test đơn thử được chuyển Đã hủy.

Hóa đơn có `window.print()` và CSS A4; không gửi lệnh tới máy in vật lý. Thanh toán và email đều mô phỏng theo đặc tả. Ngày seed không được dời lại sau mỗi lần khởi động; điều này được giải thích trong README. Không sửa `AGENTS.md` hoặc file đặc tả.
