# EzRoom Android Technical Specification

Tài liệu này cung cấp đặc tả kỹ thuật chi tiết của ứng dụng EzRoom Android để đồng bộ hóa với hệ thống Web Admin.

---

## 1. Data Models (Domain Entities)

Dưới đây là danh sách đầy đủ các lớp dữ liệu được định nghĩa trong gói `com.example.ezroom.domain.model`.

### 1.1 Room & Property
#### **Property** (Dãy trọ / Tòa nhà)
- `id`: `String` - ID duy nhất của tòa nhà.
- `name`: `String` - Tên dãy trọ/tòa nhà.
- `type`: `PropertyType` - Loại hình (Dãy phức hợp hoặc Đăng tin lẻ).
- `address`: `String` - Địa chỉ tóm tắt (Quận, TP).
- `detailedAddress`: `String` - Địa chỉ chi tiết (Số nhà, tên đường).
- `description`: `String` - Mô tả chung.
- `commonAmenities`: `List<Amenity>` - Danh sách tiện ích chung.
- `images`: `List<RoomImage>` - Danh sách hình ảnh của tòa nhà.
- `latitude`: `Double` - Vĩ độ bản đồ.
- `longitude`: `Double` - Kinh độ bản đồ.
- `rooms`: `List<Room>` - Danh sách các phòng thuộc tòa nhà này.
- `isHidden`: `Boolean` - Trạng thái ẩn/hiện toàn bộ tòa nhà.

#### **Room** (Phòng trọ)
- `id`: `String` - ID duy nhất của phòng.
- `propertyId`: `String?` - ID của tòa nhà cha (null nếu là phòng lẻ).
- `title`: `String` - Tiêu đề bài đăng phòng.
- `price`: `Long` - Giá thuê theo tháng (VND).
- `priceFormatted`: `String` - Giá đã định dạng hiển thị.
- `electricityPrice`: `Long` - Đơn giá điện (đ/kWh). Default: 3500.
- `waterPrice`: `Long` - Đơn giá nước (đ/m³). Default: 15000.
- `address`: `String` - Địa chỉ hiển thị.
- `detailedAddress`: `String` - Vị trí cụ thể (VD: Tầng 2).
- `description`: `String` - Mô tả chi tiết phòng.
- `structure`: `RoomStructure` - Cấu trúc phòng (Phòng đơn, Nguyên căn, Căn hộ).
- `floorArea`: `Double` - Tổng diện tích (m²).
- `mezzanineArea`: `Double` - Diện tích gác lửng (m²).
- `detailedAreas`: `List<DetailedArea>` - Chi tiết các không gian thành phần.
- `rating`: `Float` - Điểm đánh giá trung bình.
- `reviewCount`: `Int` - Tổng số lượt đánh giá.
- `reviews`: `List<RoomReview>` - Danh sách các đánh giá của khách thuê.
- `images`: `List<RoomImage>` - Danh sách hình ảnh phòng.
- `amenities`: `List<Amenity>` - Danh sách tiện ích trong phòng.
- `status`: `RoomStatus` - Trạng thái phòng (Đang hiển thị, Đã thuê, Chờ duyệt).
- `latitude`: `Double` - Vĩ độ bản đồ.
- `longitude`: `Double` - Kinh độ bản đồ.
- `currentRenter`: `RenterInfo?` - Thông tin người đang thuê hiện tại.
- `pastRenters`: `List<RenterInfo>` - Lịch sử những người đã thuê trước đây.
- `isUserHidden`: `Boolean` - Trạng thái ẩn bài đăng do người dùng cài đặt.

#### **DetailedArea** (Chi tiết diện tích)
- `id`: `String`
- `roomName`: `String` - Tên không gian (VD: WC, Ban công).
- `areaValue`: `Double` - Diện tích tương ứng.

#### **Amenity** (Tiện ích)
- `name`: `String` - Tên tiện ích.
- `compensationAmount`: `Long` - Số tiền đền bù nếu làm hỏng.
- `iconRes`: `Int?` - Tài nguyên icon (nếu có).

### 1.2 User & Profile
#### **User** (Người dùng)
- `id`: `String`
- `name`: `String` - Họ và tên.
- `email`: `String`
- `phone`: `String`
- `avatarUrl`: `String?`
- `role`: `String` - Vai trò ("RENTER" hoặc "HOST").
- `isEkycVerified`: `Boolean` - Đã xác thực danh tính chưa.
- `creditScore`: `Float` - Điểm uy tín người dùng.

