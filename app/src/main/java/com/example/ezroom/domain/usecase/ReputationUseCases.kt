package com.example.ezroom.domain.usecase

import com.example.ezroom.domain.model.RenterReview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetRenterReviewsUseCase(
    private val api: com.example.ezroom.data.remote.RenterReviewApi = com.example.ezroom.data.remote.RenterReviewApi.create()
) {
    operator fun invoke(userId: String): Flow<List<RenterReview>> = flow {
        try {
            val reviews = api.getRenterReviews(userId)
            emit(reviews)
        } catch (e: java.lang.Exception) {
            emit(emptyList())
        }
    }
}
