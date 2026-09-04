package com.example.ezroom.data.repository

import com.example.ezroom.data.remote.AuthApi
import com.example.ezroom.data.remote.LoginRequest
import com.example.ezroom.data.remote.RegisterRequest
import com.example.ezroom.data.remote.EkycRequest
import com.example.ezroom.data.remote.UpdateProfileRequest
import com.example.ezroom.data.remote.ChangePasswordRequest
import com.example.ezroom.data.remote.NetworkClient
import com.example.ezroom.domain.model.User
import com.example.ezroom.domain.repository.UserRepository
import com.example.ezroom.util.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import com.example.ezroom.data.remote.ForgotPasswordRequest
import com.example.ezroom.data.remote.ResetPasswordRequest
import android.content.Context
import android.net.Uri
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
class UserRepositoryImpl : UserRepository {
    private val authApi = NetworkClient.createService<AuthApi>()
    private val cachedUser = TokenManager.getUser()
    // If cached user has no valid id, clear and start fresh (stale cache migration)
    private val _user = MutableStateFlow<User?>(
        if (cachedUser != null && cachedUser.id.isNotBlank()) cachedUser else null
    )
    
    override fun getCurrentUser(): Flow<User?> = _user.asStateFlow()

    override suspend fun updateProfile(name: String, phone: String) {
        try {
            val response = authApi.updateProfile(UpdateProfileRequest(name, phone))
            if (response.success && response.user != null) {
                _user.update { response.user }
                TokenManager.saveUser(response.user)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback locally
            _user.update { it?.copy(name = name, phone = phone) }
            _user.value?.let { TokenManager.saveUser(it) }
        }
    }

    override suspend fun changePassword(current: String, new: String): Boolean {
        return try {
            val response = authApi.changePassword(ChangePasswordRequest(current, new))
            if (response.success) {
                // Clear entire session — user must re-login with new password
                _user.update { null }
                TokenManager.clear()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun uploadEkycImage(uri: Uri, context: Context): String? {
        return try {
//            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
//            val bytes = inputStream?.readBytes() ?: return null
//            val mediaType = context.contentResolver.getType(uri)?.toMediaTypeOrNull() ?: "image/jpeg".toMediaTypeOrNull()
//            val requestBody = bytes.toRequestBody(mediaType)
//            val body = MultipartBody.Part.createFormData("image", "ekyc_img_${System.currentTimeMillis()}.jpg", requestBody)
//
//            val response = authApi.uploadEkycImage(body)
//            if (response.success) response.url else null
            Log.d("EKYC", "URI input = $uri")

            val inputStream = context.contentResolver.openInputStream(uri)

            if (inputStream == null) {
                Log.e("EKYC", "Cannot open InputStream")
                return null
            }

            val bytes = inputStream.readBytes()

            Log.d("EKYC", "Image size = ${bytes.size} bytes")

            val mimeType = context.contentResolver.getType(uri)
                ?: "image/jpeg"

            Log.d("EKYC", "Mime type = $mimeType")

            val mediaType = mimeType.toMediaTypeOrNull()

            val requestBody = bytes.toRequestBody(mediaType)

            val body = MultipartBody.Part.createFormData(
                "image",
                "ekyc_img_${System.currentTimeMillis()}.jpg",
                requestBody
            )

            Log.d("EKYC", "Starting upload...")

            val response = authApi.uploadEkycImage(body)

            Log.d("EKYC", "Upload success = ${response.success}")
            Log.d("EKYC", "Upload url = ${response.url}")

            if (response.success) {
                response.url
            } else {
                Log.e("EKYC", "Upload failed: $response")
                null
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    override suspend fun verifyEkyc(idCardNumber: String, frontUri: Uri, backUri: Uri, selfieUri: Uri, context: Context): Result<Unit> {
        val currentUser = _user.value ?: return Result.failure(Exception("User not found locally"))
        return try {
            Log.d("check-truoc", "frontUri: $frontUri, backUri: $backUri, selfieUri: $selfieUri")
            val frontUrl = uploadEkycImage(frontUri, context)
            val backUrl = uploadEkycImage(backUri, context)
            val selfieUrl = uploadEkycImage(selfieUri, context)

            Log.d("check", "frontUrl: $frontUrl, backUrl: $backUrl, selfieUrl: $selfieUrl")

            if (frontUrl == null || backUrl == null || selfieUrl == null) {
                return Result.failure(Exception("Không thể tải ảnh lên. Vui lòng thử lại."))
            }

            val response = authApi.submitEkyc(
                EkycRequest(
                    userId = currentUser.id,
                    idCardNumber = idCardNumber,
                    frontImageUrl = frontUrl,
                    backImageUrl = backUrl,
                    selfieUrl = selfieUrl
                )
            )
            if (response.success) {
                val updatedUser = currentUser.copy(
                    ekycStatus = "PENDING"
                )
                _user.update { updatedUser }
                TokenManager.saveUser(updatedUser)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Gửi hồ sơ thất bại."))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Lỗi kết nối máy chủ. Vui lòng thử lại."))
        }
    }

    suspend fun loginWithErrorMessage(email: String, password: String): Pair<Boolean, String?> {
        return try {
            val response = authApi.login(LoginRequest(email, "", password))
            if (response.success && response.token != null && response.user != null) {
                TokenManager.saveToken(response.token)
                TokenManager.saveUser(response.user)
                _user.update { response.user }
                Pair(true, null)
            } else {
                Pair(false, response.error ?: "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin!")
            }
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val json = org.json.JSONObject(errorBody ?: "")
                json.optString("error", "Tài khoản của bạn đã bị khóa.")
            } catch (ex: Exception) {
                "Tài khoản của bạn đã bị khóa hoặc có lỗi xảy ra."
            }
            Pair(false, errorMessage)
        } catch (e: Exception) {
            Pair(false, "Lỗi kết nối máy chủ. Vui lòng thử lại!")
        }
    }

    override suspend fun login(email: String, password: String): Boolean {
        return loginWithErrorMessage(email, password).first
    }

    override suspend fun register(name: String, email: String, phone: String, password: String, role: String): Boolean {
        return try {
            val response = authApi.register(RegisterRequest(
                name = name,
                email = email,
                phone = phone,
                role = role,
                password = password
            ))
            if (response.success && response.token != null && response.user != null) {
                TokenManager.saveToken(response.token)
                TokenManager.saveUser(response.user)
                _user.update { response.user }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun requestForgotPassword(email: String): Result<String> {
        return try {
            val response = authApi.forgotPassword(ForgotPasswordRequest(email))
            if (response.success) {
                Result.success(response.message ?: "Mã OTP đã được gửi.")
            } else {
                Result.failure(Exception(response.error ?: "Email không tồn tại trong hệ thống."))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Không thể kết nối đến máy chủ."))
        }
    }

    override suspend fun resetPassword(email: String, otp: String, newPass: String): Result<String> {
        return try {
            val response = authApi.resetPassword(ResetPasswordRequest(email, otp, newPass))
            if (response.success) {
                Result.success(response.message ?: "Đổi mật khẩu thành công.")
            } else {
                Result.failure(Exception(response.error ?: "Đổi mật khẩu thất bại."))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Không thể kết nối đến máy chủ."))
        }
    }

    suspend fun fetchFreshProfile(): User? {
        return try {
            val response = authApi.getProfile()
            if (response.success && response.user != null) {
                TokenManager.saveUser(response.user)
                _user.update { response.user }
                response.user
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

