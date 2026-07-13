package com.example.ezroom.data.model

import androidx.compose.runtime.mutableStateListOf
import com.example.ezroom.domain.model.*

object MockData {
    // Initial Properties
    val properties = mutableStateListOf(
        Property(
            id = "prop_1",
            name = "EzHome Hải Châu - Dãy trọ sinh viên",
            type = PropertyType.COMPLEX,
            address = "Hải Châu, Đà Nẵng",
            detailedAddress = "123 Lê Lợi, P. Thạch Thang, Q. Hải Châu, Đà Nẵng",
            description = "Dãy trọ mới xây, an ninh, gần các trường đại học. Có nhà xe chung, camera 24/7.",
            commonAmenities = listOf(
                Amenity("WiFi chung"),
                Amenity("Nhà xe"),
                Amenity("Camera an ninh"),
            ),
            latitude = 16.0678,
            longitude = 108.2208
        ),
        Property(
            id = "prop_2",
            name = "Nhà nguyên căn 2 tầng - Thanh Khê",
            type = PropertyType.SINGLE,
            address = "Thanh Khê, Đà Nẵng",
            detailedAddress = "789 Điện Biên Phủ, Q. Thanh Khê, Đà Nẵng",
            description = "Nhà 2 tầng, phù hợp cho hộ gia đình hoặc nhóm bạn ở lâu dài.",
            latitude = 16.0620,
            longitude = 108.1900
        ),
        Property(
            id = "prop_3",
            name = "Luxury Studio Building - Ngũ Hành Sơn",
            type = PropertyType.COMPLEX,
            address = "Ngũ Hành Sơn, Đà Nẵng",
            detailedAddress = "45 An Thượng 2, P. Mỹ An, Q. Ngũ Hành Sơn, Đà Nẵng",
            description = "Tòa nhà căn hộ cao cấp, cách biển 5 phút đi bộ. Dịch vụ dọn dẹp hàng tuần.",
            commonAmenities = listOf(
                Amenity("Thang máy"),
                Amenity("Bảo vệ 24/7"),
                Amenity("Hồ bơi tầng thượng")
            ),
            latitude = 16.0475,
            longitude = 108.2435
        ),
        Property(
            id = "prop_4",
            name = "Căn hộ dịch vụ Quận 7",
            type = PropertyType.COMPLEX,
            address = "Quận 7, TP.HCM",
            detailedAddress = "Số 10, Đường số 2, Tân Phong, Quận 7, TP.HCM",
            description = "Khu vực sầm uất, gần Vivo City, bảo vệ 24/24.",
            commonAmenities = listOf(
                Amenity("Phòng Gym"),
                Amenity("Sân thượng"),
                Amenity("Giặt sấy chung")
            ),
            latitude = 10.7291,
            longitude = 106.7022
        ),
        Property(
            id = "prop_5",
            name = "Chung cư mini Bình Thạnh",
            type = PropertyType.SINGLE,
            address = "Bình Thạnh, TP.HCM",
            detailedAddress = "456 Xô Viết Nghệ Tĩnh, P. 25, Bình Thạnh, TP.HCM",
            description = "Căn hộ studio riêng tư, đầy đủ nội thất cơ bản.",
            latitude = 10.8016,
            longitude = 106.7118
        )
    )

