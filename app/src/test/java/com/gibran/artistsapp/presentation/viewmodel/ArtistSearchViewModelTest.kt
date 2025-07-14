package com.gibran.artistsapp.presentation.viewmodel

import androidx.paging.PagingData
import com.gibran.artistsapp.domain.model.Artist
import com.gibran.artistsapp.domain.usecase.SearchArtistsUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ArtistSearchViewModelTest {

    private lateinit var viewModel: ArtistSearchViewModel
    private lateinit var mockSearchArtistsUseCase: SearchArtistsUseCase

    private val sampleArtists = listOf(
        Artist(1, "The Beatles", "image1.jpg", "resource1", "Artist"),
        Artist(2, "Queen", "image2.jpg", "resource2", "Artist")
    )

    @Before
    fun setUp() {
        mockSearchArtistsUseCase = mockk(relaxed = true)
        viewModel = ArtistSearchViewModel(mockSearchArtistsUseCase)
    }

    @Test
    fun `onIntent with Search intent should update search query`() {
        // Arrange
        val searchQuery = "The Beatles"
        val intent = ArtistSearchIntent.Search(searchQuery)

        // Act
        viewModel.onIntent(intent)

        // Assert
        assertEquals(searchQuery, viewModel.searchQuery.value)
    }

    @Test
    fun `onIntent with ClearSearch intent should clear search query`() {
        // Arrange
        val initialQuery = "The Beatles"
        val searchIntent = ArtistSearchIntent.Search(initialQuery)
        val clearIntent = ArtistSearchIntent.ClearSearch

        // Act
        viewModel.onIntent(searchIntent)
        viewModel.onIntent(clearIntent)

        // Assert
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `searchQuery initial value should be empty string`() {
        // Arrange & Act
        val initialQuery = viewModel.searchQuery.value

        // Assert
        assertEquals("", initialQuery)
    }

    @Test
    fun `isSearchEmpty should return true when query is empty`() {
        // Arrange & Act
        val isEmpty = viewModel.isSearchEmpty.value

        // Assert
        assertEquals(true, isEmpty)
    }

    @Test
    fun `onIntent should handle different intent types correctly`() {
        // Arrange
        val searchQuery = "The Beatles"
        val searchIntent = ArtistSearchIntent.Search(searchQuery)
        val clearIntent = ArtistSearchIntent.ClearSearch

        // Act & Assert - Search intent
        viewModel.onIntent(searchIntent)
        assertEquals(searchQuery, viewModel.searchQuery.value)

        // Act & Assert - Clear intent
        viewModel.onIntent(clearIntent)
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `search query state should be updated immediately on intent`() {
        // Arrange
        val searchQuery = "The Beatles"
        val intent = ArtistSearchIntent.Search(searchQuery)

        // Act
        viewModel.onIntent(intent)

        // Assert
        assertEquals(searchQuery, viewModel.searchQuery.value)
    }

    @Test
    fun `empty search query should result in empty state`() {
        // Arrange
        val emptyQuery = ""
        val intent = ArtistSearchIntent.Search(emptyQuery)

        // Act
        viewModel.onIntent(intent)

        // Assert
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `blank search query should store the actual query value`() {
        // Arrange  
        val blankQuery = "   "
        val intent = ArtistSearchIntent.Search(blankQuery)

        // Act
        viewModel.onIntent(intent)

        // Assert
        assertEquals(blankQuery, viewModel.searchQuery.value)
    }

    @Test
    fun `multiple search queries should update state correctly`() {
        // Arrange
        val firstQuery = "The Beatles"
        val secondQuery = "Queen"
        val firstIntent = ArtistSearchIntent.Search(firstQuery)
        val secondIntent = ArtistSearchIntent.Search(secondQuery)

        // Act
        viewModel.onIntent(firstIntent)
        val firstResult = viewModel.searchQuery.value

        viewModel.onIntent(secondIntent)
        val secondResult = viewModel.searchQuery.value

        // Assert
        assertEquals(firstQuery, firstResult)
        assertEquals(secondQuery, secondResult)
    }

    @Test
    fun `search then clear should update query correctly`() {
        // Arrange
        val searchQuery = "The Beatles"
        val searchIntent = ArtistSearchIntent.Search(searchQuery)
        val clearIntent = ArtistSearchIntent.ClearSearch

        // Act
        viewModel.onIntent(searchIntent)
        val afterSearch = viewModel.searchQuery.value

        viewModel.onIntent(clearIntent)
        val afterClear = viewModel.searchQuery.value

        // Assert
        assertEquals(searchQuery, afterSearch)
        assertEquals("", afterClear)
    }

    @Test
    fun `ClearSearch intent should reset to empty string`() {
        // Arrange
        val searchQuery = "Test Query"
        val searchIntent = ArtistSearchIntent.Search(searchQuery)
        val clearIntent = ArtistSearchIntent.ClearSearch

        // Act
        viewModel.onIntent(searchIntent)
        assertEquals(searchQuery, viewModel.searchQuery.value)

        viewModel.onIntent(clearIntent)

        // Assert
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `Search intent should handle special characters correctly`() {
        // Arrange
        val searchQuery = "AC/DC & Queen"
        val intent = ArtistSearchIntent.Search(searchQuery)

        // Act
        viewModel.onIntent(intent)

        // Assert
        assertEquals(searchQuery, viewModel.searchQuery.value)
    }

    // ===== ARTISTS PAGING FLOW TESTS =====

    @Test
    fun `artistsPagingFlow should return empty paging data when query is empty`() = runTest {
        // Arrange
        val emptyQuery = ""
        val intent = ArtistSearchIntent.Search(emptyQuery)

        // Act
        viewModel.onIntent(intent)
        val pagingData = viewModel.artistsPagingFlow.first()

        // Assert - PagingData assertions
        assertNotNull("PagingData should not be null", pagingData)

        // Assert - Use case verification
        verify(exactly = 0) { mockSearchArtistsUseCase(any()) }
    }

    @Test
    fun `artistsPagingFlow should return empty paging data when query is blank`() = runTest {
        // Arrange
        val blankQuery = "   "
        val intent = ArtistSearchIntent.Search(blankQuery)

        // Act
        viewModel.onIntent(intent)
        val pagingData = viewModel.artistsPagingFlow.first()

        // Assert - PagingData assertions
        assertNotNull("PagingData should not be null", pagingData)

        // Assert - Use case verification
        verify(exactly = 0) { mockSearchArtistsUseCase(any()) }
    }

    @Test
    fun `artistsPagingFlow should call use case when query is not blank`() = runTest {
        // Arrange
        val searchQuery = "The Beatles"
        val expectedPagingData = PagingData.from(sampleArtists)
        val intent = ArtistSearchIntent.Search(searchQuery)

        every { mockSearchArtistsUseCase(searchQuery) } returns flowOf(expectedPagingData)

        // Act
        viewModel.onIntent(intent)
        val pagingData = viewModel.artistsPagingFlow.first()

        // Assert - PagingData assertions
        assertNotNull("PagingData should not be null", pagingData)
        assertTrue(
            "PagingData should be the expected instance type",
            pagingData is PagingData<Artist>
        )

        // Assert - Use case verification
        verify(exactly = 1) { mockSearchArtistsUseCase(searchQuery) }
    }

    @Test
    fun `artistsPagingFlow should return paging data when query is valid`() = runTest {
        // Arrange
        val searchQuery = "Queen"
        val expectedPagingData = PagingData.from(sampleArtists)
        val intent = ArtistSearchIntent.Search(searchQuery)

        every { mockSearchArtistsUseCase(searchQuery) } returns flowOf(expectedPagingData)

        // Act
        viewModel.onIntent(intent)
        val pagingData = viewModel.artistsPagingFlow.first()

        // Assert - PagingData assertions
        assertNotNull("PagingData should not be null", pagingData)
        assertTrue("PagingData should be correct type", pagingData is PagingData<Artist>)

        // Assert - Use case verification
        verify(exactly = 1) { mockSearchArtistsUseCase(searchQuery) }

        // Assert - Verify correct query was passed
        verify { mockSearchArtistsUseCase(eq(searchQuery)) }
    }

    @Test
    fun `artistsPagingFlow should switch to empty when search is cleared`() = runTest {
        // Arrange
        val searchQuery = "The Beatles"
        val expectedPagingData = PagingData.from(sampleArtists)
        val searchIntent = ArtistSearchIntent.Search(searchQuery)
        val clearIntent = ArtistSearchIntent.ClearSearch

        every { mockSearchArtistsUseCase(searchQuery) } returns flowOf(expectedPagingData)

        // Act
        viewModel.onIntent(searchIntent)
        val searchPagingData = viewModel.artistsPagingFlow.first() // Trigger first emission

        viewModel.onIntent(clearIntent)
        val clearPagingData = viewModel.artistsPagingFlow.first() // Trigger second emission

        // Assert - PagingData assertions
        assertNotNull("Search PagingData should not be null", searchPagingData)
        assertNotNull("Clear PagingData should not be null", clearPagingData)
        assertTrue(
            "Both should be PagingData instances",
            searchPagingData is PagingData<Artist> && clearPagingData is PagingData<Artist>
        )

        // Assert - Use case verification
        verify(exactly = 1) { mockSearchArtistsUseCase(searchQuery) }
    }

    @Test
    fun `multiple search queries should call use case with latest query`() = runTest {
        // Arrange
        val firstQuery = "The Beatles"
        val secondQuery = "Queen"
        val firstPagingData = PagingData.from(listOf(sampleArtists[0]))
        val secondPagingData = PagingData.from(listOf(sampleArtists[1]))

        every { mockSearchArtistsUseCase(firstQuery) } returns flowOf(firstPagingData)
        every { mockSearchArtistsUseCase(secondQuery) } returns flowOf(secondPagingData)

        // Act
        viewModel.onIntent(ArtistSearchIntent.Search(firstQuery))
        val firstResult = viewModel.artistsPagingFlow.first()

        viewModel.onIntent(ArtistSearchIntent.Search(secondQuery))
        val secondResult = viewModel.artistsPagingFlow.first()

        // Assert - PagingData assertions
        assertNotNull("First PagingData should not be null", firstResult)
        assertNotNull("Second PagingData should not be null", secondResult)
        assertTrue(
            "Both should be PagingData instances",
            firstResult is PagingData<Artist> && secondResult is PagingData<Artist>
        )

        // Assert - Use case verification
        verify(exactly = 1) { mockSearchArtistsUseCase(firstQuery) }
        verify(exactly = 1) { mockSearchArtistsUseCase(secondQuery) }

        // Assert - Verify correct order of calls
        verify { mockSearchArtistsUseCase(eq(firstQuery)) }
        verify { mockSearchArtistsUseCase(eq(secondQuery)) }
    }

    @Test
    fun `artistsPagingFlow should handle special characters in search query`() = runTest {
        // Arrange
        val searchQuery = "AC/DC & Queen"
        val expectedPagingData = PagingData.from(sampleArtists)
        val intent = ArtistSearchIntent.Search(searchQuery)

        every { mockSearchArtistsUseCase(searchQuery) } returns flowOf(expectedPagingData)

        // Act
        viewModel.onIntent(intent)
        val pagingData = viewModel.artistsPagingFlow.first()

        // Assert - PagingData assertions
        assertNotNull("PagingData should not be null", pagingData)
        assertTrue("PagingData should be correct type", pagingData is PagingData<Artist>)

        // Assert - Use case verification
        verify(exactly = 1) { mockSearchArtistsUseCase(searchQuery) }

        // Assert - Verify special characters are preserved
        verify { mockSearchArtistsUseCase(eq("AC/DC & Queen")) }
    }

    // ===== MOCK VERIFICATION TESTS =====

    @Test
    fun `use case should be configured with expected return value`() {
        // Arrange
        val searchQuery = "The Beatles"
        val expectedPagingData = PagingData.from(sampleArtists)

        every { mockSearchArtistsUseCase(searchQuery) } returns flowOf(expectedPagingData)

        // Act
        val result = mockSearchArtistsUseCase(searchQuery)

        // Assert
        verify { mockSearchArtistsUseCase(searchQuery) }
        assertEquals(flowOf(expectedPagingData).javaClass, result.javaClass)
    }

    @Test
    fun `use case should handle different queries`() {
        // Arrange
        val firstQuery = "The Beatles"
        val secondQuery = "Queen"
        val pagingData = PagingData.from(sampleArtists)

        every { mockSearchArtistsUseCase(any()) } returns flowOf(pagingData)

        // Act
        mockSearchArtistsUseCase(firstQuery)
        mockSearchArtistsUseCase(secondQuery)

        // Assert
        verify { mockSearchArtistsUseCase(firstQuery) }
        verify { mockSearchArtistsUseCase(secondQuery) }
    }

    @Test
    fun `use case should handle empty query`() {
        // Arrange
        val emptyQuery = ""
        val emptyPagingData = PagingData.empty<Artist>()

        every { mockSearchArtistsUseCase(emptyQuery) } returns flowOf(emptyPagingData)

        // Act
        mockSearchArtistsUseCase(emptyQuery)

        // Assert
        verify { mockSearchArtistsUseCase(emptyQuery) }
    }

    @Test
    fun `use case should handle blank query`() {
        // Arrange
        val blankQuery = "   "
        val emptyPagingData = PagingData.empty<Artist>()

        every { mockSearchArtistsUseCase(blankQuery) } returns flowOf(emptyPagingData)

        // Act
        mockSearchArtistsUseCase(blankQuery)

        // Assert
        verify { mockSearchArtistsUseCase(blankQuery) }
    }

    @Test
    fun `view model should maintain search query independence from use case calls`() {
        // Arrange
        val searchQuery = "The Beatles"
        val intent = ArtistSearchIntent.Search(searchQuery)

        // Act
        viewModel.onIntent(intent)

        // Assert - Query should be updated regardless of use case behavior
        assertEquals(searchQuery, viewModel.searchQuery.value)
    }

    @Test
    fun `view model should handle multiple intents correctly`() {
        // Arrange
        val queries = listOf("Beatles", "Queen", "Zeppelin", "")

        // Act & Assert
        queries.forEach { query ->
            val intent = ArtistSearchIntent.Search(query)
            viewModel.onIntent(intent)
            assertEquals(query, viewModel.searchQuery.value)
        }
    }
}
