# BẢNG PHÂN CÔNG CÔNG VIỆC DỰ ÁN EZROOM

Hệ thống Nền tảng Quản lý và Cho thuê Phòng trọ EzRoom

---

## 1. BẢNG TỔNG HỢP PHÂN CÔNG CÔNG VIỆC

| STT | Thành viên | Vai trò phụ trách | Phân hệ phụ trách | Nhiệm vụ & Chức năng chi tiết | Kết quả bàn giao |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1** | **PhongTV** | Lập trình viên Android | Ứng dụng di động Android (Kiến trúc nền tảng, Xác thực, Khám phá & Tương tác) | - Thiết lập kiến trúc tổng thể ứng dụng Android theo mô hình Clean Architecture (Domain - Data - Presentation) kết hợp MVVM, Hilt DI và Jetpack Compose.<br>- Xây dựng module Xác thực & Tài khoản: Màn hình Chào mừng (Splash), Đăng ký tài khoản (Khách & Chủ), Đăng nhập, Quên mật khẩu OTP email và Đặt lại mật khẩu.<br>- Xây dựng module Hồ sơ cá nhân & Cài đặt: Cập nhật thông tin người dùng, đổi mật khẩu và quản lý phiên đăng nhập (JWT Token) bằng Jetpack DataStore.<br>- Xây dựng Trung tâm Thông báo: Nhận và hiển thị danh sách thông báo hệ thống về biến động lịch hẹn, trạng thái hợp đồng và hóa đơn tiền phòng.<br>- Xây dựng module Khám phá & Tìm kiếm phòng trọ: Tích hợp dữ liệu địa giới hành chính ngoại tuyến; Bộ lọc nâng cao theo giá, diện tích, tiện ích và quét bán kính GPS.<br>- Xây dựng module Chi tiết phòng & Phòng yêu thích: Hiển thị thư viện ảnh, thông tin chủ trọ, tiện nghi, vị trí Google Maps và tính năng lưu (thả tim) phòng yêu thích.<br>- Xây dựng module Đặt lịch hẹn & Nhắn tin trực tiếp: Người thuê gửi lịch xem phòng; Nhắn tin trò chuyện kèm gửi ảnh Cloudinary, chia sẻ tọa độ GPS và nút gọi nhanh. | - Mã nguồn và giao diện các phân hệ Kiến trúc nền tảng, Xác thực, Hồ sơ cá nhân, Thông báo, Khám phá, Tìm kiếm, Chi tiết phòng, Phòng yêu thích, Đặt lịch hẹn và Nhắn tin trên Android. |
| **2** | **TàiPĐA** | Lập trình viên Android | Ứng dụng di động Android (Phân hệ Giao dịch, Hợp đồng & Hóa đơn Người thuê) | - Xây dựng module Đặt cọc giữ chỗ: Tích hợp cổng thanh toán trực tuyến PayOS với cơ chế giữ tiền cọc ký quỹ an toàn, bảo đảm quyền lợi trước khi nhận phòng.<br>- Xây dựng module Hợp đồng điện tử phía Người thuê: Xem chi tiết điều khoản hợp đồng và thực hiện ký kết hợp đồng điện tử hai bên trực tiếp trên ứng dụng.<br>- Xây dựng module Xuất tài liệu hợp đồng PDF: Tích hợp thư viện xử lý và kết xuất văn bản hợp đồng hoàn chỉnh ra định dạng PDF để người dùng tải về máy.<br>- Xây dựng module Khiếu nại & Tranh chấp cọc: Xây dựng chức năng gửi yêu cầu hoàn cọc hoặc khiếu nại tranh chấp lên Admin nếu chủ trọ vi phạm cam kết bàn giao.<br>- Xây dựng module Quản lý & Thanh toán Hóa đơn: Hiển thị danh sách hóa đơn hàng tháng, chi tiết số điện nước (cũ/mới) và quét mã QR thanh toán nhanh qua VietQR.<br>- Xây dựng module Đánh giá chất lượng phòng trọ: Cho phép người thuê chấm điểm (1-5 sao) và để lại nhận xét trải nghiệm sau khi thanh lý hợp đồng thuê.<br>- Xây dựng module Báo cáo vi phạm & Xem điểm uy tín: Xây dựng chức năng gửi báo cáo bài đăng/đánh giá vi phạm và màn hình theo dõi điểm uy tín cá nhân. | - Mã nguồn và giao diện các phân hệ Đặt cọc giữ chỗ, Ký hợp đồng điện tử, Xuất file PDF, Khiếu nại cọc, Thanh toán hóa đơn, Đánh giá chất lượng và Báo cáo vi phạm trên Android. |
| **3** | **DũngTLQ** | Lập trình viên Android | Ứng dụng di động Android (Phân hệ Nghiệp vụ & Vận hành Chủ trọ) | - Xây dựng Bảng điều khiển Chủ trọ: Theo dõi doanh thu thực nhận sau khi trừ 5% hoa hồng sàn, tỷ lệ lấp đầy phòng, số phòng đang cho thuê và phòng còn trống.<br>- Xây dựng module Quản lý cơ sở lưu trú: Hỗ trợ quản lý linh hoạt theo mô hình Khu trọ / Tòa nhà (tự động kế thừa địa chỉ) và Phòng trọ đơn lẻ; cơ chế xóa mềm.<br>- Xây dựng module Đăng tin & Nhân bản phòng trọ: Phát triển biểu mẫu đăng phòng, tải ảnh Cloudinary, thiết lập giá, tiện ích và tính năng Clone nhân bản dữ liệu nhanh.<br>- Xây dựng module Xác thực danh tính eKYC: Quy trình chụp/tải 3 ảnh (2 mặt CCCD và ảnh chân dung selfie), theo dõi trạng thái duyệt hoặc gửi lại hồ sơ khi bị từ chối.<br>- Xây dựng module Khởi tạo Hợp đồng thuê phòng: Soạn thảo hợp đồng cho khách thuê, thiết lập tiền cọc, tự động sinh mã PayOS và chuyển trạng thái phòng sang Đã thuê.<br>- Xây dựng module Lập Hóa đơn & Tính tiền tự động: Tạo hóa đơn định kỳ, nhập số điện nước mới/cũ để hệ thống tự động tính tổng tiền gửi đến khách thuê.<br>- Xây dựng module Quản lý lịch hẹn & Giải ngân cọc: Tiếp nhận lịch hẹn (Xác nhận, Từ chối, Dời lịch), gửi yêu cầu giải ngân cọc và gửi đơn kháng cáo bài đăng bị khóa. | - Mã nguồn và giao diện các phân hệ Bảng điều khiển, Quản lý cơ sở lưu trú, Đăng tin phòng trọ, Xác thực eKYC, Lập hợp đồng, Lập hóa đơn, Giải ngân cọc và Kháng cáo trên Android. |
| **4** | **TâmT** | Lập trình viên Web Frontend | Trang web Quản trị viên (EzRoom-Admin Web Portal) | - Xây dựng kiến trúc Web Admin: Khởi tạo dự án bằng React 19, Vite, Tailwind CSS, Ant Design và thiết lập cấu hình kết nối API tập trung của hệ thống.<br>- Xây dựng Bảng điều khiển Quản trị: Thống kê số liệu toàn hệ thống, tổng doanh thu thu từ mức 5% hoa hồng nền tảng và vẽ biểu đồ trực quan hóa dữ liệu qua Recharts.<br>- Xây dựng module Kiểm duyệt eKYC: Xem xét hồ sơ xác thực 3 ảnh CCCD và ảnh chân dung của chủ trọ, thực hiện phê duyệt hoặc từ chối kèm lý do cụ thể.<br>- Xây dựng module Kiểm duyệt Tin đăng: Giám sát danh sách phòng trọ mới, thực hiện phê duyệt bài đăng công khai hoặc khóa bài đăng vi phạm quy chế.<br>- Xây dựng module Phân xử Tranh chấp Tiền cọc: Đóng vai trò trọng tài tiếp nhận khiếu nại cọc, xem xét bằng chứng để đưa ra phán quyết Hoàn cọc hoặc Giải ngân.<br>- Xây dựng module Xử lý Báo cáo Vi phạm Đánh giá: Tiếp nhận khiếu nại về các bài đánh giá có nội dung tiêu cực/sai sự thật, duyệt xóa mềm hoặc bác bỏ khiếu nại.<br>- Xây dựng module Giám sát Dòng tiền, Hợp đồng & Tiện ích: Quản lý lịch sử giao dịch cọc ký quỹ, thanh toán hóa đơn, quản lý người dùng và danh mục tiện ích. | - Toàn bộ mã nguồn hoàn chỉnh của trang Web Quản trị viên (EzRoom-Admin Web Portal), các bảng điều khiển, giao diện kiểm duyệt và bản build production. |
| **5** | **NguyệtBTN** | Lập trình viên Backend & Cơ sở dữ liệu | Hệ thống máy chủ Backend & Cơ sở dữ liệu (EzRoom-Backend API) | - Thiết kế kiến trúc Backend & CSDL MongoDB: Thiết lập dự án Node.js, Express, TypeScript và định nghĩa Schema cấu trúc dữ liệu cho 12 bảng bằng Mongoose ODM.<br>- Xây dựng hệ thống API Xác thực & Người dùng: Hiện thực hóa các API Đăng ký, Đăng nhập JWT, Quên mật khẩu OTP, Hồ sơ cá nhân và Phân quyền truy cập tài khoản.<br>- Xây dựng hệ thống API Bất động sản & Phòng trọ: Hiện thực hóa các API Quản lý Tòa nhà, Phòng trọ (tìm kiếm GPS Haversine, lưu yêu thích, xóa mềm) và Tiện ích.<br>- Xây dựng hệ thống API Giao dịch & Hợp đồng: Xây dựng API Hợp đồng, Hóa đơn (tự tính hoa hồng 5%), Lịch hẹn (đề xuất dời lịch), Tin nhắn và Thông báo.<br>- Tích hợp Cổng thanh toán PayOS: Xử lý Webhook thu hộ tiền cọc/hóa đơn và tích hợp dịch vụ Chi hộ Payout tự động giải ngân tiền cọc cho chủ trọ.<br>- Xây dựng Tác vụ nền tự động Cron-job: Lập lịch quét và giải ngân tiền cọc ký quỹ với trạng thái khóa bảo vệ FROZEN chống trùng lặp dữ liệu giao dịch.<br>- Tích hợp Dịch vụ Đám mây & Bảo mật: Tích hợp Cloudinary lưu trữ ảnh, Nodemailer gửi OTP Gmail, mã hóa bcryptjs, Rate-Limit chống brute-force và CORS. | - Toàn bộ mã nguồn hệ thống Backend API (Node.js/Express/TypeScript), cấu hình Cơ sở dữ liệu MongoDB Atlas và các dịch vụ tích hợp thanh toán, lưu trữ đám mây. |

