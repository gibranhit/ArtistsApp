package com.gibran.artistsapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gibran.artistsapp.data.api.DiscogsApiService
import com.gibran.artistsapp.domain.model.Release
import com.gibran.artistsapp.domain.model.DiscographyFilter
import retrofit2.HttpException
import java.io.IOException

class ReleasesPagingSource(
    private val apiService: DiscogsApiService,
    private val artistId: Long,
    private val filter: DiscographyFilter
) : PagingSource<Int, Release>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Release> {
        val page = params.key ?: 1

        return try {
            val response = apiService.getArtistReleases(
                id = artistId,
                page = page,
                sort = filter.sortBy.apiSort,
                sortOrder = filter.sortBy.apiOrder
            )

            if (response.isSuccessful) {
                val releasesResponse = response.body()!!
                val releases = releasesResponse.releases.map { it.toDomain() }

                LoadResult.Page(
                    data = releases,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (releasesResponse.pagination.page < releasesResponse.pagination.pages) {
                        page + 1
                    } else null
                )
            } else {
                LoadResult.Error(
                    HttpException(response)
                )
            }
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Release>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}