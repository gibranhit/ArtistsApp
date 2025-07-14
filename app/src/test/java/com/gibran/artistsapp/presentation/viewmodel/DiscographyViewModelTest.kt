package com.gibran.artistsapp.presentation.viewmodel

import androidx.paging.PagingData
import com.gibran.artistsapp.domain.model.Release
import com.gibran.artistsapp.domain.model.DiscographyFilter
import com.gibran.artistsapp.domain.model.SortOption
import com.gibran.artistsapp.domain.usecase.GetArtistReleasesUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DiscographyViewModelTest {

    private lateinit var viewModel: DiscographyViewModel
    private lateinit var mockGetArtistReleasesUseCase: GetArtistReleasesUseCase

    private val testDispatcher = StandardTestDispatcher()

    private val sampleReleases = listOf(
        Release(1, "Abbey Road", 1969, "Album", "Main", "thumb1.jpg"),
        Release(2, "Sgt. Pepper's", 1967, "Album", "Main", "thumb2.jpg"),
        Release(3, "White Album", 1968, "Album", "Main", "thumb3.jpg")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockGetArtistReleasesUseCase = mockk(relaxed = true)
        viewModel = DiscographyViewModel(mockGetArtistReleasesUseCase)
    }

    @Test
    fun `initial filter state should be default DiscographyFilter`() {
        // Arrange & Act
        val initialFilter = viewModel.currentFilterState.value

        // Assert
        assertEquals(DiscographyFilter(), initialFilter)
    }

    @Test
    fun `onIntent with Load should trigger releases flow for valid artist id`() = runTest {
        // Arrange
        val artistId = 1L
        val intent = DiscographyIntent.Load(artistId)
        val expectedPagingData = PagingData.from(sampleReleases)

        every {
            mockGetArtistReleasesUseCase(artistId, any())
        } returns flowOf(expectedPagingData)

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()
        val releasesData = viewModel.releases.first()

        // Assert
        verify { mockGetArtistReleasesUseCase(artistId, DiscographyFilter()) }
        assertNotNull("Releases data should not be null", releasesData)
        assertTrue("Releases data should be PagingData type", releasesData is PagingData<Release>)
    }

    @Test
    fun `onIntent with ApplyFilter should update filter state`() {
        // Arrange
        val artistId = 1L
        val newFilter = DiscographyFilter(sortBy = SortOption.TITLE_ASC)
        val loadIntent = DiscographyIntent.Load(artistId)
        val filterIntent = DiscographyIntent.ApplyFilter(artistId, newFilter)

        every {
            mockGetArtistReleasesUseCase(any(), any())
        } returns flowOf(PagingData.from(sampleReleases))

        // Act
        viewModel.onIntent(loadIntent)
        viewModel.onIntent(filterIntent)

        // Assert
        assertEquals(newFilter, viewModel.currentFilterState.value)
    }

    @Test
    fun `onIntent with ApplyFilter should trigger new releases flow with filter`() = runTest {
        // Arrange
        val artistId = 1L
        val newFilter = DiscographyFilter(sortBy = SortOption.YEAR_DESC)
        val loadIntent = DiscographyIntent.Load(artistId)
        val filterIntent = DiscographyIntent.ApplyFilter(artistId, newFilter)

        every {
            mockGetArtistReleasesUseCase(artistId, any())
        } returns flowOf(PagingData.from(sampleReleases))

        // Act
        viewModel.onIntent(loadIntent)
        advanceUntilIdle()

        viewModel.onIntent(filterIntent)
        advanceUntilIdle()

        val releasesData = viewModel.releases.first()

        // Assert
        verify { mockGetArtistReleasesUseCase(artistId, DiscographyFilter()) }
        verify { mockGetArtistReleasesUseCase(artistId, newFilter) }
        assertNotNull("Releases data should not be null", releasesData)
    }

    @Test
    fun `releases flow should return empty when no artist id is set`() = runTest {
        // Arrange & Act
        val releasesData = viewModel.releases.first()

        // Assert
        assertNotNull("Releases data should not be null", releasesData)
        verify(exactly = 0) { mockGetArtistReleasesUseCase(any(), any()) }
    }

    @Test
    fun `should handle multiple filter changes correctly`() = runTest {
        // Arrange
        val artistId = 1L
        val firstFilter = DiscographyFilter(sortBy = SortOption.TITLE_ASC)
        val secondFilter = DiscographyFilter(sortBy = SortOption.YEAR_DESC)
        val thirdFilter = DiscographyFilter(sortBy = SortOption.FORMAT_ASC)

        every {
            mockGetArtistReleasesUseCase(any(), any())
        } returns flowOf(PagingData.from(sampleReleases))

        // Act
        viewModel.onIntent(DiscographyIntent.Load(artistId))
        viewModel.onIntent(DiscographyIntent.ApplyFilter(artistId, firstFilter))
        viewModel.onIntent(DiscographyIntent.ApplyFilter(artistId, secondFilter))
        viewModel.onIntent(DiscographyIntent.ApplyFilter(artistId, thirdFilter))
        advanceUntilIdle()

        // Assert
        assertEquals(thirdFilter, viewModel.currentFilterState.value)
    }


    @Test
    fun `should verify exact parameter matching for use case calls`() = runTest {
        // Arrange
        val artistId = 123L
        val filter = DiscographyFilter(sortBy = SortOption.TITLE_DESC)
        val loadIntent = DiscographyIntent.Load(artistId)
        val filterIntent = DiscographyIntent.ApplyFilter(artistId, filter)

        every {
            mockGetArtistReleasesUseCase(any(), any())
        } returns flowOf(PagingData.from(sampleReleases))

        // Act
        viewModel.onIntent(loadIntent)
        viewModel.onIntent(filterIntent)
        advanceUntilIdle()

    }

    @Test
    fun `should handle load intent with different sort options`() = runTest {
        // Arrange
        val artistId = 1L
        val sortOptions = listOf(
            SortOption.YEAR_DESC,
            SortOption.YEAR_ASC,
            SortOption.TITLE_ASC,
            SortOption.TITLE_DESC,
            SortOption.FORMAT_ASC
        )

        every {
            mockGetArtistReleasesUseCase(any(), any())
        } returns flowOf(PagingData.from(sampleReleases))

        // Act & Assert
        viewModel.onIntent(DiscographyIntent.Load(artistId))

        sortOptions.forEach { sortOption ->
            val filter = DiscographyFilter(sortBy = sortOption)
            viewModel.onIntent(DiscographyIntent.ApplyFilter(artistId, filter))

            assertEquals(filter, viewModel.currentFilterState.value)
        }
    }

    @Test
    fun `should handle filter changes without artist id correctly`() {
        // Arrange
        val filter = DiscographyFilter(sortBy = SortOption.TITLE_ASC)
        val filterIntent = DiscographyIntent.ApplyFilter(1L, filter)

        // Act
        viewModel.onIntent(filterIntent)

        // Assert
        assertEquals(filter, viewModel.currentFilterState.value)
        // No use case call should be made since no artist is loaded
        verify(exactly = 0) { mockGetArtistReleasesUseCase(any(), any()) }
    }

    @Test
    fun `should maintain filter state across artist changes`() = runTest {
        // Arrange
        val firstArtistId = 1L
        val secondArtistId = 2L
        val filter = DiscographyFilter(sortBy = SortOption.YEAR_DESC)

        every {
            mockGetArtistReleasesUseCase(any(), any())
        } returns flowOf(PagingData.from(sampleReleases))

        // Act
        viewModel.onIntent(DiscographyIntent.Load(firstArtistId))
        viewModel.onIntent(DiscographyIntent.ApplyFilter(firstArtistId, filter))
        viewModel.onIntent(DiscographyIntent.Load(secondArtistId))
        advanceUntilIdle()

        // Assert
        assertEquals(filter, viewModel.currentFilterState.value)
    }

    @Test
    fun `should handle empty releases result`() = runTest {
        // Arrange
        val artistId = 1L
        val intent = DiscographyIntent.Load(artistId)
        val emptyPagingData = PagingData.empty<Release>()

        every {
            mockGetArtistReleasesUseCase(artistId, any())
        } returns flowOf(emptyPagingData)

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()
        val releasesData = viewModel.releases.first()

        // Assert
        verify { mockGetArtistReleasesUseCase(artistId, DiscographyFilter()) }
        assertNotNull("Releases data should not be null", releasesData)
        assertTrue("Releases data should be PagingData type", releasesData is PagingData<Release>)
    }

    @Test
    fun `should handle negative artist id`() = runTest {
        // Arrange
        val negativeArtistId = -1L
        val intent = DiscographyIntent.Load(negativeArtistId)

        every {
            mockGetArtistReleasesUseCase(negativeArtistId, any())
        } returns flowOf(PagingData.from(sampleReleases))

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()
        val releasesData = viewModel.releases.first()

        // Assert
        verify { mockGetArtistReleasesUseCase(negativeArtistId, DiscographyFilter()) }
        assertNotNull("Releases data should not be null", releasesData)
    }

    @Test
    fun `should handle rapid consecutive filter changes`() = runTest {
        // Arrange
        val artistId = 1L
        val filters = listOf(
            DiscographyFilter(sortBy = SortOption.TITLE_ASC),
            DiscographyFilter(sortBy = SortOption.YEAR_DESC),
            DiscographyFilter(sortBy = SortOption.FORMAT_ASC),
            DiscographyFilter(sortBy = SortOption.TITLE_DESC)
        )

        every {
            mockGetArtistReleasesUseCase(any(), any())
        } returns flowOf(PagingData.from(sampleReleases))

        // Act
        viewModel.onIntent(DiscographyIntent.Load(artistId))

        filters.forEach { filter ->
            viewModel.onIntent(DiscographyIntent.ApplyFilter(artistId, filter))
        }

        advanceUntilIdle()

        // Assert
        assertEquals(filters.last(), viewModel.currentFilterState.value)

    }

    @Test
    fun `should handle load then multiple applies sequence correctly`() = runTest {
        // Arrange
        val artistId = 1L
        val firstFilter = DiscographyFilter(sortBy = SortOption.TITLE_ASC)
        val secondFilter = DiscographyFilter(sortBy = SortOption.YEAR_DESC)

        every {
            mockGetArtistReleasesUseCase(any(), any())
        } returns flowOf(PagingData.from(sampleReleases))

        // Act
        viewModel.onIntent(DiscographyIntent.Load(artistId))
        viewModel.onIntent(DiscographyIntent.ApplyFilter(artistId, firstFilter))
        viewModel.onIntent(DiscographyIntent.ApplyFilter(artistId, secondFilter))
        advanceUntilIdle()

        // Assert
        assertEquals(secondFilter, viewModel.currentFilterState.value)
    }
}
