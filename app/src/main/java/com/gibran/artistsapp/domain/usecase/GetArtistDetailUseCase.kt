package com.gibran.artistsapp.domain.usecase

import com.gibran.artistsapp.domain.model.ArtistDetail
import kotlin.Result
import com.gibran.artistsapp.domain.repository.ArtistRepository
import javax.inject.Inject

class GetArtistDetailUseCase @Inject constructor(
    private val repository: ArtistRepository
) {
    suspend operator fun invoke(id: Long): Result<ArtistDetail> = repository.getArtistDetail(id)
}
