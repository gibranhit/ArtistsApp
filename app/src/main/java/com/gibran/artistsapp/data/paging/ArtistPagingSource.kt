package com.gibran.artistsapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gibran.artistsapp.data.api.DiscogsApiService
import com.gibran.artistsapp.domain.model.Artist
import retrofit2.HttpException
import java.io.IOException

class ArtistPagingSource(
    private val apiService: DiscogsApiService,
    private val query: String
) : PagingSource<Int, Artist>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Artist> {
        val page = params.key ?: 1

        return try {
            val response = apiService.searchArtists(
                query = query,
                page = page
            )

            if (response.isSuccessful) {
                val searchResponse = response.body()!!
                val artists = searchResponse.results.map { it.toDomain() }

                LoadResult.Page(
                    data = artists,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (searchResponse.pagination.page < searchResponse.pagination.pages) {
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
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Artist>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
