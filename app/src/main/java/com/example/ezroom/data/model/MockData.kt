package com.example.ezroom.data.model

import java.util.UUID

object MockData {
    // Mock data
    val rooms = listOf(
        Room(
            id = "room_1",
            title = "Phòng trọ cao cấp ban công thoáng mát - Hải Châu",
            price = 3500000L,
            priceFormatted = "3.500.000₫",
            address = "Hải Châu, Đà Nẵng",
            detailedAddress = "123 Lê Lợi, P. Thạch Thang, Q. Hải Châu, Đà Nẵng",
            description = "Phòng rộng rãi, có ban công riêng, đầy đủ nội thất cơ bản. Gần các trường đại học lớn.",
            structure = RoomStructure.SINGLE,
            floorArea = 25.0,
            mezzanineArea = 0.0,
            detailedAreas = listOf(
                DetailedArea("da_1", "Phòng ngủ", 18.0),
                DetailedArea("da_2", "WC", 4.0),
                DetailedArea("da_3", "Ban công", 3.0)
            ),
            rating = 4.8f,
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(
                Amenity("WiFi", null),
                Amenity("Máy lạnh", null),
                Amenity("Máy giặt", null)
            ),
            status = RoomStatus.ACTIVE,
            latitude = 16.0678,
            longitude = 108.2208
        ),
        Room(
            id = "room_2",
            title = "Căn hộ Studio full nội thất - Ngũ Hành Sơn",
            price = 5500000L,
            priceFormatted = "5.500.000₫",
            address = "Ngũ Hành Sơn, Đà Nẵng",
            detailedAddress = "45 An Thượng 2, P. Mỹ An, Q. Ngũ Hành Sơn, Đà Nẵng",
            description = "Căn hộ hiện đại, thiết kế sang trọng, cách biển Mỹ Khê chỉ 5 phút đi bộ.",
            structure = RoomStructure.APARTMENT,
            floorArea = 35.0,
            mezzanineArea = 10.0,
            detailedAreas = listOf(
                DetailedArea("da_4", "Phòng khách & Bếp", 20.0),
                DetailedArea("da_5", "Gác ngủ", 10.0),
                DetailedArea("da_6", "WC", 5.0)
            ),
            rating = 4.5f,
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(
                Amenity("WiFi", null),
                Amenity("Thang máy", null),
                Amenity("Bảo vệ 24/7", null)
            ),
            status = RoomStatus.ACTIVE,
            latitude = 16.0475,
            longitude = 108.2435
        ),
        Room(
            id = "room_3",
            title = "Nhà nguyên căn 2 tầng giá rẻ - Thanh Khê",
            price = 8000000L,
            priceFormatted = "8.000.000₫",
            address = "Thanh Khê, Đà Nẵng",
            detailedAddress = "789 Điện Biên Phủ, Q. Thanh Khê, Đà Nẵng",
            description = "Nhà 2 tầng, phù hợp cho nhóm bạn hoặc hộ gia đình ở lâu dài. Khu vực an ninh.",
            structure = RoomStructure.WHOLE,
            floorArea = 60.0,
            mezzanineArea = 0.0,
            rating = 4.2f,
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(
                Amenity("Chỗ để xe", null),
                Amenity("Tủ lạnh", null)
            ),
            status = RoomStatus.ACTIVE,
            latitude = 16.0620,
            longitude = 108.1900
        ),
        Room(
            id = "room_4",
            title = "Phòng trọ sinh viên tiện nghi - Liên Chiểu",
            price = 2000000L,
            priceFormatted = "2.000.000₫",
            address = "Liên Chiểu, Đà Nẵng",
            detailedAddress = "101 Tôn Đức Thắng, Q. Liên Chiểu, Đà Nẵng",
            description = "Phòng trọ giá sinh viên, gần Đại học Bách Khoa. Giờ giấc tự do.",
            structure = RoomStructure.SINGLE,
            floorArea = 18.0,
            mezzanineArea = 6.0,
            rating = 4.0f,
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(
                Amenity("WiFi", null),
                Amenity("Kệ bếp", null)
            ),
            status = RoomStatus.RENTED,
            latitude = 16.0750,
            longitude = 108.1530
        ),
        Room(
            id = "room_5",
            title = "Căn hộ mini 1PN cao cấp - Sơn Trà",
            price = 4500000L,
            priceFormatted = "4.500.000₫",
            address = "Sơn Trà, Đà Nẵng",
            detailedAddress = "22 Võ Văn Kiệt, Q. Sơn Trà, Đà Nẵng",
            description = "Căn hộ mới xây, đầy đủ thiết bị hiện đại. Gần cầu Rồng và trung tâm thành phố.",
            structure = RoomStructure.APARTMENT,
            floorArea = 28.0,
            mezzanineArea = 0.0,
            rating = 4.7f,
            images = listOf(RoomImage(resId = android.R.drawable.ic_menu_gallery)),
            amenities = listOf(
                Amenity("WiFi", null),
                Amenity("Điều hòa", null),
                Amenity("Nước nóng", null)
            ),
            status = RoomStatus.ACTIVE,
            latitude = 16.0610,
            longitude = 108.2350
        )
    )

    val appointments = listOf(
        Appointment(
            id = "app_1",
            roomId = "room_1",
            roomName = "Phòng trọ cao cấp Hải Châu",
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
            roomId = "room_2",
            roomName = "Căn hộ Studio Ngũ Hành Sơn",
            renterName = "Trần Thị B",
            renterPhone = "0987654321",
            hostName = "Nguyễn Văn Host",
            date = "26/05/2026",
            time = "15:30",
            note = "Check inbox giúp em.",
            status = AppointmentStatus.APPROVED
        ),
        Appointment(
            id = "app_3",
            roomId = "room_5",
            roomName = "Căn hộ mini Sơn Trà",
            renterName = "Phạm Văn C",
            renterPhone = "0123456789",
            hostName = "Trần Thị Host",
            date = "24/05/2026",
            time = "10:00",
            note = "Bận việc đột xuất không đi được.",
            status = AppointmentStatus.CANCELED
        )
    )

    val invoices = listOf(
        Invoice(
            id = "INV-001",
            roomId = "room_1",
            roomName = "Phòng 101 - Hải Châu",
            period = "05/2026",
            roomPrice = 3500000L,
            oldElectricity = 1200,
            newElectricity = 1350,
            oldWater = 400,
            newWater = 415,
            otherCosts = 50000L,
            status = InvoiceStatus.UNPAID,
            dateCreated = "10/05/2026"
        ),
        Invoice(
            id = "INV-002",
            roomId = "room_2",
            roomName = "Phòng 202 - Ngũ Hành Sơn",
            period = "05/2026",
            roomPrice = 5500000L,
            oldElectricity = 800,
            newElectricity = 920,
            oldWater = 200,
            newWater = 210,
            otherCosts = 100000L,
            status = InvoiceStatus.UNPAID,
            dateCreated = "12/05/2026"
        ),
        Invoice(
            id = "DEP-001",
            roomId = "room_5",
            roomName = "Căn hộ Sơn Trà",
            period = "Cọc giữ chỗ",
            roomPrice = 2000000L,
            oldElectricity = 0,
            newElectricity = 0,
            oldWater = 0,
            newWater = 0,
            otherCosts = 0L,
            status = InvoiceStatus.PAID,
            dateCreated = "01/05/2026",
            paymentMethod = "VNPAY"
        )
    )
}