    // Initial Rooms linked to Properties
    val rooms = mutableStateListOf(
        // Belong to prop_1 (Complex)
        Room(
            id = "room_101",
            propertyId = "prop_1",
            title = "Phòng 101 - Ban công thoáng",
            price = 3500000L,
            priceFormatted = "3.500.000 đ",
            address = "Hải Châu, Đà Nẵng",
            detailedAddress = "Tầng 1, 123 Lê Lợi",
            description = "Phòng tầng trệt, có cửa sổ rộng.",
            structure = RoomStructure.SINGLE,
            floorArea = 22.0,
            detailedAreas = listOf(
                DetailedArea("da_101_1", "Phòng ngủ", 15.0),
                DetailedArea("da_101_2", "WC", 4.0),
                DetailedArea("da_101_3", "Ban công", 3.0)
            ),
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(Amenity("Máy lạnh"), Amenity("Tủ quần áo")),
            status = RoomStatus.ACTIVE,
            latitude = 16.0678,
            longitude = 108.2208,
            reviews = listOf(
                RoomReview("rev_101", "Nguyễn Văn A", null, 5, "Phòng rất sạch sẽ!", "12/05/2026", hostReply = "Cảm ơn bạn đã ủng hộ!")
            ),
            currentRenter = RenterInfo("u1", "Nguyễn Văn A", "0901234567", null, "01/2024 - Hiện tại", isCurrentlyStaying = true),
            pastRenters = listOf(
                RenterInfo("u2", "Trần Thị B", "0907654321", null, "01/2023 - 12/2023", isCurrentlyStaying = false),
            ),
        ),
        Room(
            id = "room_102",
            propertyId = "prop_1",
            title = "Phòng 102 - Tầng trệt",
            price = 3200000L,
            priceFormatted = "3.200.000 đ",
            address = "Hải Châu, Đà Nẵng",
            detailedAddress = "Tầng 1, 123 Lê Lợi",
            description = "Phòng cơ bản, sạch sẽ.",
            structure = RoomStructure.SINGLE,
            floorArea = 20.0,
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(Amenity("Quạt trần")),
            status = RoomStatus.ACTIVE,
            latitude = 16.0678,
            longitude = 108.2208
        ),
        Room(
            id = "room_103",
            propertyId = "prop_1",
            title = "Phòng 103 - Chờ duyệt",
            price = 3800000L,
            priceFormatted = "3.800.000 đ",
            address = "Hải Châu, Đà Nẵng",
            detailedAddress = "Tầng 2, 123 Lê Lợi",
            description = "Phòng mới sửa chữa xong.",
            structure = RoomStructure.SINGLE,
            floorArea = 25.0,
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(Amenity("Điều hòa")),
            status = RoomStatus.PENDING,
            latitude = 16.0678,
            longitude = 108.2208
        ),
        // Belong to prop_2 (Single)
        Room(
            id = "room_standalone_1",
            propertyId = "prop_2",
            title = "Nhà nguyên căn 2 tầng Điện Biên Phủ",
            price = 8000000L,
            priceFormatted = "8.000.000 đ",
            address = "Thanh Khê, Đà Nẵng",
            detailedAddress = "789 Điện Biên Phủ",
            description = "Toàn bộ căn nhà 2 tầng.",
            structure = RoomStructure.WHOLE,
            floorArea = 85.0,
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(Amenity("Bếp riêng"), Amenity("Sân để xe")),
            status = RoomStatus.ACTIVE,
            latitude = 16.0620,
            longitude = 108.1900
        ),
        // Belong to prop_3 (Complex)
        Room(
            id = "room_301",
            propertyId = "prop_3",
            title = "Studio 301 - View Biển",
            price = 6500000L,
            priceFormatted = "6.500.000 đ",
            address = "Ngũ Hành Sơn, Đà Nẵng",
            detailedAddress = "Tầng 3, 45 An Thượng 2",
            description = "Căn hộ view biển cực đẹp.",
            structure = RoomStructure.APARTMENT,
            floorArea = 35.0,
            detailedAreas = listOf(
                DetailedArea("da_301_1", "Phòng khách", 20.0),
                DetailedArea("da_301_2", "Gác lửng", 10.0),
                DetailedArea("da_301_3", "WC", 5.0)
            ),
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(Amenity("Full nội thất"), Amenity("Bếp điện")),
            status = RoomStatus.ACTIVE,
            latitude = 16.0475,
            longitude = 108.2435
        ),
        Room(
            id = "room_302",
            propertyId = "prop_3",
            title = "Studio 302 - Đang ẩn",
            price = 6500000L,
            priceFormatted = "6.500.000 đ",
            address = "Ngũ Hành Sơn, Đà Nẵng",
            detailedAddress = "Tầng 3, 45 An Thượng 2",
            description = "Căn hộ đang bảo trì máy lạnh.",
            structure = RoomStructure.APARTMENT,
            floorArea = 35.0,
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(Amenity("Full nội thất")),
            status = RoomStatus.ACTIVE,
            isUserHidden = true,
            latitude = 16.0475,
            longitude = 108.2435
        ),
        // Belong to prop_4 (Complex - TP.HCM)
        Room(
            id = "room_401",
            propertyId = "prop_4",
            title = "Phòng 401 - Cơ bản",
            price = 5500000L,
            priceFormatted = "5.500.000 đ",
            address = "Quận 7, TP.HCM",
            detailedAddress = "Tầng 4, Số 10 Đường số 2",
            description = "Phòng rộng, thoáng, ánh sáng tự nhiên.",
            structure = RoomStructure.SINGLE,
            floorArea = 30.0,
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(Amenity("Máy giặt"), Amenity("Tủ lạnh")),
            status = RoomStatus.ACTIVE,
            latitude = 10.7291,
            longitude = 106.7022
        ),
        Room(
            id = "room_402",
            propertyId = "prop_4",
            title = "Phòng 402 - Cao cấp (Gác lửng)",
            price = 7500000L,
            priceFormatted = "7.500.000 đ",
            address = "Quận 7, TP.HCM",
            detailedAddress = "Tầng 4, Số 10 Đường số 2",
            description = "Phòng có gác lửng rộng rãi, nội thất gỗ cao cấp.",
            structure = RoomStructure.APARTMENT,
            floorArea = 45.0,
            detailedAreas = listOf(
                DetailedArea("da_402_1", "Tầng trệt", 30.0),
                DetailedArea("da_402_2", "Gác lửng", 15.0)
            ),
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(Amenity("Sàn gỗ"), Amenity("Bếp hồng ngoại"), Amenity("Bàn làm việc")),
            status = RoomStatus.ACTIVE,
            latitude = 10.7291,
            longitude = 106.7022
        ),
        Room(
            id = "room_403",
            propertyId = "prop_4",
            title = "Phòng 403 - Đã cho thuê",
            price = 6000000L,
            priceFormatted = "6.000.000 đ",
            address = "Quận 7, TP.HCM",
            detailedAddress = "Tầng 4, Số 10 Đường số 2",
            description = "Phòng tương tự 401 nhưng view nhìn ra công viên.",
            structure = RoomStructure.SINGLE,
            floorArea = 32.0,
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(Amenity("Máy lạnh"), Amenity("Máy nước nóng")),
            status = RoomStatus.RENTED,
            latitude = 10.7291,
            longitude = 106.7022
        ),
        Room(
            id = "room_404",
            propertyId = "prop_4",
            title = "Phòng 404 - Studio hiện đại",
            price = 8500000L,
            priceFormatted = "8.500.000 đ",
            address = "Quận 7, TP.HCM",
            detailedAddress = "Tầng 5, Số 10 Đường số 2",
            description = "Phòng Studio phong cách minimalist, đầy đủ tiện nghi cho người đi làm.",
            structure = RoomStructure.APARTMENT,
            floorArea = 40.0,
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(Amenity("Smart TV"), Amenity("Tủ lạnh Side-by-side"), Amenity("Khóa từ")),
            status = RoomStatus.ACTIVE,
            latitude = 10.7291,
            longitude = 106.7022
        ),
        Room(
            id = "room_removed_1",
            propertyId = "prop_1",
            title = "Phòng 104 - Vi phạm quy định",
            price = 4000000L,
            priceFormatted = "4.000.000 đ",
            address = "Hải Châu, Đà Nẵng",
            detailedAddress = "Tầng 2, 123 Lê Lợi",
            description = "Phòng bị gỡ do hình ảnh không đúng thực tế.",
            structure = RoomStructure.SINGLE,
            floorArea = 25.0,
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(Amenity("Điều hòa")),
            status = RoomStatus.REMOVED,
            latitude = 16.0678,
            longitude = 108.2208,
            removalInfo = RoomRemovalInfo(
                reason = "Hình ảnh phòng không đúng thực tế, có dấu hiệu sao chép từ nguồn khác.",
                removedDate = "10/07/2026",
                autoDeleteDate = "17/07/2026"
            )
        ),
        // Standalone Rooms
        Room(
            id = "room_standalone_2",
            propertyId = null,
            title = "Phòng trọ lẻ giá rẻ - Ngũ Hành Sơn",
            price = 1500000L,
            priceFormatted = "1.500.000 đ",
            address = "Ngũ Hành Sơn, Đà Nẵng",
            detailedAddress = "Kiệt 12 Võ Nguyên Giáp",
            description = "Phòng trọ sinh viên.",
            structure = RoomStructure.SINGLE,
            floorArea = 15.0,
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(Amenity("Giá rẻ")),
            status = RoomStatus.ACTIVE,
            latitude = 16.0475,
            longitude = 108.2435
        ),
        Room(
            id = "room_standalone_rented",
            propertyId = null,
            title = "Nhà nguyên căn đã thuê (Cẩm Lệ)",
            price = 12000000L,
            priceFormatted = "12.000.000 đ",
            address = "Cẩm Lệ, Đà Nẵng",
            detailedAddress = "45 Nguyễn Hữu Thọ",
            description = "Nhà nguyên căn full nội thất.",
            structure = RoomStructure.WHOLE,
            floorArea = 100.0,
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(Amenity("Sân vườn")),
            status = RoomStatus.RENTED,
            latitude = 16.0200,
            longitude = 108.2100,
            currentRenter = RenterInfo("u3", "Lê Minh C", "0912345678", null, "03/2024 - Hiện tại", true)
        )
    )

