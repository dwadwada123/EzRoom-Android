package com.example.ezroom.ui.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.ezroom.ui.theme.EzRoomTheme
import org.junit.Rule
import org.junit.Test

class ForgotPasswordScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun forgotPasswordScreen_initialState_showsEmailInput() {
        composeTestRule.setContent {
            EzRoomTheme {
                ForgotPasswordScreen(onBackClick = {}, onResetSuccess = {})
            }
        }

        // Check if step 1 text is displayed
        composeTestRule.onNodeWithText("XÁC THỰC EMAIL").assertIsDisplayed()
        
        // Check if email field exists
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        
        // Check if submit button exists
        composeTestRule.onNodeWithText("GỬI MÃ XÁC THỰC").assertIsDisplayed()
    }

    @Test
    fun forgotPasswordScreen_invalidEmail_showsError() {
        composeTestRule.setContent {
            EzRoomTheme {
                ForgotPasswordScreen(onBackClick = {}, onResetSuccess = {})
            }
        }

        // Type invalid email
        composeTestRule.onNodeWithText("Email").performTextInput("invalid-email")
        
        // Click submit
        composeTestRule.onNodeWithText("GỬI MÃ XÁC THỰC").performClick()
        
        // Check if error message appears
        composeTestRule.onNodeWithText("EMAIL KHÔNG HỢP LỆ. VUI LÒNG KIỂM TRA LẠI.").assertIsDisplayed()
    }

    @Test
    fun forgotPasswordScreen_validEmail_transitionsToStepTwo() {
        composeTestRule.setContent {
            EzRoomTheme {
                ForgotPasswordScreen(onBackClick = {}, onResetSuccess = {})
            }
        }

        // Type valid email
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        
        // Click submit
        composeTestRule.onNodeWithText("GỬI MÃ XÁC THỰC").performClick()
        
        // Check if step 2 text is displayed
        composeTestRule.onNodeWithText("THIẾT LẬP LẠI").assertIsDisplayed()
        
        // Check if OTP field exists
        composeTestRule.onNodeWithText("Mã OTP").assertIsDisplayed()
    }

    @Test
    fun forgotPasswordScreen_stepTwoValidation_showsErrors() {
        composeTestRule.setContent {
            EzRoomTheme {
                ForgotPasswordScreen(onBackClick = {}, onResetSuccess = {})
            }
        }

        // Transition to step 2
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("GỬI MÃ XÁC THỰC").performClick()

        // 1. Test short OTP
        composeTestRule.onNodeWithText("Mã OTP").performTextInput("123")
        composeTestRule.onNodeWithText("XÁC NHẬN ĐẶT LẠI MẬT KHẨU").performClick()
        composeTestRule.onNodeWithText("MÃ OTP PHẢI CÓ 4-6 KÝ TỰ.").assertIsDisplayed()

        // 2. Test short password
        composeTestRule.onNodeWithText("Mã OTP").performTextReplacement("1234")
        composeTestRule.onNodeWithText("Mật khẩu mới").performTextInput("123")
        composeTestRule.onNodeWithText("XÁC NHẬN ĐẶT LẠI MẬT KHẨU").performClick()
        composeTestRule.onNodeWithText("MẬT KHẨU QUÁ NGẮN (TỐI THIỂU 6 KÝ TỰ).").assertIsDisplayed()

        // 3. Test password mismatch
        composeTestRule.onNodeWithText("Mật khẩu mới").performTextReplacement("password123")
        composeTestRule.onNodeWithText("Xác nhận mật khẩu mới").performTextInput("mismatch")
        composeTestRule.onNodeWithText("XÁC NHẬN ĐẶT LẠI MẬT KHẨU").performClick()
        composeTestRule.onNodeWithText("MẬT KHẨU XÁC NHẬN KHÔNG KHỚP.").assertIsDisplayed()
    }
}
