package com.example.ezroom.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.ezroom.domain.model.Invoice
import com.example.ezroom.domain.model.InvoiceStatus
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat

object PdfExporter {
    fun exportInvoicePdf(context: Context, invoice: Invoice) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val titlePaint = Paint().apply {
                color = Color.rgb(29, 78, 216) // Tech Blue
                textSize = 20f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }

            val textPaint = Paint().apply {
                color = Color.DKGRAY
                textSize = 12f
            }

            val boldPaint = Paint().apply {
                color = Color.BLACK
                textSize = 13f
                isFakeBoldText = true
            }

            val formatter = DecimalFormat("#,### đ")
            val totalAmount = invoice.calculatedTotalAmount

            var y = 60f
            canvas.drawText("HÓA ĐƠN THANH TOÁN TIỀN PHÒNG", 297.5f, y, titlePaint)

            y += 30f
            canvas.drawText("EzRoom Smart Rental Management", 297.5f, y, textPaint.apply { textAlign = Paint.Align.CENTER })
            textPaint.textAlign = Paint.Align.LEFT

            y += 40f
            canvas.drawLine(40f, y, 555f, y, textPaint)
            y += 25f

            canvas.drawText("Mã hóa đơn: ${invoice.id}", 40f, y, boldPaint)
            canvas.drawText("Kỳ thanh toán: ${invoice.period}", 350f, y, boldPaint)
            y += 20f
            canvas.drawText("Tên phòng: ${invoice.roomName}", 40f, y, textPaint)
            canvas.drawText("Người thuê: ${invoice.renterName}", 350f, y, textPaint)
            y += 20f
            canvas.drawText("Ngày lập: ${invoice.dateCreated}", 40f, y, textPaint)
            canvas.drawText("Trạng thái: ${if (invoice.status == InvoiceStatus.PAID) "ĐÃ THANH TOÁN" else "CHƯA THANH TOÁN"}", 350f, y, textPaint)

            y += 35f
            canvas.drawLine(40f, y, 555f, y, textPaint)
            y += 25f

            canvas.drawText("BẢNG KÊ CHI TIẾT KHOẢN TIỀN", 40f, y, boldPaint)
            y += 25f

            canvas.drawText("1. Tiền thuê phòng cố định:", 50f, y, textPaint)
            canvas.drawText(formatter.format(invoice.roomPrice), 450f, y, textPaint)
            y += 20f

            val elecUsage = (invoice.newElectricity - invoice.oldElectricity).coerceAtLeast(0)
            val elecCost = elecUsage * invoice.electricityPrice
            canvas.drawText("2. Tiền điện ($elecUsage kWh x ${formatter.format(invoice.electricityPrice)}):", 50f, y, textPaint)
            canvas.drawText(formatter.format(elecCost), 450f, y, textPaint)
            y += 20f

            val waterUsage = (invoice.newWater - invoice.oldWater).coerceAtLeast(0)
            val waterCost = waterUsage * invoice.waterPrice
            canvas.drawText("3. Tiền nước ($waterUsage m³ x ${formatter.format(invoice.waterPrice)}):", 50f, y, textPaint)
            canvas.drawText(formatter.format(waterCost), 450f, y, textPaint)
            y += 20f

            if (invoice.otherCosts.isNotEmpty()) {
                invoice.otherCosts.forEach { cost ->
                    canvas.drawText("• ${cost.reason}:", 60f, y, textPaint)
                    canvas.drawText(formatter.format(cost.amount), 450f, y, textPaint)
                    y += 20f
                }
            }

            y += 15f
            canvas.drawLine(40f, y, 555f, y, textPaint)
            y += 30f

            canvas.drawText("TỔNG CỘNG THANH TOÁN:", 40f, y, boldPaint.apply { textSize = 15f })
            canvas.drawText(formatter.format(totalAmount), 420f, y, boldPaint.apply { color = Color.rgb(16, 185, 129) })

