# AGENTS.md

## Đặc tả dự án
- Toàn bộ yêu cầu nằm trong file `YeuCau_XayDung_WebsiteQuanLyKhachSan.md`. Đọc file này trước khi làm bất cứ việc gì.
- Luôn làm đúng theo đặc tả, đặc biệt **Mục 0** (hướng dẫn), **Mục 6** (4 mẫu thiết kế) và **Mục 12** (những điều không được làm).
- Không tự thêm chức năng ngoài đặc tả. Điểm nào chưa rõ thì chọn cách đơn giản nhất và ghi vào `README.md`.

## Cách làm việc
- Làm theo thứ tự các bước ở **Mục 14**. Xong mỗi bước thì chạy thử, báo kết quả và **dừng lại chờ tôi xác nhận** trước khi sang bước tiếp theo.
- Khi báo kết quả, ghi ngắn gọn: đã làm gì, các file đã tạo/sửa, lệnh đã chạy, còn lỗi gì không.
- Trả lời tôi bằng **tiếng Việt**.

## Môi trường
- Hệ điều hành: Windows, terminal PowerShell trong VS Code.
- Java: JDK 21. Chạy ứng dụng bằng `.\mvnw.cmd spring-boot:run`, chạy test bằng `.\mvnw.cmd test`.
- CSDL: MySQL 8 chạy bằng Docker (`docker compose up -d`), cổng 3307, cấu hình ở Mục 10 và 10.1 của đặc tả.
- Trước khi chạy ứng dụng, kiểm tra container `khachsan-mysql` đang chạy bằng `docker ps`.

## Quy tắc code
- 4 mẫu thiết kế (Builder, Decorator, State, Strategy) đặt trong package `vn.edu.dlu.khachsan.pattern`, đúng tên lớp như Mục 6. Không thay bằng `if-else` / `switch-case`.
- Không dùng Lombok. Tiền dùng `BigDecimal`.
- Giao diện và thông báo lỗi bằng tiếng Việt có dấu. Comment trong code bằng tiếng Việt, ngắn gọn.
- Không sửa file `YeuCau_XayDung_WebsiteQuanLyKhachSan.md` và file `AGENTS.md`.
