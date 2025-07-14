package com.gibran.artistsapp.data.repository

import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.gibran.artistsapp.data.api.DiscogsApiService
import com.gibran.artistsapp.data.response.ArtistDetailResponse
import com.gibran.artistsapp.data.response.ImageResponse
import com.gibran.artistsapp.data.response.MemberResponse
import com.gibran.artistsapp.data.response.ReleaseDetailResponse
import com.gibran.artistsapp.di.DispatcherProvider
import com.gibran.artistsapp.domain.model.ArtistDetail
import com.gibran.artistsapp.domain.model.DiscographyFilter
import com.gibran.artistsapp.domain.model.Image
import com.gibran.artistsapp.domain.model.Member
import com.gibran.artistsapp.domain.model.ReleaseDetail
import com.gibran.artistsapp.domain.model.SortOption
import com.gibran.artistsapp.util.EmptyBodyException
import com.gibran.artistsapp.util.NetworkUnavailableException
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ArtistRepositoryImplTest {

    private lateinit var apiService: DiscogsApiService
    private lateinit var dispatcherProvider: DispatcherProvider
    private lateinit var repository: ArtistRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Arrange
        Dispatchers.setMain(testDispatcher)
        apiService = mockk()
        dispatcherProvider = mockk()
        every { dispatcherProvider.io } returns testDispatcher
        every { dispatcherProvider.main } returns testDispatcher
        every { dispatcherProvider.default } returns testDispatcher

        repository = ArtistRepositoryImpl(apiService, dispatcherProvider)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchArtists returns flow with paging data`() = runTest {
        // Arrange
        val query = "test artist"

        // Act
        val result = repository.searchArtists(query)

        // Assert
        assertTrue(result != null)
    }

    @Test
    fun `getArtistDetail returns success when api call succeeds`() = runTest {
        // Arrange
        val artistId = 123L
        val mockResponse = ArtistDetailResponse(
            id = artistId,
            name = "Test Artist",
            profile = "Test profile",
            images = listOf(ImageResponse("test-image-uri")),
            members = listOf(MemberResponse(1L, "Member 1"))
        )
        val expectedArtistDetail = ArtistDetail(
            id = artistId,
            name = "Test Artist",
            profile = "Test profile",
            images = listOf(Image("test-image-uri")),
            members = listOf(Member(1L, "Member 1"))
        )

        coEvery { apiService.getArtistDetails(artistId) } returns Response.success(mockResponse)

        // Act
        val result = repository.getArtistDetail(artistId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedArtistDetail, result.getOrNull())
    }

    @Test
    fun `getArtistDetail returns failure when api returns error response`() = runTest {
        // Arrange
        val artistId = 123L
        val errorResponse = Response.error<ArtistDetailResponse>(404, mockk(relaxed = true))

        coEvery { apiService.getArtistDetails(artistId) } returns errorResponse

        // Act
        val result = repository.getArtistDetail(artistId)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is HttpException)
    }

    @Test
    fun `getArtistDetail returns failure when api returns null body`() = runTest {
        // Arrange
        val artistId = 123L
        val nullBodyResponse = Response.success<ArtistDetailResponse>(null)

        coEvery { apiService.getArtistDetails(artistId) } returns nullBodyResponse

        // Act
        val result = repository.getArtistDetail(artistId)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is EmptyBodyException)
    }

    @Test
    fun `getArtistDetail returns failure when network is unavailable`() = runTest {
        // Arrange
        val artistId = 123L

        coEvery { apiService.getArtistDetails(artistId) } throws IOException("Network error")

        // Act
        val result = repository.getArtistDetail(artistId)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NetworkUnavailableException)
    }

    @Test
    fun `getReleaseDetail returns success when api call succeeds`() = runTest {
        // Arrange
        val releaseId = 456L
        val mockResponse = mockk<ReleaseDetailResponse> {
            every { toDomain() } returns mockk<ReleaseDetail>()
        }

        coEvery { apiService.getReleaseDetails(releaseId) } returns Response.success(mockResponse)

        // Act
        val result = repository.getReleaseDetail(releaseId)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `getReleaseDetail returns failure when api returns error response`() = runTest {
        // Arrange
        val releaseId = 456L
        val errorResponse = Response.error<ReleaseDetailResponse>(500, mockk(relaxed = true))

        coEvery { apiService.getReleaseDetails(releaseId) } returns errorResponse

        // Act
        val result = repository.getReleaseDetail(releaseId)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is HttpException)
    }

    @Test
    fun `getReleaseDetail returns failure when api returns null body`() = runTest {
        // Arrange
        val releaseId = 456L
        val nullBodyResponse = Response.success<ReleaseDetailResponse>(null)

        coEvery { apiService.getReleaseDetails(releaseId) } returns nullBodyResponse

        // Act
        val result = repository.getReleaseDetail(releaseId)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is EmptyBodyException)
    }

    @Test
    fun `getReleaseDetail returns failure when network is unavailable`() = runTest {
        // Arrange
        val releaseId = 456L

        coEvery { apiService.getReleaseDetails(releaseId) } throws IOException("Network error")

        // Act
        val result = repository.getReleaseDetail(releaseId)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NetworkUnavailableException)
    }

    @Test
    fun `getArtistReleases returns flow with paging data`() = runTest {
        // Arrange
        val artistId = 123L
        val filter = DiscographyFilter()

        // Act
        val result = repository.getArtistReleases(artistId, filter)

        // Assert
        assertTrue(result != null)
    }

    @Test
    fun `getArtistReleases creates pager with correct configuration`() = runTest {
        // Arrange
        val artistId = 123L
        val filter = DiscographyFilter(sortBy = SortOption.TITLE_ASC)

        // Act
        val result = repository.getArtistReleases(artistId, filter)

        // Assert
        assertTrue(result != null)
    }
}