            pdfDocument.finishPage(page)

            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.cacheDir
            if (!dir.exists()) dir.mkdirs()
            val safeId = invoice.id.replace(Regex("[^a-zA-Z0-9]"), "_")
            val file = File(dir, "EzRoom_Invoice_${safeId}.pdf")
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            val displayPath = file.absolutePath
            Toast.makeText(context, "📁 Đã xuất tệp PDF thành công!\nLưu tại: $displayPath", Toast.LENGTH_LONG).show()

            // Open PDF Viewer
            try {
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(intent, "Mở hóa đơn PDF (EzRoom)").apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
            } catch (ex: Exception) {
                // Ignore fallback
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi tạo tệp PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun exportContractPdf(context: Context, contract: com.example.ezroom.domain.model.Contract) {
        try {
            val pdfDocument = PdfDocument()
            val formatter = DecimalFormat("#,### đ")
            val safeId = contract.id.takeIf { it.isNotBlank() }?.replace(Regex("[^a-zA-Z0-9]"), "_") ?: "EZ_${System.currentTimeMillis()}"

            // Paints
            val titlePaint = Paint().apply {
                color = Color.rgb(29, 78, 216) // Tech Blue
                textSize = 15f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }

            val nationalHeaderPaint = Paint().apply {
                color = Color.BLACK
                textSize = 11f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }

            val nationalSubPaint = Paint().apply {
                color = Color.BLACK
                textSize = 10f
                textAlign = Paint.Align.CENTER
            }

            val sectionHeaderPaint = Paint().apply {
                color = Color.rgb(29, 78, 216)
                textSize = 10.5f
                isFakeBoldText = true
            }

            val textPaint = Paint().apply {
                color = Color.DKGRAY
                textSize = 9f
            }

            val boldTextPaint = Paint().apply {
                color = Color.BLACK
                textSize = 9.5f
                isFakeBoldText = true
            }

            val italicPaint = Paint().apply {
                color = Color.GRAY
                textSize = 8.5f
                textAlign = Paint.Align.CENTER
            }

            val borderPaint = Paint().apply {
                color = Color.LTGRAY
                strokeWidth = 0.8f
                style = Paint.Style.STROKE
            }

            val leftX = 40f
            val rightX = 555f
            val printWidth = 515f

            // Helper to draw wrapped text
            fun drawWrapped(canvas: android.graphics.Canvas, text: String, x: Float, startY: Float, paint: Paint, maxW: Float, lineH: Float): Float {
                var curY = startY
                val words = text.split(" ")
                var curLine = StringBuilder()
                for (w in words) {
                    val test = if (curLine.isEmpty()) w else "$curLine $w"
                    if (paint.measureText(test) <= maxW) {
                        curLine.append(if (curLine.isEmpty()) w else " $w")
                    } else {
                        canvas.drawText(curLine.toString(), x, curY, paint)
                        curY += lineH
                        curLine = StringBuilder(w)
                    }
                }
                if (curLine.isNotEmpty()) {
                    canvas.drawText(curLine.toString(), x, curY, paint)
                    curY += lineH
                }
                return curY
            }

            // ==================== TRANG 1 ====================
            val page1Info = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page1 = pdfDocument.startPage(page1Info)
            val canvas1 = page1.canvas

            var y1 = 45f
            canvas1.drawText("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", 297.5f, y1, nationalHeaderPaint)
            y1 += 16f
            canvas1.drawText("Độc lập – Tự do – Hạnh phúc", 297.5f, y1, nationalSubPaint)
            y1 += 8f
            canvas1.drawLine(240f, y1, 355f, y1, borderPaint)

            y1 += 24f
            canvas1.drawText("HỢP ĐỒNG THUÊ PHÒNG TRỌ", 297.5f, y1, titlePaint)
            y1 += 16f
            canvas1.drawText("(Mã số hợp đồng: ${contract.id.ifBlank { "HD-EZROOM-2026" }})", 297.5f, y1, italicPaint)
            y1 += 14f
            canvas1.drawLine(leftX, y1, rightX, y1, borderPaint)

            y1 += 18f
            canvas1.drawText("CĂN CỨ PHÁP LÝ GIAO KẾT:", leftX, y1, boldTextPaint)
            y1 += 14f
            canvas1.drawText("• Căn cứ Bộ luật Dân sự số 91/2015/QH13 ngày 24/11/2015;", leftX + 10f, y1, textPaint)
            y1 += 13f
            canvas1.drawText("• Căn cứ Luật Giao dịch điện tử số 20/2023/QH15 ngày 22/06/2023;", leftX + 10f, y1, textPaint)
            y1 += 13f
            canvas1.drawText("• Căn cứ Luật Nhà ở số 27/2023/QH15 và Luật Kinh doanh BĐS số 29/2023/QH15;", leftX + 10f, y1, textPaint)
            y1 += 13f
            canvas1.drawText("• Căn cứ Nghị định số 52/2013/NĐ-CP & Nghị định số 85/2021/NĐ-CP về Thương mại điện tử;", leftX + 10f, y1, textPaint)
            y1 += 13f
            canvas1.drawText("• Thông qua nền tảng ứng dụng công nghệ quản lý và thuê trọ trực tuyến EzRoom.", leftX + 10f, y1, textPaint)

            y1 += 20f
            canvas1.drawText("CHÚNG TÔI GỒM CÁC BÊN:", leftX, y1, boldTextPaint)
            y1 += 16f

            // Bên A
            canvas1.drawText("BÊN CHO THUÊ (BÊN A):", leftX, y1, sectionHeaderPaint)
            y1 += 14f
            canvas1.drawText("• Họ và tên: ${contract.hostName ?: "Chủ nhà"}", leftX + 10f, y1, boldTextPaint)
            canvas1.drawText("• Tư cách: Chủ cơ sở lưu trú được định danh trên EzRoom", 280f, y1, textPaint)
            y1 += 13f
            canvas1.drawText("• Địa chỉ phòng cho thuê: ${contract.address?.takeIf { it.isNotBlank() } ?: "Theo hồ sơ cơ sở lưu trú"}", leftX + 10f, y1, textPaint)

            y1 += 18f
            // Bên B
            canvas1.drawText("BÊN THUÊ (BÊN B):", leftX, y1, sectionHeaderPaint)
            y1 += 14f
            canvas1.drawText("• Họ và tên: ${contract.renterName}", leftX + 10f, y1, boldTextPaint)
            canvas1.drawText("• Số điện thoại định danh: ${contract.renterPhone}", 280f, y1, boldTextPaint)
            y1 += 13f
            canvas1.drawText("• Nơi ĐKTT: (Kê khai chi tiết theo hồ sơ làm thủ tục tạm trú khi nhận phòng)", leftX + 10f, y1, textPaint)

            y1 += 18f
            // Bên C
            canvas1.drawText("BÊN TRUNG GIAN NỀN TẢNG (BÊN C):", leftX, y1, sectionHeaderPaint)
            y1 += 14f
            canvas1.drawText("• Tên ứng dụng: Nền tảng Công nghệ Quản lý & Thuê trọ EzRoom", leftX + 10f, y1, textPaint)
            y1 += 13f
            canvas1.drawText("• Mã số thuế: 0123456789 | Đại diện: Trần Vũ Phong - Trưởng dự án", leftX + 10f, y1, textPaint)

            y1 += 22f
            canvas1.drawLine(leftX, y1, rightX, y1, borderPaint)
            y1 += 18f

            // Điều 1
            canvas1.drawText("ĐIỀU 1: THÔNG TIN TÀI SẢN THUÊ", leftX, y1, sectionHeaderPaint)
            y1 += 15f
            val d1Text = "Bên A đồng ý cho Bên B thuê phòng trọ: ${contract.roomName.ifBlank { "Phòng trọ" }}, tại địa chỉ: ${contract.address?.takeIf { it.isNotBlank() } ?: "Theo thông báo trên hệ thống EzRoom"}. Mục đích thuê: Dùng để ở, sinh hoạt văn minh. Thời hạn thuê từ ngày ${contract.startDate} đến ngày ${contract.endDate}."
            y1 = drawWrapped(canvas1, d1Text, leftX + 10f, y1, textPaint, printWidth - 10f, 13f)

            y1 += 12f
            // Điều 2
            canvas1.drawText("ĐIỀU 2: GIÁ THUÊ, TIỀN CỌC VÀ PHƯƠNG THỨC THANH TOÁN", leftX, y1, sectionHeaderPaint)
            y1 += 15f
            val d2Text = "1. Tiền đặt cọc bảo hộ (Escrow Deposit): Bên B đặt cọc số tiền ${formatter.format(contract.depositAmount)} thông qua cổng thanh toán bảo hộ EzRoom. Khoản tiền được đóng băng an toàn và chỉ giải ngân cho Bên A khi hợp đồng có hiệu lực.\n2. Thanh toán tiền phòng: Bên B có nghĩa vụ thanh toán tiền thuê phòng vào ngày 05 hàng tháng qua ứng dụng EzRoom."
            for (paragraph in d2Text.split("\n")) {
                y1 = drawWrapped(canvas1, paragraph, leftX + 10f, y1, textPaint, printWidth - 10f, 13f)
                y1 += 2f
            }

            y1 += 12f
            // Điều 3
            canvas1.drawText("ĐIỀU 3: PHÍ DỊCH VỤ NỀN TẢNG", leftX, y1, sectionHeaderPaint)
            y1 += 15f
            val d3Text = "Bên A đồng ý chi trả khoản phí kết nối giao dịch thành công bằng 5% tiền thuê theo chính sách nền tảng EzRoom. Khoản phí được khấu trừ tự động qua hệ thống khi thanh toán tiền phòng. Bên C có nghĩa vụ xuất hóa đơn điện tử hợp pháp cho Bên thanh toán theo quy định pháp luật."
            y1 = drawWrapped(canvas1, d3Text, leftX + 10f, y1, textPaint, printWidth - 10f, 13f)

            // Footer Trang 1
            canvas1.drawText("Hợp đồng điện tử EzRoom • Trang 1/2", 297.5f, 820f, italicPaint)
            pdfDocument.finishPage(page1)


            // ==================== TRANG 2 ====================
            val page2Info = PdfDocument.PageInfo.Builder(595, 842, 2).create()
            val page2 = pdfDocument.startPage(page2Info)
            val canvas2 = page2.canvas

            var y2 = 45f
            canvas2.drawText("HỢP ĐỒNG THUÊ PHÒNG TRỌ (Tiếp theo - Trang 2/2)", 297.5f, y2, titlePaint.apply { textSize = 12f })
            y2 += 14f
            canvas2.drawLine(leftX, y2, rightX, y2, borderPaint)

            y2 += 22f
            // Điều 4
            canvas2.drawText("ĐIỀU 4: QUYỀN VÀ NGHĨA VỤ CỦA CÁC BÊN", leftX, y2, sectionHeaderPaint)
            y2 += 16f
            val d4Text1 = "1. Quyền và nghĩa vụ của Bên A:\n- Giao phòng và các trang thiết bị kèm theo đúng tình trạng đã thỏa thuận.\n- Đảm bảo quyền sử dụng riêng tư trọn vẹn của Bên B. Bên A tuyệt đối KHÔNG được tự ý vào phòng thuê khi chưa thông báo trước ít nhất 24 giờ và chưa được sự đồng ý của Bên B (trừ các trường hợp khẩn cấp như hỏa hoạn, ngập lụt nguy hiểm đến tính mạng, tài sản).\n- Chịu trách nhiệm thực hiện thủ tục đăng ký tạm trú cho Bên B theo quy định pháp luật sau khi Bên B cung cấp đầy đủ giấy tờ tùy thân."
            for (p in d4Text1.split("\n")) {
                y2 = drawWrapped(canvas2, p, leftX + 10f, y2, textPaint, printWidth - 10f, 13.5f)
                y2 += 2f
            }

            y2 += 6f
            val d4Text2 = "2. Quyền và nghĩa vụ của Bên B:\n- Trả tiền thuê phòng và các khoản chi phí sinh hoạt phát sinh đầy đủ, đúng hạn.\n- Sử dụng phòng trọ đúng mục đích, giữ gìn an ninh trật tự, vệ sinh môi trường chung.\n- Không được tự ý sửa chữa, thay đổi kết cấu công trình hoặc cho thuê lại nếu chưa có sự đồng ý bằng văn bản của Bên A."
            for (p in d4Text2.split("\n")) {
                y2 = drawWrapped(canvas2, p, leftX + 10f, y2, textPaint, printWidth - 10f, 13.5f)
                y2 += 2f
            }

            y2 += 6f
            val d4Text3 = "3. Quyền và nghĩa vụ của Bên C (EzRoom):\n- Cung cấp hạ tầng ứng dụng vận hành ổn định để Bên A và Bên B ký kết hợp đồng, lưu trữ dữ liệu an toàn.\n- Đóng vai trò trung gian đối soát tài chính, phong tỏa và bảo vệ khoản tiền cọc minh bạch.\n- Phối hợp cung cấp dữ liệu giao dịch khi có tranh chấp hoặc khi có yêu cầu từ cơ quan chức năng có thẩm quyền."
            for (p in d4Text3.split("\n")) {
                y2 = drawWrapped(canvas2, p, leftX + 10f, y2, textPaint, printWidth - 10f, 13.5f)
                y2 += 2f
            }

            y2 += 14f
            // Điều 5
            canvas2.drawText("ĐIỀU 5: CHẤM DỨT HỢP ĐỒNG VÀ GIẢI QUYẾT TRANH CHẤP", leftX, y2, sectionHeaderPaint)
            y2 += 16f
            val d5Text = "1. Đơn phương chấm dứt: Bên muốn chấm dứt hợp đồng trước hạn phải thông báo trước cho bên kia ít nhất 30 ngày qua ứng dụng. Tiền cọc sẽ được xử lý tự động theo quy chế EzRoom Escrow.\n2. Giải quyết tranh chấp: Các bên ưu tiên thương lượng hòa bình thông qua sự hỗ trợ đối soát của Bên C. Trường hợp có hành vi vi phạm pháp luật hoặc bạo lực, Bên C có quyền trích xuất hồ sơ và trình báo cơ quan Công an xử lý theo quy định."
            for (p in d5Text.split("\n")) {
                y2 = drawWrapped(canvas2, p, leftX + 10f, y2, textPaint, printWidth - 10f, 13.5f)
                y2 += 2f
            }

            y2 += 25f
            canvas2.drawLine(leftX, y2, rightX, y2, borderPaint)
            y2 += 25f

            // KHUNG KÝ VÀ XÁC THỰC 3 BÊN
            val boxWidth = 160f
            val boxHeight = 100f

            // Bên A
            val aLeft = 40f
            canvas2.drawText("ĐẠI DIỆN BÊN A", aLeft + boxWidth / 2, y2, boldTextPaint.apply { textAlign = Paint.Align.CENTER })
            canvas2.drawText("(Chủ cho thuê)", aLeft + boxWidth / 2, y2 + 13f, italicPaint)
            val aBoxRect = android.graphics.RectF(aLeft, y2 + 20f, aLeft + boxWidth, y2 + 20f + boxHeight)
            canvas2.drawRoundRect(aBoxRect, 8f, 8f, borderPaint)
            canvas2.drawText("ĐÃ PHÁT HÀNH", aLeft + boxWidth / 2, y2 + 55f, boldTextPaint.apply { color = Color.rgb(29, 78, 216) })
            canvas2.drawText(contract.hostName ?: "Chủ nhà", aLeft + boxWidth / 2, y2 + 75f, boldTextPaint.apply { color = Color.BLACK })
            canvas2.drawText("Hệ thống EzRoom Host", aLeft + boxWidth / 2, y2 + 95f, italicPaint)

            // Bên B
            val bLeft = 217.5f
            canvas2.drawText("ĐẠI DIỆN BÊN B", bLeft + boxWidth / 2, y2, boldTextPaint.apply { color = Color.BLACK })
            canvas2.drawText("(Người thuê trọ)", bLeft + boxWidth / 2, y2 + 13f, italicPaint)
            val bBoxRect = android.graphics.RectF(bLeft, y2 + 20f, bLeft + boxWidth, y2 + 20f + boxHeight)
            canvas2.drawRoundRect(bBoxRect, 8f, 8f, borderPaint)
            if (!contract.dateSigned.isNullOrBlank() || contract.status != com.example.ezroom.domain.model.ContractStatus.WAITING_SIGN) {
                canvas2.drawText("✅ ĐÃ KÝ ĐIỆN TỬ", bLeft + boxWidth / 2, y2 + 50f, boldTextPaint.apply { color = Color.rgb(16, 185, 129) })
                canvas2.drawText(contract.renterName, bLeft + boxWidth / 2, y2 + 70f, boldTextPaint.apply { color = Color.BLACK })
                val signedDateStr = contract.dateSigned ?: contract.dateCreated
                canvas2.drawText("Ngày ký: $signedDateStr", bLeft + boxWidth / 2, y2 + 90f, italicPaint)
                canvas2.drawText("Xác thực: SĐT ${contract.renterPhone}", bLeft + boxWidth / 2, y2 + 105f, italicPaint)
            } else {
                canvas2.drawText("(Ký và ghi rõ họ tên)", bLeft + boxWidth / 2, y2 + 65f, italicPaint)
            }

            // Bên C
            val cLeft = 395f
            canvas2.drawText("ĐẠI DIỆN BÊN C", cLeft + boxWidth / 2, y2, boldTextPaint.apply { color = Color.BLACK })
            canvas2.drawText("(Nền tảng EzRoom)", cLeft + boxWidth / 2, y2 + 13f, italicPaint)
            val cBoxRect = android.graphics.RectF(cLeft, y2 + 20f, cLeft + boxWidth, y2 + 20f + boxHeight)
            canvas2.drawRoundRect(cBoxRect, 8f, 8f, borderPaint)
            canvas2.drawText("CHỨNG THỰC BỞI EZROOM", cLeft + boxWidth / 2, y2 + 50f, boldTextPaint.apply { color = Color.rgb(29, 78, 216); textSize = 8.5f })
            canvas2.drawText("Đại diện: Trần Vũ Phong", cLeft + boxWidth / 2, y2 + 70f, boldTextPaint.apply { textSize = 9.5f; color = Color.BLACK })
            canvas2.drawText("EzRoom Escrow Platform", cLeft + boxWidth / 2, y2 + 90f, italicPaint)
            boldTextPaint.textAlign = Paint.Align.LEFT

            // Footer Trang 2
            canvas2.drawText("Hợp đồng điện tử EzRoom • Trang 2/2", 297.5f, 820f, italicPaint)
            pdfDocument.finishPage(page2)

            // Save PDF
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.cacheDir
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "EzRoom_HopDong_${safeId}.pdf")
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            val displayPath = file.absolutePath
            Toast.makeText(context, "📁 Đã xuất Hợp đồng PDF thành công!\nLưu tại: $displayPath", Toast.LENGTH_LONG).show()

            // Open PDF
            try {
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(intent, "Mở Hợp đồng PDF (EzRoom)").apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
            } catch (ex: Exception) {
                // Ignore fallback
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi tạo tệp PDF Hợp đồng: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

