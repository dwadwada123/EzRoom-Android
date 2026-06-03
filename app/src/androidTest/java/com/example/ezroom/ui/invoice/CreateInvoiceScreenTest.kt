package com.example.ezroom.ui.invoice

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.ezroom.ui.theme.EzRoomTheme
import org.junit.Rule
import org.junit.Test
import java.text.DecimalFormat

class CreateInvoiceScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val formatter = DecimalFormat("#,### đ")

    @Test
    fun createInvoiceScreen_initialState_displaysCorrectBaseRent() {
        val baseRent = 3000000L
        val roomName = "Phòng 101"

        composeTestRule.setContent {
            EzRoomTheme {
                CreateInvoiceScreen(
                    roomName = roomName,
                    baseRentPrice = baseRent,
                    onNavigateBack = {},
                    onInvoiceCreated = {}
                )
            }
        }

        // Check if title and base rent are displayed
        composeTestRule.onNodeWithText("LẬP HÓA ĐƠN - $roomName").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tiền phòng cố định: ${formatter.format(baseRent)}").assertIsDisplayed()
        
        // Initial total should be the base rent
        composeTestRule.onNodeWithText(formatter.format(baseRent)).assertIsDisplayed()
    }

    @Test
    fun createInvoiceScreen_inputElectricity_updatesTotal() {
        val baseRent = 3000000L
        composeTestRule.setContent {
            EzRoomTheme {
                CreateInvoiceScreen(
                    baseRentPrice = baseRent,
                    onNavigateBack = {},
                    onInvoiceCreated = {}
                )
            }
        }

        // Old electricity: 100, New electricity: 200 -> Consumption: 100
        // Cost: 100 * 3500 = 350,000
        // Total: 3,000,000 + 350,000 = 3,350,000
        
        composeTestRule.onAllNodesWithText("Số cũ")[0].performTextInput("100")
        composeTestRule.onAllNodesWithText("Số mới")[0].performTextInput("200")

        composeTestRule.onNodeWithText(formatter.format(3350000L)).assertIsDisplayed()
    }

    @Test
    fun createInvoiceScreen_inputWater_updatesTotal() {
        val baseRent = 3000000L
        composeTestRule.setContent {
            EzRoomTheme {
                CreateInvoiceScreen(
                    baseRentPrice = baseRent,
                    onNavigateBack = {},
                    onInvoiceCreated = {}
                )
            }
        }

        // Old water: 10, New water: 20 -> Consumption: 10
        // Cost: 10 * 15000 = 150,000
        // Total: 3,000,000 + 150,000 = 3,150,000
        
        composeTestRule.onAllNodesWithText("Số cũ")[1].performTextInput("10")
        composeTestRule.onAllNodesWithText("Số mới")[1].performTextInput("20")

        composeTestRule.onNodeWithText(formatter.format(3150000L)).assertIsDisplayed()
    }

    @Test
    fun createInvoiceScreen_inputOtherCosts_updatesTotal() {
        val baseRent = 3000000L
        composeTestRule.setContent {
            EzRoomTheme {
                CreateInvoiceScreen(
                    baseRentPrice = baseRent,
                    onNavigateBack = {},
                    onInvoiceCreated = {}
                )
            }
        }

        // Other costs: 50,000
        // Total: 3,000,000 + 50,000 = 3,050,000
        
        composeTestRule.onNodeWithText("Chi phí phát sinh khác (đ)").performTextInput("50000")

        composeTestRule.onNodeWithText(formatter.format(3050000L)).assertIsDisplayed()
    }

    @Test
    fun createInvoiceScreen_clickConfirm_callsCallback() {
        var confirmed = false
        composeTestRule.setContent {
            EzRoomTheme {
                CreateInvoiceScreen(
                    onNavigateBack = {},
                    onInvoiceCreated = { confirmed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("XÁC NHẬN TẠO HÓA ĐƠN").performClick()
        assert(confirmed)
    }

    @Test
    fun createInvoiceScreen_clickBack_callsCallback() {
        var backed = false
        composeTestRule.setContent {
            EzRoomTheme {
                CreateInvoiceScreen(
                    onNavigateBack = { backed = true },
                    onInvoiceCreated = {}
                )
            }
        }

        // The back icon is in an IconButton. We can find it by content description or icon.
        // In the code: navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = OrangePrimary) } }
        // Since contentDescription is null, we might need to use a role or find the parent IconButton if possible, 
        // but often the Icon's node can be clicked if it doesn't have a content desc but its parent is clickable.
        // Actually, let's try finding the node with the IconButton's onClick or just by its position/icon.
        // A better way is to add a content description to the icon in the source code.
        
        // Let's try finding by icon if possible, or just the IconButton.
        // For now, I'll use a semantic matcher that might work or I'll update the source code to add a content description.
        
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backed)
    }
}
