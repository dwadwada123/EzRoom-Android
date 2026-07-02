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
            title = "Phòng 103 - Đã cho thuê (Test)",
            price = 3800000L,
            priceFormatted = "3.800.000 đ",
            address = "Hải Châu, Đà Nẵng",
            detailedAddress = "Tầng 2, 123 Lê Lợi",
            description = "Phòng đã có khách thuê để test đánh giá.",
            structure = RoomStructure.SINGLE,
            floorArea = 25.0,
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(Amenity("Điều hòa")),
            status = RoomStatus.RENTED,
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
            id = "room_standalone_2",
            propertyId = null, // Truly standalone
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
            title = "Nhà nguyên căn đã thuê (Test Lẻ)",
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
            longitude = 108.2100
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
            renterName = "Nguyễn Văn A", // Common Renter Name for mock
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
            renterName = "Nguyễn Văn A",
            renterPhone = "0901234567",
            hostName = "Phạm Thị Chủ",
            date = "30/05/2026",
            time = "10:00",
            note = "Chủ nhà hẹn lại lịch này.",
            status = AppointmentStatus.RESCHEDULED
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

    val contracts = mutableStateListOf<Contract>()

    var currentUser = User(
        id = "u1",
        name = "Nguyễn Văn A",
        email = "nguyenvana@gmail.com",
        phone = "0987654321",
        avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?q=80&w=200",
        role = "HOST", // Changed to HOST to test eKYC flow as Host
        isEkycVerified = false, // Set to false to see eKYC screen
        creditScore = 4.5f
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
        )
    )
}
