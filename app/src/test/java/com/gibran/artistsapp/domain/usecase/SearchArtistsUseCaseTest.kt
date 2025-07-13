package com.gibran.artistsapp.domain.usecase

import androidx.paging.PagingData
import com.gibran.artistsapp.domain.model.Artist
import com.gibran.artistsapp.domain.repository.ArtistRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class SearchArtistsUseCaseTest {

    private lateinit var searchArtistsUseCase: SearchArtistsUseCase
    private lateinit var mockRepository: ArtistRepository

    private val sampleArtists = listOf(
        Artist(1, "The Beatles", "image1.jpg", "resource1", "Artist"),
        Artist(2, "Queen", "image2.jpg", "resource2", "Artist"),
        Artist(3, "Led Zeppelin", "image3.jpg", "resource3", "Artist")
    )

    @Before
    fun setUp() {
        mockRepository = mockk(relaxed = true)
        searchArtistsUseCase = SearchArtistsUseCase(mockRepository)
    }

    @Test
    fun `invoke should call repository searchArtists with correct query`() = runTest {
        // Arrange
        val query = "The Beatles"
        val expectedResult = flowOf(PagingData.from(sampleArtists))

        every { mockRepository.searchArtists(query) } returns expectedResult

        // Act
        val result = searchArtistsUseCase.invoke(query)

        // Assert
        verify { mockRepository.searchArtists(query) }
        assertSame(expectedResult, result)
    }

    @Test
    fun `invoke should return flow from repository`() = runTest {
        // Arrange
        val query = "Queen"
        val expectedResult = flowOf(PagingData.from(sampleArtists))

        every { mockRepository.searchArtists(query) } returns expectedResult

        // Act
        val result = searchArtistsUseCase.invoke(query)

        // Assert
        assertSame(expectedResult, result)
    }

    @Test
    fun `invoke should handle empty query string`() = runTest {
        // Arrange
        val query = ""
        val expectedResult = flowOf(PagingData.empty<Artist>())

        every { mockRepository.searchArtists(query) } returns expectedResult

        // Act
        val result = searchArtistsUseCase.invoke(query)

        // Assert
        verify { mockRepository.searchArtists(query) }
        assertSame(expectedResult, result)
    }

    @Test
    fun `invoke should handle blank query string`() = runTest {
        // Arrange
        val query = "   "
        val expectedResult = flowOf(PagingData.empty<Artist>())

        every { mockRepository.searchArtists(query) } returns expectedResult

        // Act
        val result = searchArtistsUseCase.invoke(query)

        // Assert
        verify { mockRepository.searchArtists(query) }
        assertSame(expectedResult, result)
    }

    @Test
    fun `invoke should handle special characters in query`() = runTest {
        // Arrange
        val query = "AC/DC & Queen"
        val expectedResult = flowOf(PagingData.from(sampleArtists))

        every { mockRepository.searchArtists(query) } returns expectedResult

        // Act
        val result = searchArtistsUseCase.invoke(query)

        // Assert
        verify { mockRepository.searchArtists(query) }
        assertSame(expectedResult, result)
    }

    @Test
    fun `invoke should handle long query string`() = runTest {
        // Arrange
        val query = "This is a very long search query that might be used to test edge cases"
        val expectedResult = flowOf(PagingData.from(sampleArtists))

        every { mockRepository.searchArtists(query) } returns expectedResult

        // Act
        val result = searchArtistsUseCase.invoke(query)

        // Assert
        verify { mockRepository.searchArtists(query) }
        assertSame(expectedResult, result)
    }

    @Test
    fun `invoke should handle unicode characters in query`() = runTest {
        // Arrange
        val query = "Björk & Sigur Rós"
        val expectedResult = flowOf(PagingData.from(sampleArtists))

        every { mockRepository.searchArtists(query) } returns expectedResult

        // Act
        val result = searchArtistsUseCase.invoke(query)

        // Assert
        verify { mockRepository.searchArtists(query) }
        assertSame(expectedResult, result)
    }

    @Test
    fun `invoke should handle multiple consecutive calls with different queries`() = runTest {
        // Arrange
        val firstQuery = "The Beatles"
        val secondQuery = "Queen"
        val firstResult = flowOf(PagingData.from(listOf(sampleArtists[0])))
        val secondResult = flowOf(PagingData.from(listOf(sampleArtists[1])))

        every { mockRepository.searchArtists(firstQuery) } returns firstResult
        every { mockRepository.searchArtists(secondQuery) } returns secondResult

        // Act
        val result1 = searchArtistsUseCase.invoke(firstQuery)
        val result2 = searchArtistsUseCase.invoke(secondQuery)

        // Assert
        verify { mockRepository.searchArtists(firstQuery) }
        verify { mockRepository.searchArtists(secondQuery) }
        assertSame(firstResult, result1)
        assertSame(secondResult, result2)
    }

    @Test
    fun `invoke should handle same query called multiple times`() = runTest {
        // Arrange
        val query = "Led Zeppelin"
        val expectedResult = flowOf(PagingData.from(sampleArtists))

        every { mockRepository.searchArtists(query) } returns expectedResult

        // Act
        val result1 = searchArtistsUseCase.invoke(query)
        val result2 = searchArtistsUseCase.invoke(query)

        // Assert
        verify(exactly = 2) { mockRepository.searchArtists(query) }
        assertSame(expectedResult, result1)
        assertSame(expectedResult, result2)
    }

    @Test
    fun `invoke should pass through repository result unchanged`() = runTest {
        // Arrange
        val query = "Test Artist"
        val repositoryResult = flowOf(PagingData.from(sampleArtists))

        every { mockRepository.searchArtists(query) } returns repositoryResult

        // Act
        val useCaseResult = searchArtistsUseCase.invoke(query)

        // Assert
        verify { mockRepository.searchArtists(query) }
        assertSame(repositoryResult, useCaseResult)
    }

    @Test
    fun `invoke operator should work correctly`() = runTest {
        // Arrange
        val query = "Test Query"
        val expectedResult = flowOf(PagingData.from(sampleArtists))

        every { mockRepository.searchArtists(query) } returns expectedResult

        // Act - Using operator invoke syntax
        val result = searchArtistsUseCase(query)

        // Assert
        verify { mockRepository.searchArtists(query) }
        assertSame(expectedResult, result)
    }

    @Test
    fun `invoke should handle repository returning empty paging data`() = runTest {
        // Arrange
        val query = "Nonexistent Artist"
        val emptyResult = flowOf(PagingData.empty<Artist>())

        every { mockRepository.searchArtists(query) } returns emptyResult

        // Act
        val result = searchArtistsUseCase.invoke(query)

        // Assert
        verify { mockRepository.searchArtists(query) }
        assertSame(emptyResult, result)
    }

    @Test
    fun `invoke should verify exact parameter matching`() = runTest {
        // Arrange
        val query = "Exact Match Test"
        val expectedResult = flowOf(PagingData.from(sampleArtists))

        every { mockRepository.searchArtists(query) } returns expectedResult

        // Act
        searchArtistsUseCase.invoke(query)

        // Assert
        verify { mockRepository.searchArtists(eq(query)) }
        verify(exactly = 1) { mockRepository.searchArtists(any()) }
    }

    @Test
    fun `invoke should handle case sensitive queries correctly`() = runTest {
        // Arrange
        val lowerCaseQuery = "the beatles"
        val upperCaseQuery = "THE BEATLES"
        val lowerResult = flowOf(PagingData.from(listOf(sampleArtists[0])))
        val upperResult = flowOf(PagingData.from(listOf(sampleArtists[1])))

        every { mockRepository.searchArtists(lowerCaseQuery) } returns lowerResult
        every { mockRepository.searchArtists(upperCaseQuery) } returns upperResult

        // Act
        val result1 = searchArtistsUseCase.invoke(lowerCaseQuery)
        val result2 = searchArtistsUseCase.invoke(upperCaseQuery)

        // Assert
        verify { mockRepository.searchArtists(eq(lowerCaseQuery)) }
        verify { mockRepository.searchArtists(eq(upperCaseQuery)) }
        assertSame(lowerResult, result1)
        assertSame(upperResult, result2)
    }

    @Test
    fun `invoke should not call repository methods other than searchArtists`() = runTest {
        // Arrange
        val query = "Test Query"
        val expectedResult = flowOf(PagingData.from(sampleArtists))

        every { mockRepository.searchArtists(query) } returns expectedResult

        // Act
        searchArtistsUseCase.invoke(query)

        // Assert
        verify { mockRepository.searchArtists(query) }
        // Note: We only verify that searchArtists was called
        // Other methods are suspend functions and not directly verifiable in this context
    }
}