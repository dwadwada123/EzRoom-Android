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
}
