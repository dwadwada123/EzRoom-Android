# EzRoom Android Application

Ứng dụng di động quản lý phòng trọ và cho thuê phòng dành cho Người thuê (Renter) và Chủ nhà (Host), được phát triển trên hệ điều hành Android bằng ngôn ngữ Kotlin và Jetpack Compose.

## 1. Công nghệ sử dụng

- Ngôn ngữ: Kotlin
- UI Framework: Jetpack Compose (Giao diện người dùng khai báo Declarative UI)
- Kiến trúc phần mềm: Clean Architecture (Domain, Data, Presentation) kết hợp mô hình MVVM
- Bất đồng bộ: Kotlin Coroutines & StateFlow
- Kết nối mạng: Retrofit 2 & OkHttp 3 (Giao tiếp RESTful API và VietQR API)
- Tải và xử lý hình ảnh: Coil Compose
- Quản lý phiên đăng nhập: EncryptedSharedPreferences (TokenManager)
- Điều hướng: Navigation Compose

## 2. Yêu cầu hệ thống để phát triển

- Android Studio: Phiên bản Ladybug (2024.2.1) hoặc mới hơn.
- Java Development Kit (JDK): Phiên bản 17.
- Android SDK: Min SDK 26 (Android 8.0), Target SDK 34 (Android 14).
- Server Backend EzRoom đang hoạt động (chạy cục bộ hoặc triển khai trên máy chủ).

## 3. Hướng dẫn cài đặt và khởi chạy

### Bước 1: Mở dự án
1. Khởi động Android Studio.
2. Chọn Open và dẫn tới thư mục chứa dự án EzRoom-Android.
3. Chờ Android Studio đồng bộ Gradle và tải toàn bộ dependencies cần thiết.

### Bước 2: Cấu hình địa chỉ kết nối API Server (ApiConfig.kt)
Để ứng dụng kết nối đúng với server backend của bạn khi thử nghiệm, mở file:
`app/src/main/java/com/example/ezroom/util/ApiConfig.kt`

Cập nhật các biến địa chỉ server tương ứng với thiết bị thử nghiệm của bạn:

- Chạy trên máy ảo (Android Emulator):
  Hệ điều hành Android Emulator sử dụng địa chỉ IP loopback đặc biệt `10.0.2.2` để trỏ về localhost của máy tính.
  ```kotlin
  private const val BASE_URL_EMULATOR = "http://10.0.2.2:3000/"
  ```

- Chạy trên thiết bị thật (Physical Device):
  Máy tính chạy Backend và điện thoại thử nghiệm cần kết nối vào cùng một mạng Wi-Fi nội bộ.
  Kiểm tra địa chỉ IP mạng nội bộ của máy tính (sử dụng lệnh `ipconfig` trên Windows hoặc `ifconfig` trên macOS/Linux) và điền vào biến `BASE_URL_DEVICE`:
  ```kotlin
  private const val BASE_URL_DEVICE = "http://<IP-MAY-TINH-CUA-BAN>:3000/"
  ```
  Ví dụ: `http://192.168.1.15:3000/`

- Triển khai Production:
  Thay thế bằng tên miền HTTPS chính thức của máy chủ backend (ví dụ: `https://api.ezroom.vn/`).

### Bước 3: Khởi chạy ứng dụng
1. Kết nối điện thoại thật qua cáp USB (đã bật chế độ USB Debugging) hoặc khởi động Máy ảo Android Emulator từ Android Studio.
2. Nhấn nút Run (hoặc nhấn tổ hợp phím Shift + F10) trên thanh công cụ để biên dịch và cài đặt ứng dụng lên thiết bị.

## 4. Các phân hệ tính năng chính

### A. Dành cho Người thuê (Renter)
- Tìm kiếm và lọc phòng trọ thông minh theo tỉnh thành, phường xã, khoảng giá và các tiện ích đi kèm.
- Xem chi tiết thông tin phòng, danh sách tiện nghi kèm mức đền bù nếu hư hỏng, thư viện hình ảnh và đánh giá từ khách thuê trước.
- Đặt cọc giữ chỗ trực tuyến an toàn thông qua cổng thanh toán PayOS với cơ chế bảo vệ tiền cọc Escrow.
- Xem nội dung và ký kết hợp đồng điện tử trực tiếp trên ứng dụng.
- Quản lý danh sách hóa đơn hàng tháng, xem chi tiết chỉ số điện nước và thanh toán nhanh qua mã QR.
- Đánh giá phòng, chấm điểm dịch vụ và gửi phản hồi uy tín cho chủ nhà.
- Nhắn tin trực tiếp với chủ trọ để trao đổi thông tin.

### B. Dành cho Chủ nhà (Host)
- Bảng điều khiển (Dashboard) trực quan theo dõi doanh thu thực nhận, tỷ lệ lấp đầy và số lượng phòng còn trống.
- Quản lý cơ sở lưu trú linh hoạt: Quản lý theo Dãy trọ / Tòa nhà phức hợp (Complex) hoặc Phòng đơn lẻ (Single).
- Đăng tin cho thuê phòng, cập nhật hình ảnh, giá thuê và các tiện ích đi kèm.
- Xác thực danh tính chủ nhà (eKYC) bằng ảnh Căn cước công dân (mặt trước, mặt sau) và ảnh chụp chân dung.
- Lập hợp đồng thuê phòng điện tử và quản lý tiến trình giải ngân tiền cọc.
- Lập hóa đơn sinh hoạt hàng tháng với công thức tính tiền điện nước tự động theo chỉ số công tơ.
- Gửi yêu cầu kháng cáo khi tin đăng phòng có tranh chấp hoặc bị hệ thống tạm khóa.
- Quản lý danh sách tài khoản ngân hàng thụ hưởng để nhận tiền giải ngân tự động.

## 5. Cấu trúc mã nguồn (Clean Architecture)

- `app/src/main/java/com/example/ezroom/domain/`: Chứa các thực thể nghiệp vụ (Entities), Models, Repository Interfaces và các UseCases độc lập.
- `app/src/main/java/com/example/ezroom/data/`: Triển khai các Repository, Retrofit Remote API services, DTOs và lưu trữ dữ liệu local.
- `app/src/main/java/com/example/ezroom/ui/`: Chứa các composable screens (Renter, Host, Auth), components tái sử dụng, theme giao diện và cấu hình navigation.
- `app/src/main/java/com/example/ezroom/viewmodel/`: Các ViewModel quản lý UI State, xử lý luồng sự kiện và kết nối logic nghiệp vụ từ domain layer.
- `app/src/main/java/com/example/ezroom/util/`: Chứa các lớp tiện ích cấu hình API (ApiConfig), quản lý Token đăng nhập, định dạng tiền tệ và xử lý thời gian.