    val appointments = mutableStateListOf(
        Appointment(
            id = "app_1",
            roomId = "room_101",
            roomName = "Phòng 101 - EzHome Hải Châu",
            renterName = "Nguyễn Văn A",
            renterPhone = "0901234567",
            hostName = "Lê Văn Chủ",
            date = "25/05/2026",
            time = "09:00",
            note = "Mình muốn xem phòng vào buổi sáng.",
            status = AppointmentStatus.PENDING
        ),
        Appointment(
            id = "app_2",
            roomId = "room_301",
            roomName = "Studio 301 - Luxury Building",
            renterName = "Nguyễn Văn A",
            renterPhone = "0901234567",
            hostName = "Trần Vũ Phong",
            date = "28/05/2026",
            time = "15:30",
            note = "Em muốn xem phòng để cọc luôn ạ.",
            status = AppointmentStatus.APPROVED
        ),
        Appointment(
            id = "app_3",
            roomId = "room_standalone_1",
            roomName = "Nhà nguyên căn Thanh Khê",
            renterName = "Trần Thị B",
            renterPhone = "0907654321",
            hostName = "Phạm Thị Chủ",
            date = "30/05/2026",
            time = "10:00",
            note = "Chủ nhà hẹn lại lịch này.",
            status = AppointmentStatus.RESCHEDULED
        ),
        Appointment(
            id = "app_4",
            roomId = "room_401",
            roomName = "Phòng 401 - Q7",
            renterName = "Hoàng Văn D",
            renterPhone = "0988888888",
            hostName = "Nguyễn Văn A",
            date = "01/06/2026",
            time = "14:00",
            note = "Khách đã hủy do tìm được chỗ khác.",
            status = AppointmentStatus.CANCELED
        )
    )

