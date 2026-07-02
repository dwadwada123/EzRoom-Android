# Ứng dụng EzRoom Android

EzRoom là giải pháp di động toàn diện cho việc quản lý thuê và cho thuê phòng trọ, phục vụ cả người thuê và chủ nhà. Ứng dụng tập trung vào tính minh bạch, hiệu quả và trải nghiệm người dùng cao cấp.

## Tổng quan dự án

Ứng dụng EzRoom Android được xây dựng dựa trên các tiêu chuẩn phát triển Android hiện đại nhất. Giao diện được thiết kế hiện đại, tinh tế với các hiệu ứng Glassmorphism (kính mờ) và bố cục Bento Grid. Dự án tuân thủ nghiêm ngặt nguyên lý Kiến trúc sạch (Clean Architecture) để đảm bảo khả năng mở rộng và bảo trì lâu dài.

## Các tính năng chính

### Dành cho người thuê (Renter)
- Khám phá phòng trọ: Tìm kiếm và lọc nâng cao theo vị trí (Tỉnh/Thành, Quận/Huyện), khoảng giá và các tiện ích đi kèm.
- Danh sách yêu thích: Lưu trữ và quản lý các phòng trọ ưng ý với tính năng bỏ yêu thích nhanh.
- Đặt lịch hẹn: Chủ động đặt lịch xem phòng với chủ nhà thông qua biểu mẫu chi tiết.
- Quản lý hóa đơn: Theo dõi danh sách hóa đơn hàng tháng, kiểm tra chi tiết các khoản phí dịch vụ và trạng thái thanh toán.
- Báo cáo và Đánh giá: Gửi báo cáo vi phạm bài đăng hoặc viết đánh giá chất lượng phòng sau khi thuê.
- Trò chuyện trực tiếp: Giao tiếp trực tiếp với chủ nhà thông qua hệ thống nhắn tin nội bộ.

### Dành cho chủ nhà (Host)
- Bảng điều khiển (Dashboard): Thống kê doanh thu thực tế, tỉ lệ lấp đầy phòng và các nhiệm vụ cần xử lý ngay trong ngày.
- Quản lý bất động sản: Hỗ trợ quản lý theo mô hình Dãy trọ/Tòa nhà (nhiều phòng) hoặc các căn nhà cho thuê lẻ độc lập.
- Xử lý lịch hẹn: Phê duyệt, hủy hoặc đề xuất hẹn lại thời gian xem phòng với khách thuê.
- Hợp đồng điện tử: Soạn thảo, gửi và ký kết hợp đồng thuê phòng trực tuyến ngay trên ứng dụng.
- Lập hóa đơn tự động: Hệ thống tự động tính toán tiền điện, nước dựa trên chỉ số cũ/mới và đơn giá đã cài đặt, sau đó gửi trực tiếp cho người thuê.
- Hệ thống uy tín: Đánh giá khách thuê và kiểm tra điểm tin cậy của người thuê trước khi giao kết hợp đồng.

## Công nghệ sử dụng

- Ngôn ngữ: Kotlin
- Giao diện: Jetpack Compose (100% Declarative UI)
- Kiến trúc: Clean Architecture (Phân tách rõ rệt 3 tầng: Domain, Data, UI)
- Mô hình thiết kế: MVVM (Model-View-ViewModel)
- Xử lý bất đồng bộ: Kotlin Coroutines và Flow
- Điều hướng: Jetpack Compose Navigation với hiệu ứng chuyển cảnh chuyên nghiệp
- Xử lý hình ảnh: Coil (hỗ trợ loading và caching hiệu quả)
- Mạng: Retrofit (đang trong quá trình tích hợp REST API)

## Cấu trúc thư mục chi tiết

- com.example.ezroom.core: Chứa các định nghĩa lỗi tập trung (AppError), các lớp bọc kết quả (Try/Result) và các tiện ích dùng chung toàn ứng dụng.
- com.example.ezroom.domain: Tầng trung tâm chứa logic nghiệp vụ thuần túy, bao gồm các Model dữ liệu, Interface của Repository và các Use Case xử lý logic.
- com.example.ezroom.data: Triển khai các Repository, nguồn dữ liệu từ Remote (API) hoặc Local (Database) và các lớp ánh xạ dữ liệu (Mapper).
- com.example.ezroom.ui: Tầng hiển thị bao gồm các Composable screen, hệ thống Design System (Color, Shape, Type) và logic điều hướng.
- com.example.ezroom.viewmodel: Quản lý trạng thái giao diện và kết nối tầng UI với tầng Domain.

## Quy tắc nghiệp vụ đặc thù

- Hoa hồng nền tảng: Hệ thống tự động trích 5% hoa hồng dựa trên Tiền thuê phòng cố định. Lưu ý: Không tính hoa hồng trên các khoản chi phí khác như điện, nước, tiền cọc hoặc tiền đền bù.
- Bảo mật dữ liệu: Các thao tác nhạy cảm như xóa phòng, xóa dãy trọ hoặc ký hợp đồng đều yêu cầu người dùng xác nhận thông qua hộp thoại cảnh báo nhiều bước.
- Phản hồi tức thì: Mọi thao tác thành công đều được thông báo qua Popup (Snackbar) ở phía trên cùng của màn hình để đảm bảo người dùng luôn nắm bắt được trạng thái ứng dụng.

## Hướng dẫn cài đặt và phát triển

### Yêu cầu hệ thống
- Android Studio Ladybug (phiên bản 2024.2.1) hoặc mới hơn.
- Java Development Kit (JDK) phiên bản 17.
- Android SDK tối thiểu: API Level 26 (Android 8.0).
- Android SDK mục tiêu: API Level 34 (Android 14).

### Các bước cài đặt chi tiết
1. Tải mã nguồn: Sử dụng lệnh git clone hoặc tải tệp .zip của dự án về máy cục bộ.
2. Mở dự án: Khởi động Android Studio và chọn "Open", sau đó tìm đến thư mục gốc của dự án EzRoom-Android.
3. Đồng bộ Gradle: Sau khi dự án được mở, hệ thống sẽ tự động yêu cầu đồng bộ. Nhấn "Sync Project with Gradle Files" và đợi quá trình tải các thư viện cần thiết hoàn tất.
4. Cấu hình thiết bị: Đảm bảo bạn đã cài đặt một thiết bị ảo (Emulator) hoặc kết nối thiết bị thật đã bật chế độ Developer Mode.
5. Chạy ứng dụng: Chọn module 'app' trên thanh công cụ và nhấn nút 'Run' (hình tam giác màu xanh).

### Lưu ý khi phát triển
- Ứng dụng sử dụng Edge-to-Edge hiển thị toàn màn hình, hãy lưu ý sử dụng statusBarsPadding hoặc navigationBarsPadding khi thêm các thành phần giao diện mới.
- Mọi thay đổi về dữ liệu cần được định nghĩa trước trong tầng Domain trước khi triển khai ở tầng Data.