---

## 2. CHI TIẾT PHÂN BỔ TRÁCH NHIỆM THEO TỪNG THÀNH VIÊN

### 1. PhongTV - Lập trình viên Android
- **Mục tiêu:** Xây dựng nền tảng kiến trúc ứng dụng di động vững chắc, hoàn thiện phân hệ Xác thực/Tài khoản, Hồ sơ cá nhân, Thông báo, Phòng yêu thích và mang lại trải nghiệm tìm kiếm, tương tác mượt mà cho Người thuê.
- **Nhiệm vụ cụ thể:**
  - Thiết lập kiến trúc tổng thể ứng dụng Android theo mô hình Clean Architecture (Domain - Data - Presentation) kết hợp MVVM, Hilt DI và luồng điều hướng Navigation Compose.
  - Xây dựng module Xác thực: Màn hình Chào mừng (Splash), Đăng ký tài khoản (hỗ trợ cả 2 vai trò Khách thuê & Chủ trọ), Đăng nhập, Quên mật khẩu OTP email và Đặt lại mật khẩu.
  - Xây dựng module Hồ sơ cá nhân (Profile): Cập nhật thông tin cá nhân, thay đổi mật khẩu, quản lý phiên đăng nhập và mã xác thực JWT Token bằng DataStore.
  - Xây dựng Trung tâm Thông báo: Nhận và hiển thị danh sách các thông báo hệ thống (nhắc nợ hóa đơn, cập nhật lịch hẹn, trạng thái hợp đồng).
  - Xây dựng giao diện trang chủ Người thuê và tính năng bộ lọc nâng cao (lọc theo giá, diện tích, kết cấu, tiện ích và quét bán kính GPS bằng công thức Haversine).
  - Phát triển tính năng xem chi tiết phòng trọ, thư viện hình ảnh, tiện nghi, bản đồ Google Maps và tính năng lưu (thả tim) phòng trọ yêu thích.
  - Phát triển tính năng đặt lịch hẹn xem phòng và màn hình trò chuyện trực tiếp với chủ trọ theo từng phòng (hỗ trợ gửi ảnh, định vị vị trí và nút gọi điện thoại nhanh).