    val invoices = mutableStateListOf(
        Invoice(
            id = "INV-001",
            roomId = "room_101",
            roomName = "Phòng 101 - Hải Châu",
            period = "05/2026",
            roomPrice = 3500000L,
            oldElectricity = 1200,
            newElectricity = 1350,
            oldWater = 400,
            newWater = 415,
            otherCosts = listOf(OtherCostItem("Phí vệ sinh hành lang", 50000L)),
            status = InvoiceStatus.UNPAID,
            dateCreated = "10/05/2026"
        ),
        Invoice(
            id = "INV-002",
            roomId = "room_standalone_rented",
            roomName = "Nhà nguyên căn Cẩm Lệ",
            period = "04/2026",
            roomPrice = 12000000L,
            oldElectricity = 500,
            newElectricity = 700,
            oldWater = 100,
            newWater = 120,
            status = InvoiceStatus.PAID,
            dateCreated = "05/04/2026",
            paymentMethod = "Chuyển khoản"
        )
    )

    val renterReviews = listOf(
        RenterReview(
            id = "rr_1",
            hostName = "Lê Văn Chủ",
            rating = 5,
            tags = listOf("Thanh toán đúng hạn", "Giữ gìn vệ sinh"),
            comment = "Khách thuê rất lịch sự, phòng khi trả lại rất sạch sẽ.",
            date = "15/04/2026",
            renterReply = "Cảm ơn chú, phòng chú rất tốt ạ!"
        ),
        RenterReview(
            id = "rr_2",
            hostName = "Trần Thị Host",
            rating = 2,
            tags = listOf("Tuân thủ nội quy"),
            comment = "Khách thường xuyên dẫn bạn về quá giờ quy định.",
            date = "10/02/2026"
        )
    )

