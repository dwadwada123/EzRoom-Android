package com.example.ezroom.domain.usecase

import com.example.ezroom.domain.model.RenterReview
import com.example.ezroom.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.example.ezroom.data.model.MockData

class GetRenterReviewsUseCase() {
    operator fun invoke(userId: String): Flow<List<RenterReview>> = flowOf(
        MockData.renterReviews
    )
}