### 2. TàiPĐA - Lập trình viên Android
- **Mục tiêu:** Hiện thực hóa các tính năng giao dịch tài chính an toàn, quản lý hợp đồng, thanh toán hóa đơn, khiếu nại cọc và đánh giá chất lượng cho Người thuê.
- **Nhiệm vụ cụ thể:**
  - Phát triển luồng đặt cọc giữ chỗ trực tuyến, hiển thị mã thanh toán PayOS và xử lý trạng thái giữ tiền cọc an toàn ký quỹ trung gian.
  - Xây dựng giao diện xem điều khoản chi tiết và thực hiện ký kết hợp đồng điện tử hai bên trực tiếp trên ứng dụng di động.
  - Tích hợp công cụ xuất tài liệu hợp đồng ra định dạng PDF để người dùng lưu trữ trực tiếp về bộ nhớ máy.
  - Xây dựng tính năng gửi yêu cầu hoàn cọc hoặc khiếu nại tranh chấp tiền cọc lên hệ thống nếu phát sinh vi phạm cam kết trước khi nhận phòng.
  - Xây dựng màn hình danh sách và chi tiết hóa đơn sinh hoạt hàng tháng, tích hợp cổng thanh toán VietQR và PayOS để quét mã thanh toán nhanh.
  - Xây dựng tính năng đánh giá chất lượng phòng trọ (thang điểm 1-5 sao) và để lại nhận xét trải nghiệm sau khi thanh lý hợp đồng thuê.
  - Xây dựng chức năng gửi báo cáo nội dung bài đăng/đánh giá vi phạm và màn hình theo dõi bảng điểm uy tín cá nhân.