#### **RenterInfo** (Thông tin khách thuê)
- `id`: `String`
- `name`: `String`
- `phone`: `String`
- `avatarUrl`: `String?`
- `stayPeriod`: `String` - Khoảng thời gian ở (VD: "01/2024 - Hiện tại").
- `isCurrentlyStaying`: `Boolean` - Trạng thái đang ở hay đã rời đi.

### 1.3 Tài chính & Hợp đồng
#### **Invoice** (Hóa đơn)
- `id`: `String`
- `roomId`: `String`
- `roomName`: `String`
- `period`: `String` - Kỳ thanh toán (Tháng/Năm).
- `roomPrice`: `Long` - Tiền phòng cố định.
- `oldElectricity`: `Int` - Chỉ số điện cũ.
- `newElectricity`: `Int` - Chỉ số điện mới.
- `oldWater`: `Int` - Chỉ số nước cũ.
- `newWater`: `Int` - Chỉ số nước mới.
- `otherCosts`: `List<OtherCostItem>` - Các chi phí phát sinh khác.
- `status`: `InvoiceStatus` - Trạng thái (Đã thanh toán / Chưa thanh toán).
- `type`: `TransactionType` - Loại giao dịch.
- `dateCreated`: `String` - Ngày lập hóa đơn.
- `paymentMethod`: `String?` - Phương thức thanh toán (VNPAY, Tiền mặt...).

#### **OtherCostItem** (Mục phí phát sinh)
- `reason`: `String` - Lý do (Đền bù, Vệ sinh...).
- `amount`: `Long` - Số tiền.

#### **Contract** (Hợp đồng)
- `id`: `String`
- `roomId`: `String`
- `roomName`: `String`
- `renterName`: `String`
- `renterPhone`: `String`
- `startDate`: `String` - Ngày bắt đầu.
- `endDate`: `String` - Ngày kết thúc.
- `depositAmount`: `Long` - Số tiền đặt cọc.
- `depositStatus`: `DepositStatus` - Trạng thái cọc.
- `dateSigned`: `String?` - Ngày ký kết.

### 1.4 Chat & Tin nhắn
#### **Message**
- `id`: `String`
- `senderId`: `String`
- `text`: `String`
- `timestamp`: `Long`
- `isFromMe`: `Boolean`

#### **Conversation**
- `id`: `String`
- `otherPartyName`: `String`
- `lastMessage`: `String`
- `timestamp`: `String`
- `unreadCount`: `Int`
- `profileImage`: `String?`

### 1.5 Khác
#### **Appointment** (Lịch hẹn)
- `id`: `String`
- `roomId`: `String`
- `roomName`: `String`
- `renterName`: `String`
- `renterPhone`: `String`
- `hostName`: `String`
- `date`: `String`
- `time`: `String`
- `note`: `String`
- `status`: `AppointmentStatus`

#### **NotificationItem**
- `id`: `String`
- `title`: `String`
- `content`: `String`
- `time`: `String`
- `isRead`: `Boolean`
- `type`: `String` (appointment, invoice, contract...)

---

## 2. Enums & Constant States

### 2.1 Trạng thái Phòng & Bài đăng (`RoomStatus`)
- `ACTIVE`: "Đang hiển thị" - Bài đăng công khai.
- `RENTED`: "Đã cho thuê" - Phòng đã có người ở.
- `PENDING`: "Chờ duyệt" - Bài đăng mới chờ Admin phê duyệt.
- `HIDDEN`: "Đã ẩn bài" - Chủ nhà tự ẩn bài đăng.

### 2.2 Trạng thái Lịch hẹn (`AppointmentStatus`)
- `PENDING`: Đang chờ chủ nhà xác nhận.
- `APPROVED`: Đã được chấp nhận.
- `CANCELED`: Đã bị hủy (bởi khách hoặc chủ).
- `RESCHEDULED`: Chủ nhà đề xuất thời gian khác.

### 2.3 Trạng thái Hóa đơn (`InvoiceStatus`)
- `UNPAID`: Chưa thanh toán.
- `PAID`: Đã thanh toán xong.

### 2.4 Loại Giao dịch (`TransactionType`)
- `DEPOSIT`: Tiền cọc giữ chỗ.
- `RENT`: Tiền thuê phòng định kỳ.
- `COMPENSATION`: Tiền đền bù thiệt hại tài sản.

### 2.5 Loại Hình Bất động sản (`PropertyType`)
- `SINGLE`: Đăng tin lẻ (nhà nguyên căn, phòng đơn lẻ).
- `COMPLEX`: Quản lý dãy trọ / tòa nhà chung.

---

## 3. API Endpoints

Hiện tại ứng dụng đang trong quá trình chuyển đổi từ MockData sang REST API.