    val contracts = mutableStateListOf(
        Contract(
            id = "CON-001",
            roomId = "room_101",
            roomName = "Phòng 101 - EzHome Hải Châu",
            renterName = "Nguyễn Văn A",
            renterPhone = "0901234567",
            hostName = "Lê Văn Chủ",
            startDate = "01/08/2026",
            endDate = "01/08/2027",
            depositAmount = 3500000L,
            depositStatus = DepositStatus.FROZEN,
            status = ContractStatus.ACTIVE,
            dateCreated = "20/06/2026",
            dateSigned = "21/06/2026",
            isProtected = true,
            disburseDate = "01/08/2026"
        ),
        Contract(
            id = "CON-002",
            roomId = "room_301",
            roomName = "Studio 301 - Luxury Building",
            renterName = "Nguyễn Văn A",
            renterPhone = "0901234567",
            hostName = "Trần Vũ Phong",
            startDate = "15/08/2026",
            endDate = "15/08/2027",
            depositAmount = 6500000L,
            depositStatus = DepositStatus.UNPAID,
            status = ContractStatus.WAITING_DEPOSIT,
            dateCreated = "10/07/2026",
            dateSigned = "11/07/2026"
        )
    )

    var currentUser = User(
        id = "u1",
        name = "Nguyễn Văn A",
        email = "nguyenvana@gmail.com",
        phone = "0987654321",
        avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?q=80&w=200",
        role = "HOST",
        isEkycVerified = false,
        creditScore = 4.8f
    )

    val conversations = mutableStateListOf(
        Conversation(
            id = "conv_1",
            otherPartyName = "Lê Văn Chủ",
            lastMessage = "Dạ vâng, hẹn gặp bạn lúc 9h.",
            timestamp = "10:30 AM",
            unreadCount = 0
        ),
        Conversation(
            id = "conv_2",
            otherPartyName = "Trần Vũ Phong",
            lastMessage = "Phòng này còn trống không ạ?",
            timestamp = "Hôm qua",
            unreadCount = 2
        ),
        Conversation(
            id = "conv_3",
            otherPartyName = "Hoàng Văn D",
            lastMessage = "Cảm ơn anh đã hỗ trợ.",
            timestamp = "01/06",
            unreadCount = 0
        )
    )

    val messages = mutableStateListOf(
        Message("m1", "host_1", "Chào bạn, mình là chủ phòng 101.", 1717200000000L, false),
        Message("m2", "me", "Chào chú, con muốn xem phòng ạ.", 1717200600000L, true),
        Message("m3", "host_1", "Dạ vâng, hẹn gặp bạn lúc 9h.", 1717201200000L, false)
    )

    val notifications = mutableStateListOf(
        NotificationItem(
            id = "n1",
            title = "Lịch hẹn mới",
            content = "Bạn có một lịch hẹn xem phòng mới vào ngày 25/05.",
            time = "2 giờ trước",
            isRead = false,
            type = "appointment"
        ),
        NotificationItem(
            id = "n2",
            title = "Hóa đơn tháng 05",
            content = "Hóa đơn tiền phòng tháng 05 đã được lập.",
            time = "1 ngày trước",
            isRead = true,
            type = "invoice"
        ),
        NotificationItem(
            id = "n3",
            title = "Hợp đồng đã ký",
            content = "Hợp đồng thuê nhà nguyên căn Cẩm Lệ đã được ký kết thành công.",
            time = "3 ngày trước",
            isRead = true,
            type = "contract"
        ),
        NotificationItem(
            id = "n4",
            title = "Hợp đồng mới cần ký",
            content = "Chủ trọ Trần Vũ Phong đã gửi hợp đồng cho Studio 301. Vui lòng kiểm tra và ký kết.",
            time = "Vừa xong",
            isRead = false,
            type = "CONTRACT",
            targetId = "CON-002"
        )
    )

    val paymentAccounts = mutableStateListOf<PaymentAccount>()
}