### 3. DũngTLQ - Lập trình viên Android
- **Mục tiêu:** Xây dựng toàn diện bộ công cụ quản lý cơ sở lưu trú, hợp đồng, hóa đơn, giải ngân cọc và vận hành cho Chủ trọ.
- **Nhiệm vụ cụ thể:**
  - Phát triển Bảng điều khiển Chủ trọ trực quan theo dõi doanh thu thực nhận sau khi trừ 5% hoa hồng sàn, tỷ lệ lấp đầy phòng và số lượng phòng trống.
  - Xây dựng module quản lý cơ sở lưu trú theo 2 mô hình: Khu trọ / Tòa nhà (tự động kế thừa địa chỉ, tọa độ GPS) và Phòng trọ đơn lẻ; áp dụng cơ chế xóa mềm.
  - Phát triển biểu mẫu đăng tin phòng trọ, tải ảnh Cloudinary, thiết lập giá thuê, danh mục tiện ích và tính năng nhân bản (clone) phòng giúp nhập liệu nhanh.
  - Xây dựng quy trình chụp ảnh CCCD 2 mặt và ảnh chân dung selfie để gửi duyệt định danh eKYC; xem lý do từ chối và nộp lại hồ sơ.
  - Phát triển tính năng tạo hợp đồng thuê phòng điện tử cho khách thuê, thiết lập tiền cọc, tự động sinh mã PayOS và chuyển trạng thái phòng sang Đã thuê.
  - Phát triển tính năng lập hóa đơn sinh hoạt định kỳ tự động tính tiền điện nước theo chỉ số công tơ mới và cũ gửi đến khách thuê.
  - Xây dựng tính năng quản lý lịch hẹn xem phòng (Xác nhận, Từ chối hoặc Đề xuất dời lịch), gửi yêu cầu giải ngân tiền cọc ký quỹ và gửi đơn kháng cáo bài đăng bị khóa.

