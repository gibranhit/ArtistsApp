package com.gibran.artistsapp.domain.usecase

import com.gibran.artistsapp.domain.model.ReleaseDetail
import com.gibran.artistsapp.domain.repository.ArtistRepository
import javax.inject.Inject
import kotlin.Result

class GetReleaseDetailUseCase @Inject constructor(
    private val repository: ArtistRepository
) {
    suspend operator fun invoke(id: Long): Result<ReleaseDetail> = repository.getReleaseDetail(id)
}