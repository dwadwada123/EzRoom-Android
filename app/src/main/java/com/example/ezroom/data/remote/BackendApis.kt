package com.example.ezroom.data.remote

import com.example.ezroom.domain.model.*
import retrofit2.http.*
import okhttp3.MultipartBody

// Response wrappers
data class AuthResponse(
    val success: Boolean,
    val token: String?,
    val user: User?,
    val error: String?
)

data class GenericResponse(
    val success: Boolean,
    val message: String?,
    val error: String?
)

data class PaymentResponse(
    val success: Boolean,
    val qrUrl: String? = null,
    val checkoutUrl: String? = null,
    val qrCode: String? = null,
    val accountNumber: String? = null,
    val accountName: String? = null,
    val bankName: String? = null,
    val depositAmount: Long? = null,
    val paymentUrl: String? = null,
    val error: String? = null
)

// DTOs for requests
data class AmenityDto(
    val _id: String,
    val name: String,
    val type: String
)

data class RegisterRequest(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val password: String? = null
)

data class LoginRequest(
    val email: String,
    val phone: String,
    val password: String? = null
)

data class UpdateProfileRequest(
    val name: String,
    val phone: String
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)

data class EkycRequest(
    val userId: String,
    val idCardNumber: String,
    val frontImageUrl: String,
    val backImageUrl: String,
    val selfieUrl: String
)

data class CheckPhoneResponse(
    val success: Boolean,
    val exists: Boolean,
    val user: User? = null
)

interface AuthApi {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): GenericResponse

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): GenericResponse

    @GET("api/auth/check-phone/{phone}")
    suspend fun checkPhone(@Path("phone") phone: String): CheckPhoneResponse

    @GET("api/profile")
    suspend fun getProfile(): AuthResponse

    @POST("api/profile/update")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): AuthResponse

    @POST("api/profile/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): GenericResponse

    @POST("api/profile/ekyc")
    suspend fun submitEkyc(@Body request: EkycRequest): GenericResponse

    @Multipart
    @POST("api/auth/ekyc/upload")
    suspend fun uploadEkycImage(@Part image: MultipartBody.Part): ImageUploadResponse
}


// DTO for creating a property: serializes commonAmenities as List<String>
// to match the backend schema expectation instead of List<Amenity> objects.
data class PropertyRequest(
    val id: String,
    val name: String,
    val type: String,
    val address: String,
    val detailedAddress: String,
    val description: String,
    val commonAmenities: List<String>,
    val latitude: Double,
    val longitude: Double,
    val hostId: String
)

// DTO for receiving a property from the backend.
// Backend MongoDB documents use _id, so we map it with @SerializedName.
data class PropertyResponse(
    @com.google.gson.annotations.SerializedName("_id") val _id: String? = null,
    val id: String? = null,
    val name: String? = null,
    val type: String? = null,
    val address: String? = null,
    val detailedAddress: String? = null,
    val description: String? = null,
    val commonAmenities: List<String>? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isHidden: Boolean? = null,
    val hostId: String? = null,
    val rating: Float? = null,
    val reviewCount: Int? = null
) {
    // Use whichever id field is present (_id from MongoDB or id from transformed response)
    val resolvedId: String get() = id?.takeIf { it.isNotBlank() } ?: _id ?: ""
}

interface PropertyApi {
    @GET("api/properties")
    suspend fun getProperties(): List<PropertyResponse>

    @GET("api/properties/host")
    suspend fun getHostProperties(): List<PropertyResponse>

    @POST("api/properties")
    suspend fun createProperty(@Body property: PropertyRequest): GenericResponse

    @PATCH("api/properties/{id}/visibility")
    suspend fun togglePropertyVisibility(@Path("id") id: String): GenericResponse
}

// DTO for sending a room to the backend - only backend-relevant fields, no UI-only properties.
data class RoomRequest(
    val id: String,
    val propertyId: String?,
    val title: String,
    val price: Long,
    val electricityPrice: Long,
    val waterPrice: Long,
    val address: String,
    val detailedAddress: String,
    val description: String,
    val structure: String,
    val floorArea: Double,
    val mezzanineArea: Double,
    val capacity: Int,
    val detailedAreas: List<Map<String, Any>>,
    val images: List<Map<String, String?>>,
    val amenities: List<Map<String, Any>>,
    val latitude: Double,
    val longitude: Double,
    val status: String
)

// DTO for receiving a room from the backend - handles _id → id mapping.
data class RoomResponse(
    @com.google.gson.annotations.SerializedName("_id") val _id: String? = null,
    val id: String? = null,
    val propertyId: String? = null,
    val title: String = "",
    val price: Long = 0L,
    val electricityPrice: Long = 3500L,
    val waterPrice: Long = 15000L,
    val address: String = "",
    val detailedAddress: String = "",
    val description: String = "",
    val structure: String = "SINGLE",
    val floorArea: Double = 0.0,
    val mezzanineArea: Double = 0.0,
    val capacity: Int = 0,
    val detailedAreas: List<Map<String, Any>>? = null,
    val images: List<Map<String, String?>>? = null,
    val amenities: List<Map<String, Any>>? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val status: String = "ACTIVE",
    val isUserHidden: Boolean = false,
    val hostId: String? = null,
    val hostName: String? = null,
    val hostPhone: String? = null,
    val hostAvatarUrl: String? = null,
    val removalInfo: RemovalInfoDto? = null,
    val rating: Float = 0f,
    val reviewCount: Int = 0
) {
    val resolvedId: String get() = id?.takeIf { it.isNotBlank() } ?: _id ?: ""
}

