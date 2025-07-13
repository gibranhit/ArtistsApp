package com.gibran.artistsapp.data.api

import com.gibran.artistsapp.data.response.ArtistSearchResponse
import com.gibran.artistsapp.data.response.ArtistDetailResponse
import com.gibran.artistsapp.data.response.ReleasesResponse
import com.gibran.artistsapp.data.response.ReleaseDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DiscogsApiService {

    @GET("database/search")
    suspend fun searchArtists(
        @Query("q") query: String,
        @Query("type") type: String = "artist",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): Response<ArtistSearchResponse>

    @GET("artists/{id}")
    suspend fun getArtistDetails(
        @Path("id") id: Long
    ): Response<ArtistDetailResponse>

    @GET("artists/{id}/releases")
    suspend fun getArtistReleases(
        @Path("id") id: Long,
        @Query("sort") sort: String = "year",
        @Query("sort_order") sortOrder: String = "desc",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): Response<ReleasesResponse>

    @GET("releases/{id}")
    suspend fun getReleaseDetails(
        @Path("id") id: Long
    ): Response<ReleaseDetailResponse>

    companion object {
        const val USER_AGENT_HEADER: String = "User-Agent"
        const val USER_AGENT: String = "ArtistsApp/1.0"
        const val AUTHORIZATION_HEADER: String = "Authorization"

    }
}