### 3.1 Location API (`LocationApi`)
- **Base URL**: Cấu hình trong `ApiConfig`.
- **GET** `/api/provinces`: Lấy danh sách Tỉnh/Thành phố kèm danh sách Phường/Xã trực thuộc.
  - *Response*: `List<Province>`

---

## 4. UI Architecture (Screens)

### 4.1 Người thuê (Renter)
1. **Discovery (Trang chủ)**: Tìm kiếm, lọc phòng theo khu vực, giá, tiện ích.
2. **Room Detail**: Chi tiết phòng, xem ảnh, vị trí bản đồ, tiện ích, đánh giá.
3. **Saved Rooms**: Danh sách phòng yêu thích, bỏ yêu thích nhanh.
4. **Booking Form**: Đặt lịch xem phòng với chủ nhà.
5. **Invoice List & Detail**: Xem danh sách hóa đơn và chi tiết thanh toán.
6. **Report/Review**: Gửi báo cáo vi phạm bài đăng hoặc viết đánh giá sau khi thuê.
7. **Profile & Reputation**: Quản lý thông tin cá nhân, xem điểm uy tín.

### 4.2 Chủ nhà (Host)
1. **Dashboard**: Tổng quan doanh thu, tỉ lệ lấp đầy, số phòng trống.
2. **Room Management**: Quản lý theo Tabs (Dãy trọ vs Phòng lẻ).
3. **Property/Room Forms**: Tạo mới hoặc chỉnh sửa Tòa nhà/Phòng trọ.
4. **Appointment Management**: Phê duyệt hoặc hẹn lại lịch xem phòng.
5. **Invoice Creation**: Lập hóa đơn hàng tháng dựa trên chỉ số điện, nước.
6. **Contract Creation**: Soạn thảo và gửi hợp đồng điện tử cho khách.
7. **EKYC Verified**: Xác thực danh tính để tăng uy tín.

---

## 5. Business Workflows (Luồng nghiệp vụ)

### 5.1 Quy trình Đặt lịch xem phòng
1. **Khách thuê** chọn phòng -> Bấm "Đặt lịch hẹn" -> Chọn ngày, giờ, ghi chú.
2. Hệ thống gửi thông báo cho **Chủ nhà**.
3. **Chủ nhà** vào mục Lịch hẹn:
   - **Xác nhận**: Lịch chuyển sang `APPROVED`.
   - **Hủy**: Lịch chuyển sang `CANCELED`.
   - **Hẹn lại**: Chủ nhà chọn thời gian mới, lịch chuyển sang `RESCHEDULED`.

### 5.2 Quy trình Tài chính & Hoa hồng
1. **Lập hóa đơn**: Chủ nhà nhập chỉ số Điện/Nước mới. Hệ thống tự tính tiền dựa trên đơn giá đã cài đặt ở bài đăng phòng.
2. **Tính toán**:
   - `Tiền phòng` (cố định).
   - `Tiền điện/nước` = (Số mới - Số cũ) * Đơn giá.
   - `Phí phát sinh` (Cọc, đền bù, vệ sinh...).
3. **Hoa hồng hệ thống (Commission)**:
   - Hệ thống tự động tính **5% hoa hồng** chỉ dựa trên `Tiền thuê phòng` (Room Price).
   - *Không* tính hoa hồng trên tiền điện, nước, cọc hay đền bù.
4. **Doanh thu thực nhận**: `Tổng tiền hóa đơn` - `Phí nền tảng (5% tiền phòng)`.

### 5.3 Quy trình Quản lý Bất động sản
- **Dãy trọ (Complex)**: Chủ nhà tạo thông tin Tòa nhà trước (Địa chỉ chung, tiện ích chung). Sau đó thêm từng Phòng vào tòa nhà đó. Xóa tòa nhà sẽ xóa tất cả phòng bên trong.
- **Phòng lẻ (Standalone)**: Chủ nhà đăng tin trực tiếp cho một căn nhà nguyên căn hoặc phòng đơn lẻ không thuộc một dãy quản lý nào.
- **Ẩn bài đăng**: Chủ nhà có thể tạm ẩn (`isUserHidden`) bài đăng mà không cần xóa, để bảo lưu dữ liệu khi chưa muốn cho thuê tiếp.

### 5.4 Quy trình Trao đổi & Tin nhắn
1. **Người thuê** bấm "Nhắn tin" từ trang chi tiết phòng.
2. Hệ thống mở màn hình Chat, tạo hội thoại (`Conversation`) mới nếu chưa tồn tại.
3. Tin nhắn được lưu vết theo thời gian thực (hiện tại đang sử dụng MockData, sẵn sàng tích hợp WebSocket).

---
*Tài liệu này được trích xuất tự động từ Codebase EzRoom Android v1.0.*