data class RemovalInfoDto(
    val reason: String? = null,
    val dateRemoved: String? = null,
    val appealText: String? = null,
    val appealImages: List<String>? = null,
    val appealStatus: String? = null
)

data class AppealRequest(
    val appealText: String,
    val images: List<String> = emptyList()
)

interface RoomApi {
    @GET("api/rooms")
    suspend fun getRooms(): List<RoomResponse>

    @GET("api/rooms/host")
    suspend fun getHostRooms(): List<RoomResponse>

    @POST("api/rooms")
    suspend fun createRoom(@Body room: RoomRequest): GenericResponse

    @POST("api/rooms/{id}/report")
    suspend fun reportRoom(@Path("id") id: String, @Body body: Map<String, String>): GenericResponse

    @POST("api/rooms/{id}/appeal")
    suspend fun submitRoomAppeal(@Path("id") id: String, @Body request: AppealRequest): GenericResponse

    @DELETE("api/rooms/{id}")
    suspend fun deleteRoom(@Path("id") id: String): GenericResponse

    @PATCH("api/rooms/{id}/visibility")
    suspend fun toggleRoomVisibility(@Path("id") id: String): GenericResponse

    companion object {
        fun create(): RoomApi = NetworkClient.createService()
    }
}

interface AmenityApi {
    @GET("api/amenities")
    suspend fun getAmenities(): List<AmenityDto>
}

data class RoomReviewResponse(
    val id: String,
    val reviewerName: String,
    val reviewerAvatar: String?,
    val rating: Int,
    val comment: String,
    val isReported: Boolean = false,
    val reportReason: String? = null,
    val createdAt: String
)

data class RoomReviewRequest(
    val roomId: String,
    val rating: Int,
    val comment: String
)

data class ReportReviewRequest(
    val reason: String,
    val proofImages: List<String> = emptyList(),
    val reporterName: String? = null
)

interface RoomReviewApi {
    @GET("api/room-reviews/room/{roomId}")
    suspend fun getRoomReviews(@Path("roomId") roomId: String): List<RoomReviewResponse>

    @POST("api/room-reviews")
    suspend fun createRoomReview(@Body request: RoomReviewRequest): GenericResponse

    @POST("api/room-reviews/{id}/report")
    suspend fun reportRoomReview(@Path("id") id: String, @Body request: ReportReviewRequest): GenericResponse

    companion object {
        fun create(): RoomReviewApi = NetworkClient.createService()
    }
}


interface ContractApi {
    @GET("api/contracts")
    suspend fun getContracts(): List<Contract>

    @POST("api/contracts")
    suspend fun createContract(@Body contract: Contract): GenericResponse

    @POST("api/contracts/{id}/sign")
    suspend fun signContract(@Path("id") id: String): GenericResponse

    @POST("api/contracts/{id}/payment")
    suspend fun getPaymentQR(@Path("id") id: String): PaymentResponse

    @POST("api/contracts/{id}/confirm-payment")
    suspend fun confirmPayment(@Path("id") id: String): GenericResponse

    @POST("api/contracts/{id}/terminate")
    suspend fun terminateContract(@Path("id") id: String, @Body body: Map<String, String>): GenericResponse
}

interface InvoiceApi {
    @GET("api/invoices")
    suspend fun getInvoices(): List<Invoice>

    @GET("api/invoices/{id}")
    suspend fun getInvoiceById(@Path("id") id: String): Invoice

    @GET("api/invoices/{id}/payment-qr")
    suspend fun getPaymentQR(@Path("id") id: String): PaymentResponse

    @POST("api/invoices")
    suspend fun createInvoice(@Body invoice: Invoice): GenericResponse

    @PATCH("api/invoices/{id}/pay")
    suspend fun payInvoice(@Path("id") id: String, @Body body: Map<String, String>): GenericResponse

    @POST("api/invoices/{id}/remind")
    suspend fun remindInvoice(@Path("id") id: String): GenericResponse

    @POST("api/invoices/{id}/send-receipt")
    suspend fun sendInvoiceReceipt(@Path("id") id: String): GenericResponse
}

data class LocationSuggestion(
    val displayName: String,
    val lat: Double,
    val lon: Double
)

data class GeocodeResponse(
    val lat: Double,
    val lon: Double
)

interface LocationApi {
    @GET("api/location/suggest")
    suspend fun suggest(
        @Query("q") query: String,
        @Query("province") province: String?,
        @Query("ward") ward: String?
    ): List<LocationSuggestion>

    @GET("api/location/geocode")
    suspend fun geocode(
        @Query("q") query: String
    ): GeocodeResponse
}

