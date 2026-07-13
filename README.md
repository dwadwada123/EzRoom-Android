# Ứng dụng EzRoom Android

EzRoom là giải pháp di động toàn diện cho việc quản lý thuê và cho thuê phòng trọ, phục vụ cả người thuê và chủ nhà. Ứng dụng tập trung vào tính minh bạch, hiệu quả và trải nghiệm người dùng cao cấp.

## Tổng quan dự án

Ứng dụng EzRoom Android được xây dựng dựa trên các tiêu chuẩn phát triển Android hiện đại nhất. Giao diện được thiết kế hiện đại, tinh tế với các hiệu ứng Glassmorphism (kính mờ) và bố cục Bento Grid. Dự án tuân thủ nghiêm ngặt nguyên lý Kiến trúc sạch (Clean Architecture) để đảm bảo khả năng mở rộng và bảo trì lâu dài.

## Các tính năng chính

### Dành cho người thuê (Renter)
- **Khám phá phòng trọ**: Tìm kiếm và lọc nâng cao theo vị trí (Tỉnh/Thành, Quận/Huyện), khoảng giá và các tiện ích đi kèm.
- **Danh sách yêu thích**: Lưu trữ và quản lý các phòng trọ ưng ý với tính năng bỏ yêu thích nhanh và xác nhận an toàn.
- **Hợp đồng & Tiền cọc (Fintech)**: Ký hợp đồng điện tử và thanh toán tiền cọc qua QR Code. Tiền cọc được đóng băng an toàn trên hệ thống cho đến ngày nhận phòng.
- **Quản lý hóa đơn**: Theo dõi danh sách hóa đơn hàng tháng, kiểm tra chi tiết các khoản phí dịch vụ và trạng thái thanh toán.
- **Báo cáo và Đánh giá**: Gửi báo cáo vi phạm bài đăng hoặc viết đánh giá chất lượng phòng sau khi thuê.
- **Trò chuyện trực tiếp**: Giao tiếp trực tiếp với chủ nhà thông qua hệ thống nhắn tin nội bộ.

### Dành cho chủ nhà (Host)
- **Bảng điều khiển (Dashboard)**: Thống kê doanh thu thực tế, tỉ lệ lấp đầy phòng và các nhiệm vụ cần xử lý ngay trong ngày.
- **Quản lý bất động sản**: Hỗ trợ quản lý theo mô hình Dãy trọ/Tòa nhà (nhiều phòng) hoặc các căn nhà cho thuê lẻ độc lập. Hỗ trợ ẩn/hiện phòng linh hoạt.
- **Quản lý tài khoản nhận tiền**: Tích hợp danh sách hơn 50 ngân hàng Việt Nam (VietQR API) để quản lý tài khoản nhận cọc và giải ngân.
- **Xử lý lịch hẹn**: Phê duyệt, hủy hoặc đề xuất hẹn lại thời gian xem phòng với khách thuê.
- **Hợp đồng điện tử**: Soạn thảo, gửi và ký kết hợp đồng thuê phòng trực tuyến. Theo dõi trạng thái đóng băng và giải ngân tiền cọc.
- **Kháng cáo bài đăng**: Xem lý do bài đăng bị gỡ và thực hiện gửi đơn kháng cáo kèm hình ảnh minh chứng.
- **Lập hóa đơn tự động**: Hệ thống tự động tính toán tiền điện, nước dựa trên chỉ số cũ/mới và đơn giá đã cài đặt.

## Công nghệ sử dụng

- **Ngôn ngữ**: Kotlin
- **Giao diện**: Jetpack Compose (100% Declarative UI)
- **Kiến trúc**: Clean Architecture (Domain, Data, UI) kết hợp MVVM.
- **Xử lý bất đồng bộ**: Kotlin Coroutines và Flow.
- **Điều hướng**: Compose Navigation với hiệu ứng chuyển cảnh chuyên nghiệp.
- **Mạng**: Retrofit & OkHttp (Tích hợp VietQR API).
- **Xử lý hình ảnh**: Coil (loading, caching và ContentScale.Crop cho UX tốt nhất).

## Quy tắc nghiệp vụ đặc thù

- **Hệ thống Escrow (Tiền gửi an toàn)**: Tiền cọc của người thuê được App "đóng băng" bảo vệ. Tiền chỉ giải ngân cho Chủ nhà vào ngày bắt đầu hợp đồng để đảm bảo quyền lợi hai bên.
- **Cơ chế tranh chấp**: Khi bài đăng bị gỡ do vi phạm, Host có 7 ngày để kháng cáo trước khi bài đăng bị xóa vĩnh viễn.
- **Hoa hồng nền tảng**: Hệ thống tự động trích 5% hoa hồng dựa trên Tiền thuê phòng cố định (không tính trên điện, nước, cọc).
- **Trải nghiệm người dùng**: Sử dụng Staggered Entrance Animations cho các danh sách và xác nhận Dialog cho các hành động xóa/hủy quan trọng.

## Cấu trúc thư mục

- `core`: Định nghĩa lỗi (AppError), kết quả (Try) và tiện ích dùng chung.
- `domain`: Tầng trung tâm chứa Logic nghiệp vụ, Models và Repository Interfaces.
- `data`: Triển khai Repository, Remote API (Retrofit) và MockData.
- `ui`: Màn hình Composable, Design System (Color, Shape, Type) và Navigation.
- `viewmodel`: Quản lý trạng thái UI và kết nối tầng Domain với UI.

## Hướng dẫn cài đặt

1. Tải mã nguồn từ GitHub.
2. Mở dự án bằng **Android Studio Ladybug (2024.2.1)** trở lên.
3. Đồng bộ Gradle và đợi tải các thư viện.
4. Chạy trên thiết bị có API Level tối thiểu 26.

---
*EzRoom - Thuê phòng nhanh, quản lý dễ.*