### 4. TâmT - Lập trình viên Web Frontend
- **Mục tiêu:** Xây dựng cổng thông tin quản trị trực quan, tiện lợi giúp Quản trị viên giám sát và kiểm duyệt toàn bộ hệ thống.
- **Nhiệm vụ cụ thể:**
  - Khởi tạo dự án Web Admin bằng React 19, Vite, cấu hình Tailwind CSS, Ant Design và thiết lập cấu hình kết nối API tập trung của hệ thống.
  - Thiết kế Bảng điều khiển Quản trị với các thẻ thống kê tổng quan và biểu đồ doanh thu thu từ mức 5% hoa hồng nền tảng bằng Recharts.
  - Phát triển giao diện kiểm duyệt hồ sơ định danh eKYC của chủ trọ (xem ảnh CCCD, ảnh chân dung, phê duyệt hoặc từ chối kèm lý do cụ thể).
  - Phát triển trang kiểm duyệt tin đăng phòng trọ mới và công cụ khóa/gỡ bỏ bài đăng vi phạm quy chuẩn hệ thống.
  - Xây dựng trung tâm giải quyết tranh chấp: Xử lý khiếu nại cọc ký quỹ giữa Khách thuê và Chủ trọ (Hoàn cọc hoặc Giải ngân) và xử lý báo cáo vi phạm đánh giá.
  - Xây dựng bảng theo dõi lịch sử giao dịch cọc ký quỹ, thanh toán hóa đơn, giám sát hợp đồng và theo dõi trạng thái giải ngân tiền cọc.
  - Xây dựng module quản lý người dùng (khóa/mở tài khoản vi phạm) và quản lý danh mục tiện ích phòng trọ / tòa nhà.

### 5. NguyệtBTN - Lập trình viên Backend & Cơ sở dữ liệu
- **Mục tiêu:** Xây dựng hệ thống Backend API mạnh mẽ, bảo mật, thiết kế cơ sở dữ liệu tối ưu và tích hợp toàn diện các dịch vụ thanh toán, lưu trữ, gửi mail.
- **Nhiệm vụ cụ thể:**
  - Thiết lập dự án Node.js, Express, TypeScript và thiết kế Schema cấu trúc dữ liệu cho 12 bảng trong MongoDB thông qua Mongoose ODM.
  - Xây dựng hệ thống API Xác thực & Người dùng: Đăng ký, Đăng nhập JWT, Quên mật khẩu OTP, Hồ sơ cá nhân và Phân quyền truy cập tài khoản.
  - Xây dựng hệ thống API Bất động sản & Phòng trọ: Quản lý Tòa nhà, Phòng trọ (tìm kiếm GPS Haversine, lưu yêu thích, xóa mềm) và Tiện ích.
  - Xây dựng hệ thống API Giao dịch & Hợp đồng: Hợp đồng, Hóa đơn (tự tính hoa hồng 5%), Lịch hẹn (đề xuất dời lịch), Tin nhắn và Thông báo.
  - Tích hợp cổng thanh toán PayOS: Xử lý Webhook thu hộ tiền cọc, hóa đơn và tích hợp dịch vụ Chi hộ Payout tự động giải ngân tiền cọc cho chủ trọ.
  - Xây dựng dịch vụ lập lịch nền Cron-job tự động giải ngân tiền cọc định kỳ với cơ chế khóa bảo vệ FROZEN chống trùng lặp giao dịch.
  - Tích hợp Cloudinary SDK lưu trữ ảnh, Nodemailer gửi OTP Gmail, cấu hình mã hóa bcryptjs, xác thực JWT, Rate-Limit và CORS.