data class AddFavoriteRequest(val userId: String, val roomId: String)
data class RemoveFavoriteRequest(val userId: String, val roomId: String)
data class SavePaymentAccountRequest(val userId: String, val account: PaymentAccount)
data class DeletePaymentAccountRequest(val userId: String, val accountId: String)
data class SetDefaultPaymentAccountRequest(val userId: String, val accountId: String)
data class UpdateAppointmentStatusRequest(
    val status: String,
    val date: String? = null,
    val time: String? = null
)

data class FavoriteResponse(val success: Boolean, val favoriteRoomIds: List<String>)
data class PaymentAccountsResponse(val success: Boolean, val paymentAccounts: List<PaymentAccount>)

interface RenterReviewApi {
    @GET("api/renter-reviews/renter/{renterId}")
    suspend fun getRenterReviews(@Path("renterId") renterId: String): List<RenterReview>

    @POST("api/renter-reviews")
    suspend fun createRenterReview(@Body review: RenterReview): GenericResponse

    @PUT("api/renter-reviews/{id}")
    suspend fun updateRenterReview(@Path("id") id: String, @Body review: RenterReview): GenericResponse

    @DELETE("api/renter-reviews/{id}")
    suspend fun deleteRenterReview(@Path("id") id: String): GenericResponse

    @POST("api/renter-reviews/{id}/report")
    suspend fun reportRenterReview(@Path("id") id: String, @Body request: ReportReviewRequest): GenericResponse

    companion object {
        fun create(): RenterReviewApi = NetworkClient.createService()
    }
}

interface AppointmentApi {
    @POST("api/appointments")
    suspend fun createAppointment(@Body appointment: Appointment): GenericResponse

    @GET("api/appointments")
    suspend fun getAppointments(
        @Query("renterName") renterName: String?,
        @Query("hostName") hostName: String?
    ): List<Appointment>

    @PUT("api/appointments/{id}/status")
    suspend fun updateAppointmentStatus(
        @Path("id") id: String,
        @Body request: UpdateAppointmentStatusRequest
    ): GenericResponse

    companion object {
        fun create(): AppointmentApi = NetworkClient.createService()
    }
}

interface UserProfileApi {
    @POST("api/profile/favorites")
    suspend fun addFavorite(@Body request: AddFavoriteRequest): FavoriteResponse

    @POST("api/profile/favorites/remove")
    suspend fun removeFavorite(@Body request: RemoveFavoriteRequest): FavoriteResponse

    @POST("api/profile/payment-accounts")
    suspend fun savePaymentAccount(@Body request: SavePaymentAccountRequest): PaymentAccountsResponse

    @POST("api/profile/payment-accounts/delete")
    suspend fun deletePaymentAccount(@Body request: DeletePaymentAccountRequest): PaymentAccountsResponse

    @POST("api/profile/payment-accounts/default")
    suspend fun setDefaultPaymentAccount(@Body request: SetDefaultPaymentAccountRequest): PaymentAccountsResponse

    companion object {
        fun create(): UserProfileApi = NetworkClient.createService()
    }
}

data class SendMessageRequest(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val content: String,
    val timestamp: String,
    val renterId: String? = null,
    val hostId: String? = null,
    val imageUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class ImageUploadResponse(
    val success: Boolean,
    val url: String?,
    val error: String?
)

interface ChatApi {
    @GET("api/conversations")
    suspend fun getConversations(@Query("userId") userId: String): List<Conversation>

    @GET("api/conversations/{conversationId}/messages")
    suspend fun getMessages(
        @Path("conversationId") conversationId: String,
        @Query("userId") userId: String
    ): List<Message>

    @POST("api/conversations/messages")
    suspend fun sendMessage(@Body request: SendMessageRequest): GenericResponse

    @Multipart
    @POST("api/upload")
    suspend fun uploadImage(@Part image: okhttp3.MultipartBody.Part): ImageUploadResponse

    companion object {
        fun create(): ChatApi = NetworkClient.createService()
    }
}

data class CreateNotificationRequest(
    val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val type: String,
    val targetId: String? = null,
    val isRead: Boolean = false,
    val timestamp: String
)

// Media Upload
data class MediaUploadResponse(
    val success: Boolean,
    val url: String?,
    val error: String?
)

interface MediaApi {
    @Multipart
    @POST("api/media/upload")
    suspend fun uploadMedia(@Part file: okhttp3.MultipartBody.Part): MediaUploadResponse

    companion object {
        fun create(): MediaApi = NetworkClient.createService()
    }
}

interface NotificationApi {
    @GET("api/notifications")
    suspend fun getNotifications(@Query("userId") userId: String): List<NotificationItem>

    @POST("api/notifications")
    suspend fun createNotification(@Body request: CreateNotificationRequest): GenericResponse

    @PUT("api/notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: String): GenericResponse

    @PUT("api/notifications/read-all")
    suspend fun markAllAsRead(@Body body: Map<String, String>): GenericResponse

    companion object {
        fun create(): NotificationApi = NetworkClient.createService()
    }
}
