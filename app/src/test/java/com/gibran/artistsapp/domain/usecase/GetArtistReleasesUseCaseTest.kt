package com.gibran.artistsapp.domain.usecase

import androidx.paging.PagingData
import com.gibran.artistsapp.domain.model.Release
import com.gibran.artistsapp.domain.model.DiscographyFilter
import com.gibran.artistsapp.domain.model.SortOption
import com.gibran.artistsapp.domain.repository.ArtistRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetArtistReleasesUseCaseTest {

    private lateinit var getArtistReleasesUseCase: GetArtistReleasesUseCase
    private lateinit var mockRepository: ArtistRepository

    private val sampleReleases = listOf(
        Release(1, "Abbey Road", 1969, "Album", "Main", "thumb1.jpg"),
        Release(2, "Sgt. Pepper's", 1967, "Album", "Main", "thumb2.jpg"),
        Release(3, "White Album", 1968, "Album", "Main", "thumb3.jpg")
    )

    @Before
    fun setUp() {
        mockRepository = mockk(relaxed = true)
        getArtistReleasesUseCase = GetArtistReleasesUseCase(mockRepository)
    }

    @Test
    fun `invoke should call repository getArtistReleases with correct parameters`() = runTest {
        // Arrange
        val artistId = 1L
        val filter = DiscographyFilter()
        val expectedResult = flowOf(PagingData.from(sampleReleases))

        every { mockRepository.getArtistReleases(artistId, filter) } returns expectedResult

        // Act
        val result = getArtistReleasesUseCase.invoke(artistId, filter)

        // Assert
        verify { mockRepository.getArtistReleases(artistId, filter) }
        assertSame(expectedResult, result)
    }

    @Test
    fun `invoke should use default filter when none provided`() = runTest {
        // Arrange
        val artistId = 123L
        val defaultFilter = DiscographyFilter()
        val expectedResult = flowOf(PagingData.from(sampleReleases))

        every { mockRepository.getArtistReleases(artistId, defaultFilter) } returns expectedResult

        // Act
        val result = getArtistReleasesUseCase.invoke(artistId)

        // Assert
        verify { mockRepository.getArtistReleases(artistId, defaultFilter) }
        assertSame(expectedResult, result)
    }

    @Test
    fun `invoke should return flow from repository`() = runTest {
        // Arrange
        val artistId = 456L
        val filter = DiscographyFilter(sortBy = SortOption.TITLE_ASC)
        val expectedResult = flowOf(PagingData.from(sampleReleases))

        every { mockRepository.getArtistReleases(artistId, filter) } returns expectedResult

        // Act
        val result = getArtistReleasesUseCase.invoke(artistId, filter)
        val pagingData = result.first()

        // Assert
        assertNotNull("PagingData should not be null", pagingData)
        assertTrue("PagingData should be correct type", pagingData is PagingData<Release>)
        assertSame(expectedResult, result)
    }

    @Test
    fun `invoke should handle different artist ids`() = runTest {
        // Arrange
        val firstArtistId = 1L
        val secondArtistId = 2L
        val filter = DiscographyFilter()
        val firstResult = flowOf(PagingData.from(listOf(sampleReleases[0])))
        val secondResult = flowOf(PagingData.from(listOf(sampleReleases[1])))

        every { mockRepository.getArtistReleases(firstArtistId, filter) } returns firstResult
        every { mockRepository.getArtistReleases(secondArtistId, filter) } returns secondResult

        // Act
        val result1 = getArtistReleasesUseCase.invoke(firstArtistId, filter)
        val result2 = getArtistReleasesUseCase.invoke(secondArtistId, filter)

        // Assert
        verify { mockRepository.getArtistReleases(firstArtistId, filter) }
        verify { mockRepository.getArtistReleases(secondArtistId, filter) }
        assertSame(firstResult, result1)
        assertSame(secondResult, result2)
    }

    @Test
    fun `invoke should handle different filter options`() = runTest {
        // Arrange
        val artistId = 1L
        val yearDescFilter = DiscographyFilter(sortBy = SortOption.YEAR_DESC)
        val titleAscFilter = DiscographyFilter(sortBy = SortOption.TITLE_ASC)
        val yearDescResult = flowOf(PagingData.from(sampleReleases))
        val titleAscResult = flowOf(PagingData.from(sampleReleases.reversed()))

        every { mockRepository.getArtistReleases(artistId, yearDescFilter) } returns yearDescResult
        every { mockRepository.getArtistReleases(artistId, titleAscFilter) } returns titleAscResult

        // Act
        val result1 = getArtistReleasesUseCase.invoke(artistId, yearDescFilter)
        val result2 = getArtistReleasesUseCase.invoke(artistId, titleAscFilter)

        // Assert
        verify { mockRepository.getArtistReleases(artistId, yearDescFilter) }
        verify { mockRepository.getArtistReleases(artistId, titleAscFilter) }
        assertSame(yearDescResult, result1)
        assertSame(titleAscResult, result2)
    }

    @Test
    fun `invoke should handle negative artist id`() = runTest {
        // Arrange
        val artistId = -1L
        val filter = DiscographyFilter()
        val emptyResult = flowOf(PagingData.empty<Release>())

        every { mockRepository.getArtistReleases(artistId, filter) } returns emptyResult

        // Act
        val result = getArtistReleasesUseCase.invoke(artistId, filter)

        // Assert
        verify { mockRepository.getArtistReleases(artistId, filter) }
        assertSame(emptyResult, result)
    }

    @Test
    fun `invoke should handle zero artist id`() = runTest {
        // Arrange
        val artistId = 0L
        val filter = DiscographyFilter()
        val emptyResult = flowOf(PagingData.empty<Release>())

        every { mockRepository.getArtistReleases(artistId, filter) } returns emptyResult

        // Act
        val result = getArtistReleasesUseCase.invoke(artistId, filter)

        // Assert
        verify { mockRepository.getArtistReleases(artistId, filter) }
        assertSame(emptyResult, result)
    }

    @Test
    fun `invoke should handle large artist id`() = runTest {
        // Arrange
        val artistId = Long.MAX_VALUE
        val filter = DiscographyFilter()
        val expectedResult = flowOf(PagingData.from(sampleReleases))

        every { mockRepository.getArtistReleases(artistId, filter) } returns expectedResult

        // Act
        val result = getArtistReleasesUseCase.invoke(artistId, filter)

        // Assert
        verify { mockRepository.getArtistReleases(artistId, filter) }
        assertSame(expectedResult, result)
    }

    @Test
    fun `invoke should pass through repository result unchanged`() = runTest {
        // Arrange
        val artistId = 42L
        val filter = DiscographyFilter()
        val repositoryResult = flowOf(PagingData.from(sampleReleases))

        every { mockRepository.getArtistReleases(artistId, filter) } returns repositoryResult

        // Act
        val useCaseResult = getArtistReleasesUseCase.invoke(artistId, filter)

        // Assert
        verify { mockRepository.getArtistReleases(artistId, filter) }
        assertSame(repositoryResult, useCaseResult)
    }

    @Test
    fun `invoke should verify exact parameter matching`() = runTest {
        // Arrange
        val artistId = 555L
        val filter = DiscographyFilter(sortBy = SortOption.YEAR_ASC)
        val expectedResult = flowOf(PagingData.from(sampleReleases))

        every { mockRepository.getArtistReleases(artistId, filter) } returns expectedResult

        // Act
        getArtistReleasesUseCase.invoke(artistId, filter)

        // Assert
        verify(exactly = 1) { mockRepository.getArtistReleases(eq(artistId), eq(filter)) }
        verify(exactly = 1) { mockRepository.getArtistReleases(any(), any()) }
    }

    @Test
    fun `invoke operator should work correctly`() = runTest {
        // Arrange
        val artistId = 777L
        val filter = DiscographyFilter()
        val expectedResult = flowOf(PagingData.from(sampleReleases))

        every { mockRepository.getArtistReleases(artistId, filter) } returns expectedResult

        // Act - Using operator invoke syntax
        val result = getArtistReleasesUseCase(artistId, filter)

        // Assert
        verify { mockRepository.getArtistReleases(artistId, filter) }
        assertSame(expectedResult, result)
    }

    @Test
    fun `invoke should handle multiple calls with same parameters`() = runTest {
        // Arrange
        val artistId = 888L
        val filter = DiscographyFilter()
        val expectedResult = flowOf(PagingData.from(sampleReleases))

        every { mockRepository.getArtistReleases(artistId, filter) } returns expectedResult

        // Act
        val result1 = getArtistReleasesUseCase.invoke(artistId, filter)
        val result2 = getArtistReleasesUseCase.invoke(artistId, filter)

        // Assert
        verify(exactly = 2) { mockRepository.getArtistReleases(artistId, filter) }
        assertSame(expectedResult, result1)
        assertSame(expectedResult, result2)
    }

    @Test
    fun `invoke should handle empty releases result`() = runTest {
        // Arrange
        val artistId = 999L
        val filter = DiscographyFilter()
        val emptyResult = flowOf(PagingData.empty<Release>())

        every { mockRepository.getArtistReleases(artistId, filter) } returns emptyResult

        // Act
        val result = getArtistReleasesUseCase.invoke(artistId, filter)
        val pagingData = result.first()

        // Assert
        verify { mockRepository.getArtistReleases(artistId, filter) }
        assertNotNull("PagingData should not be null", pagingData)
        assertSame(emptyResult, result)
    }

    @Test
    fun `invoke should handle all sort options correctly`() = runTest {
        // Arrange
        val artistId = 1L
        val sortOptions = listOf(
            SortOption.YEAR_DESC,
            SortOption.YEAR_ASC,
            SortOption.TITLE_ASC,
            SortOption.TITLE_DESC,
            SortOption.FORMAT_ASC
        )
        val expectedResult = flowOf(PagingData.from(sampleReleases))

        sortOptions.forEach { sortOption ->
            val filter = DiscographyFilter(sortBy = sortOption)
            every { mockRepository.getArtistReleases(artistId, filter) } returns expectedResult
        }

        // Act & Assert
        sortOptions.forEach { sortOption ->
            val filter = DiscographyFilter(sortBy = sortOption)
            val result = getArtistReleasesUseCase.invoke(artistId, filter)

            verify { mockRepository.getArtistReleases(artistId, filter) }
            assertSame(expectedResult, result)
        }
    }

    @Test
    fun `invoke should handle special characters in release data`() = runTest {
        // Arrange
        val artistId = 100L
        val filter = DiscographyFilter()
        val specialReleases = listOf(
            Release(1, "Sgt. Pepper's Lonely Hearts Club Band", 1967, "Album", "Main", null),
            Release(2, "A Hard Day's Night", 1964, "Album", "Main", null),
            Release(3, "Can't Buy Me Love", 1964, "Single", "Main", null)
        )
        val expectedResult = flowOf(PagingData.from(specialReleases))

        every { mockRepository.getArtistReleases(artistId, filter) } returns expectedResult

        // Act
        val result = getArtistReleasesUseCase.invoke(artistId, filter)

        // Assert
        verify { mockRepository.getArtistReleases(artistId, filter) }
        assertSame(expectedResult, result)
    }
}
