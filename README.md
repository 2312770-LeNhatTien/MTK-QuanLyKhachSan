# Website quản lý khách sạn

Ứng dụng demo cho một khách sạn, xây dựng theo `YeuCau_XayDung_WebsiteQuanLyKhachSan.md`: Spring Boot 3.5.13, Java 21, MVC/Thymeleaf, MySQL 8, Spring Security, Bootstrap 5 và Chart.js. Có đủ CN01–CN17, hai phân hệ khách hàng/nhân viên và bốn mẫu Builder, Decorator, State, Strategy.

## 1. Cài đặt và chạy trên Windows

Cần JDK 21, Docker Desktop chạy Linux containers, PowerShell. Maven Wrapper có sẵn; lần build đầu cần Internet để tải Maven và dependency. Bootstrap/Icons/Chart.js đã lưu cục bộ, không cần CDN khi sử dụng giao diện.

Mở PowerShell tại thư mục dự án:

```powershell
# Chọn đường dẫn JDK 21 thực tế trên máy nếu JAVA_HOME chưa đúng:
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
.\mvnw.cmd -version

docker compose up -d
docker ps --filter name=khachsan-mysql
# Chờ trạng thái healthy trước khi chạy:
.\mvnw.cmd spring-boot:run
```

Mở [trang khách hàng](http://localhost:8080/) hoặc [đăng nhập nhân viên](http://localhost:8080/admin/dang-nhap). Nếu 8080 đang được dùng:

```powershell
.\mvnw.cmd spring-boot:run '-Dspring-boot.run.arguments=--server.port=8081'
```

Khi đó mở [http://localhost:8081](http://localhost:8081/). Dừng ứng dụng bằng `Ctrl+C`, dừng MySQL bằng `docker compose stop`. Lệnh này giữ dữ liệu trong Docker volume.

## 2. CSDL và mật khẩu MySQL

`compose.yaml` chạy MySQL 8.0, container `khachsan-mysql`, cổng máy **3307**, CSDL `quan_ly_khach_san`, charset `utf8mb4_unicode_ci`. Mật khẩu root mặc định cho demo cục bộ là `khachsan_demo`. Để chọn mật khẩu khác **trước lần khởi tạo volume đầu tiên**, đặt cùng biến khi tạo container và chạy ứng dụng:

```powershell
$env:DB_PASSWORD = 'mat-khau-cua-ban'
docker compose up -d
.\mvnw.cmd spring-boot:run
```

Cấu hình kết nối ở [application.properties](src/main/resources/application.properties). `spring.datasource.password=${DB_PASSWORD:khachsan_demo}` đọc biến môi trường; JDBC dùng localhost:3307. Có thể ghi đè URL bằng `SPRING_DATASOURCE_URL` nếu dùng MySQL 8 có sẵn. Nếu tạo CSDL thủ công:

```sql
CREATE DATABASE quan_ly_khach_san CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Với volume đã khởi tạo, đổi `DB_PASSWORD` không tự đổi mật khẩu trong MySQL. Đăng nhập bằng mật khẩu hiện có trong MySQL client, chạy `ALTER USER` theo tài khoản root hiện có, rồi đặt lại biến môi trường tương ứng trước khi chạy ứng dụng. Không xóa volume để đổi mật khẩu vì sẽ mất dữ liệu.

Flyway tự chạy [V1__tao_bang.sql](src/main/resources/db/migration/V1__tao_bang.sql). Hibernate chỉ **validate**, không tự tạo/sửa bảng. Có đúng **13 bảng nghiệp vụ**, cộng bảng kỹ thuật `flyway_schema_history`. Migration có 70 cột, 13 PK, 10 UNIQUE, 11 FK `ON DELETE RESTRICT`, 14 CHECK và các DEFAULT theo đặc tả.

## 3. Tài khoản demo

Mật khẩu của cả bốn tài khoản: **123456**.

| Phân hệ | Email | Vai trò |
|---|---|---|
| `/dang-nhap` | `khach@gmail.com` | Khách hàng |
| `/admin/dang-nhap` | `letan@khachsan.vn` | Lễ tân – VT01 |
| `/admin/dang-nhap` | `quanly@khachsan.vn` | Quản lý – VT02 |
| `/admin/dang-nhap` | `buongphong@khachsan.vn` | Buồng phòng – VT03 |

Hai bộ xác thực đọc hai bảng riêng. Một phiên trình duyệt giữ một tài khoản; đăng nhập phân hệ khác thay thế tài khoản hiện tại. Dùng các hồ sơ trình duyệt khác nhau nếu muốn thao tác đồng thời nhiều vai trò. Cookie phiên là `KHACHSAN_SESSION` để tránh dùng chung JSESSIONID với dự án khác trên localhost.

Dữ liệu mẫu chỉ tạo nếu `NHAN_VIEN` trống. Gọi ngày khởi tạo lần đầu là **H**:

| Đơn | Phòng / ngày lưu trú | Trạng thái và số tiền |
|---|---|---|
| DP00000001 | 101, H−5 → H−3, buffet 2 người | Hoàn tất; 1.600.000 đ; đủ tiền; chưa đánh giá |
| DP00000002 | 201, H → H+2 | Đã xác nhận; 1.600.000 đ; cọc 480.000 đ; chưa nhận phụ trách/check-in |
| DP00000003 | 202, H−1 → H+1, đưa đón 1 lượt | Đang ở; 1.900.000 đ; cọc 570.000 đ; NV0001 phụ trách |
| DP00000004 | 102, H+3 → H+4 | Đã hủy; 500.000 đ; chưa hóa đơn |

Có 3 loại phòng, 12 phòng, 4 dịch vụ và 3 tham số. Phòng 104 Đang dọn, 202 Đang có khách, 304 Bảo trì. Ngày tương đối tính một lần khi tạo dữ liệu; lần khởi động sau không dời ngày, đặt lại mật khẩu, tham số hoặc trạng thái đơn đã sử dụng.

## 4. Cách demo các luồng

**Khách hàng:** trang chủ → tìm khoảng ngày/số khách → chọn một hoặc nhiều phòng → đăng nhập → xác nhận số khách từng phòng, dịch vụ/số lượng → xem tạm tính → đồng ý quy định → đặt phòng → thanh toán cọc hoặc toàn bộ → cổng giả lập → bấm thành công/thất bại. Cổng này chỉ mô phỏng, không thu tiền thật. Thất bại được thử lại trong thời gian giữ phòng, dùng cùng hóa đơn và giao dịch mới. Thành công tạo file `email-logs/xac-nhan-DP........html` (không gửi email thật).

Vào Lịch sử đặt phòng để lọc, xem chi tiết, hủy theo thời hạn hoặc đánh giá đơn Hoàn tất. Trang chủ hiển thị sáu đánh giá mới nhất. Hủy không xóa đơn, không hoàn tiền trong hệ thống; đơn đã trả tiền hiển thị thông báo khách sạn liên hệ về việc hoàn tiền.

**Bốn mẫu:** một phòng Deluxe/hai đêm + buffet số lượng 2 + đưa đón số lượng 1 phải có ba dòng và tổng **2.500.000 đ**. Tick/bỏ tick dịch vụ cập nhật bảng ngay từ server; JavaScript không tính tiền phòng/dịch vụ. Hướng dẫn chi tiết và tên test ở [GHI_CHU_MAU_THIET_KE.md](GHI_CHU_MAU_THIET_KE.md).

**Lễ tân:** Đơn cần xác nhận → Nhận phụ trách DP00000002. Check-in → nhập mã đơn đó → kiểm tra ngày/phòng → Check-in. Check-out → nhập `202` hoặc `DP00000003` → còn lại **1.330.000 đ** → chọn tiền mặt, nhập `1400000` → thông báo tiền thối **70.000 đ** → Hoàn tất trả phòng → In hóa đơn. Với đơn khác chưa thu phần còn lại, chọn thẻ và nhập mã chuẩn chi giả lập gồm đúng 6 chữ số. Không nhập hoặc lưu thông tin thẻ thật.

**Buồng phòng:** Tình trạng phòng → mục Phòng cần dọn ở đầu trang → Dọn xong/Bảo trì/Kết thúc bảo trì. Nút xuất hiện theo State; không có nút check-in/check-out. Check-out chuyển phòng sang Đang dọn, Dọn xong chuyển về Trống.

**Quản lý:** thêm/sửa/xóa loại phòng, phòng, nhân viên; đặt lại mật khẩu; tra cứu toàn bộ đơn theo trạng thái/ngày đặt/mã/email (chỉ xem); cấu hình ba tham số; xem doanh thu theo ngày/phương thức, số đơn và công suất. Không được xóa loại đang có phòng, phòng có lịch sử đặt, nhân viên đã phụ trách đơn hoặc chính tài khoản đang đăng nhập.

## 5. Kiểm thử

JUnit cho bốn mẫu không cần CSDL:

```powershell
.\mvnw.cmd test
```

Chạy toàn bộ kiểm thử, cần MySQL Docker hoạt động và thông tin kết nối như trên:

```powershell
docker ps --filter name=khachsan-mysql
.\mvnw.cmd '-Dtest=*Test,*IT' test
```

| Nhóm | Kiểm tra chính |
|---|---|
| `pattern/*Test` | 73 trường hợp Builder, Decorator, State, Strategy, không cần CSDL |
| `Buoc4MySqlIT` | Schema, sinh mã, seed, tham số, quan hệ dữ liệu |
| `Buoc5SecurityIT` | 89 trường hợp bảo mật/tài khoản/menu, gồm ma trận 64 URL/vai trò và đăng nhập 4 tài khoản thật |
| `Buoc6NghiepVuIT` | Tìm/đặt, khóa đồng thời, tạm tính/CSRF, retry/idempotent thanh toán, hết hạn |
| `Buoc7KhachHangIT` | Lịch sử/quyền sở hữu, hạn hủy, đánh giá một lần, hiển thị trang chủ |
| `Buoc8LeTanIT` | Nhận phụ trách, check-in/rollback đa phòng, tiền mặt/thẻ, check-out/hóa đơn |
| `Buoc9QuanLyIT` | CRUD, State buồng phòng, giá chốt, lọc đơn, tham số tức thời, báo cáo và render trang |

Kiểm thử ghi dữ liệu dùng rollback; các kiểm thử đặt đồng thời/rollback nhiều phòng có transaction độc lập sẽ giữ đơn thử ở Đã hủy trong CSDL kiểm thử riêng `quan_ly_khach_san_kiem_thu`. Email thử được ghi vào `.tools/email-kiem-thu/`. Các kiểm thử bước 4–5 dùng CSDL nền tự seed `quan_ly_khach_san_kiem_thu_nen`, tách khỏi CSDL demo và luồng kiểm thử; xem kết quả và phạm vi xác minh ở [docs/NGHIEM_THU.md](docs/NGHIEM_THU.md).

Đóng gói (sau khi test đạt):

```powershell
.\mvnw.cmd package
java -jar target/khachsan-0.0.1-SNAPSHOT.jar --server.port=8081
```

Dừng ứng dụng đang dùng 8081 trước khi chạy JAR. Không cần cài Maven riêng.

## 6. Kiến trúc và tài liệu

- `controller/` → `service/` → `repository/` → MySQL; Controller không gọi Repository.
- `entity/`: 13 entity và enum trạng thái. Tiền dùng `BigDecimal`, không dùng Lombok.
- `pattern/`: đúng bốn mẫu, đủ 23 lớp/interface/record/enum theo Mục 6.
- `scheduler/HuyDonQuaHanJob`: kiểm tra đơn quá hạn mỗi 60 giây.
- `seed/DuLieuMauRunner`: dữ liệu tương đối theo ngày khởi tạo; đơn được tạo qua Builder.
- `templates/`, `static/`: giao diện tiếng Việt, fragment chung, Bootstrap/Icons/Chart.js cục bộ; minh họa phòng bằng CSS.
- [Ghi chú mẫu thiết kế](GHI_CHU_MAU_THIET_KE.md), [5 nguồn PlantUML](docs/uml/), [nghiệm thu](docs/NGHIEM_THU.md).

Hai `SecurityFilterChain`, BCrypt, phân quyền URL đúng vai trò, kiểm tra quyền sở hữu đơn ở Service. CSRF giữ mặc định, kể cả request fetch tạm tính. Các thao tác nhiều bảng nằm trong transaction; khi tạo đơn khóa phòng theo mã tăng dần rồi kiểm tra trùng lịch lại. Các trang lỗi 403/404/500 tiếng Việt không hiển thị stack trace.

## 7. Lựa chọn và giả định

1. Mục 10 của đặc tả minh họa 3306, nhưng AGENTS.md yêu cầu Docker cổng 3307 (không có Mục 10.1 trong tài liệu): dùng **3307**. Tên khách sạn đơn giản là “Khách sạn”.
2. Chạy một instance ứng dụng demo. Mã sinh từ mã lớn nhất trong CSDL + 1; khóa sinh mã giữ đến commit/rollback, cấp nhiều mã trong cùng transaction không trùng. Khóa MySQL `FOR UPDATE` bảo vệ dữ liệu được đọc. Không sử dụng bộ đếm lâu dài trong bộ nhớ.
3. Tìm kiếm hiểu “số khách” là số khách **mỗi phòng**. Ở xác nhận có thể điều chỉnh từng phòng. Khoảng lưu trú là [ngày nhận, ngày trả), phòng Đang dọn/Đang có khách vẫn có thể đặt nếu không bảo trì và không trùng lịch theo QĐ3; check-in vẫn yêu cầu State Trống.
4. Số phòng gồm 1–9 chữ cái/chữ số, mã là `P` + số phòng. Giữ số phòng cố định sau khi tạo để không đổi khóa và lịch sử; sửa được tầng/loại, không có trường sửa tình trạng.
5. Bảy ngày nhận phụ trách gồm hôm nay và sáu ngày tiếp theo. Lọc khoảng ngày trong quản lý đơn và số đơn trong báo cáo dựa trên **ngày đặt**; doanh thu dựa trên **ngày giao dịch**.
6. Công suất: tổng số đêm-phòng giao với khoảng ngày chọn của đơn Đã xác nhận/Hoàn tất, chia số phòng hiện tại không Bảo trì × số ngày chọn (gồm hai đầu). Không có bảng lịch sử tồn kho nên dùng số phòng hiện tại làm mẫu số; mẫu số 0 thì hiển thị 0%.
7. Tiền cọc tối thiểu làm tròn lên hai chữ số thập phân. Đã trả/còn lại và trạng thái hóa đơn tính từ các giao dịch Thành công; giao dịch Thất bại/Đang xử lý không tính doanh thu. Dịch vụ giữ thứ tự chọn trong đơn. Đổi giá không sửa giá đã chốt.
8. Tham số đọc trực tiếp từ THAM_SO, không cache hoặc viết cứng. Giữ phòng tính theo phút, hủy theo ngày ở múi giờ `Asia/Ho_Chi_Minh`; tác vụ nền có thể xử lý chậm tối đa gần 60 giây. Khi thanh toán vẫn kiểm tra hết hạn trực tiếp.
9. Đơn mẫu và mật khẩu không tự đặt lại khi khởi động. Để có dữ liệu mẫu tương đối mới, dùng một CSDL demo mới; không xóa lịch sử hiện có.
10. Mỗi trình duyệt một tài khoản/phiên. Đổi mật khẩu khách đăng xuất phiên hiện tại; thay đổi vai trò nhân viên có hiệu lực ở lần đăng nhập sau. Spring có thể cảnh báo có hai UserDetailsService ở global manager; từng chain đã có AuthenticationManager riêng và được kiểm thử cách ly.
11. HTML5 date picker hiển thị theo ngôn ngữ trình duyệt/hệ điều hành; ngày trong bảng/nội dung định dạng `dd/MM/yyyy`, tiền `1.250.000 đ`. Toàn bộ câu chữ giao diện là tiếng Việt.
12. Email là file HTML cục bộ, không SMTP. Đây là demo một máy; không có hoàn tiền tự động, thuế, khuyến mãi, tích hợp ngân hàng, thêm dịch vụ sau đặt hoặc tính năng ngoài đặc tả.

