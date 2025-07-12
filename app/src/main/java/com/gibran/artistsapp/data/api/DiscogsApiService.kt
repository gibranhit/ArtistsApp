package com.gibran.artistsapp.data.api

import com.gibran.artistsapp.data.response.ArtistSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DiscogsApiService {

    @GET("database/search")
    suspend fun searchArtists(
        @Query("q") query: String,
        @Query("type") type: String = "artist",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): Response<ArtistSearchResponse>

    companion object {
        const val USER_AGENT_HEADER: String = "User-Agent"
        const val USER_AGENT: String = "ArtistsApp/1.0"
        const val AUTHORIZATION_HEADER: String = "Authorization"

    }
}