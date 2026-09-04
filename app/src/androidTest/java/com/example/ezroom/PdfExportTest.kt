package com.example.ezroom

import android.os.Environment
import androidx.core.content.FileProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ezroom.domain.model.*
import com.example.ezroom.util.PdfExporter
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class PdfExportTest {

    @Test
    fun testExportContractPdf() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val contract = Contract(
            id = "test_contract_12345",
            roomId = "room_1",
            roomName = "Phòng 301 - Tòa Nhà EzRoom",
            address = "123 Đường Nguyễn Trãi, Thanh Xuân, Hà Nội",
            renterName = "Nguyễn Văn A",
            renterPhone = "0987654321",
            hostName = "Trần Văn B",
            startDate = "01/09/2026",
            endDate = "01/09/2027",
            depositAmount = 3500000L,
            depositStatus = DepositStatus.FROZEN,
            status = ContractStatus.ACTIVE,
            dateCreated = "01/09/2026",
            dateSigned = "01/09/2026"
        )

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            PdfExporter.exportContractPdf(appContext, contract)
        }

        val dir = appContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: appContext.cacheDir
        val file = File(dir, "EzRoom_HopDong_test_contract_12345.pdf")

        assertTrue("Contract PDF file should exist", file.exists())
        assertTrue("Contract PDF file should not be empty", file.length() > 0)

        // Verify FileProvider can generate URI without throwing
        val uri = FileProvider.getUriForFile(appContext, "${appContext.packageName}.provider", file)
        assertNotNull("URI from FileProvider should not be null", uri)
        assertEquals("content", uri.scheme)
        println("Contract PDF Uri generated successfully: $uri (Size: ${file.length()} bytes)")
    }

    @Test
    fun testExportInvoicePdf() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val invoice = Invoice(
            id = "test_inv_67890",
            roomId = "room_1",
            roomName = "Phòng 301",
            period = "Tháng 09/2026",
            roomPrice = 3500000L,
            oldElectricity = 120,
            newElectricity = 180,
            oldWater = 30,
            newWater = 36,
            otherCosts = listOf(
                OtherCostItem("Phí dịch vụ chung", 150000L),
                OtherCostItem("Tiền mạng WiFi", 100000L)
            ),
            status = InvoiceStatus.PAID,
            dateCreated = "04/09/2026",
            renterName = "Nguyễn Văn A",
            renterPhone = "0987654321"
        )

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            PdfExporter.exportInvoicePdf(appContext, invoice)
        }

        val dir = appContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: appContext.cacheDir
        val file = File(dir, "EzRoom_Invoice_test_inv_67890.pdf")

        assertTrue("Invoice PDF file should exist", file.exists())
        assertTrue("Invoice PDF file should not be empty", file.length() > 0)

        // Verify FileProvider can generate URI without throwing
        val uri = FileProvider.getUriForFile(appContext, "${appContext.packageName}.provider", file)
        assertNotNull("URI from FileProvider should not be null", uri)
        assertEquals("content", uri.scheme)
        println("Invoice PDF Uri generated successfully: $uri (Size: ${file.length()} bytes)")
    }
}
