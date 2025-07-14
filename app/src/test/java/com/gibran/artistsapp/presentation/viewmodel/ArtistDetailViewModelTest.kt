package com.gibran.artistsapp.presentation.viewmodel

import com.gibran.artistsapp.domain.model.ArtistDetail
import com.gibran.artistsapp.domain.usecase.GetArtistDetailUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ArtistDetailViewModelTest {

    private lateinit var viewModel: ArtistDetailViewModel
    private lateinit var mockGetArtistDetailUseCase: GetArtistDetailUseCase

    private val testDispatcher = StandardTestDispatcher()

    private val sampleArtistDetail = ArtistDetail(
        id = 1,
        name = "The Beatles",
        profile = "The Beatles were an English rock band formed in Liverpool in 1960.",
        images = listOf(),
        members = listOf()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockGetArtistDetailUseCase = mockk(relaxed = true)
        viewModel = ArtistDetailViewModel(mockGetArtistDetailUseCase)
    }

    @Test
    fun `initial state should be loading`() {
        // Arrange & Act
        val initialState = viewModel.event.value

        // Assert
        assertTrue("Initial state should be Loading", initialState is ArtistDetailEvent.Loading)
    }

    @Test
    fun `onIntent with Load should call use case and emit success event`() = runTest {
        // Arrange
        val artistId = 1L
        val intent = ArtistDetailIntent.Load(artistId)

        coEvery { mockGetArtistDetailUseCase(artistId) } returns Result.success(sampleArtistDetail)

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify { mockGetArtistDetailUseCase(artistId) }
        assertTrue("Event should be Success", viewModel.event.value is ArtistDetailEvent.Success)
        assertEquals(
            sampleArtistDetail,
            (viewModel.event.value as ArtistDetailEvent.Success).detail
        )
    }

    @Test
    fun `onIntent with Load should emit error event when use case fails`() = runTest {
        // Arrange
        val artistId = 1L
        val intent = ArtistDetailIntent.Load(artistId)
        val errorMessage = "Network error"
        val exception = Exception(errorMessage)

        coEvery { mockGetArtistDetailUseCase(artistId) } returns Result.failure(exception)

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify { mockGetArtistDetailUseCase(artistId) }
        assertTrue("Event should be Error", viewModel.event.value is ArtistDetailEvent.Error)
        assertEquals(
            errorMessage,
            (viewModel.event.value as ArtistDetailEvent.Error).message
        )
    }

    @Test
    fun `onIntent with Load should emit error with default message when exception message is null`() =
        runTest {
            // Arrange
            val artistId = 1L
            val intent = ArtistDetailIntent.Load(artistId)
            val exception = Exception(null as String?)

            coEvery { mockGetArtistDetailUseCase(artistId) } returns Result.failure(exception)

            // Act
            viewModel.onIntent(intent)
            advanceUntilIdle()

            // Assert
            coVerify { mockGetArtistDetailUseCase(artistId) }
            assertTrue("Event should be Error", viewModel.event.value is ArtistDetailEvent.Error)
            assertEquals(
                "Error desconocido",
                (viewModel.event.value as ArtistDetailEvent.Error).message
            )
        }

    @Test
    fun `onIntent with Retry should call use case and emit success event`() = runTest {
        // Arrange
        val artistId = 2L
        val intent = ArtistDetailIntent.Retry(artistId)

        coEvery { mockGetArtistDetailUseCase(artistId) } returns Result.success(sampleArtistDetail)

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify { mockGetArtistDetailUseCase(artistId) }
        assertTrue("Event should be Success", viewModel.event.value is ArtistDetailEvent.Success)
        assertEquals(
            sampleArtistDetail,
            (viewModel.event.value as ArtistDetailEvent.Success).detail
        )
    }

    @Test
    fun `onIntent with Retry should emit error event when use case fails`() = runTest {
        // Arrange
        val artistId = 2L
        val intent = ArtistDetailIntent.Retry(artistId)
        val errorMessage = "Retry failed"
        val exception = Exception(errorMessage)

        coEvery { mockGetArtistDetailUseCase(artistId) } returns Result.failure(exception)

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify { mockGetArtistDetailUseCase(artistId) }
        assertTrue("Event should be Error", viewModel.event.value is ArtistDetailEvent.Error)
        assertEquals(
            errorMessage,
            (viewModel.event.value as ArtistDetailEvent.Error).message
        )
    }

    @Test
    fun `should emit loading state before calling use case`() = runTest {
        // Arrange
        val artistId = 1L
        val intent = ArtistDetailIntent.Load(artistId)

        coEvery { mockGetArtistDetailUseCase(artistId) } returns Result.success(sampleArtistDetail)

        // Act
        viewModel.onIntent(intent)

        // Assert - Check loading state immediately
        assertTrue(
            "State should be Loading initially",
            viewModel.event.value is ArtistDetailEvent.Loading
        )

        advanceUntilIdle()

        // Assert - Check final state
        assertTrue(
            "State should be Success after completion",
            viewModel.event.value is ArtistDetailEvent.Success
        )
    }

    @Test
    fun `should handle different artist ids correctly`() = runTest {
        // Arrange
        val firstArtistId = 1L
        val secondArtistId = 2L
        val firstArtist = sampleArtistDetail.copy(id = firstArtistId, name = "The Beatles")
        val secondArtist = sampleArtistDetail.copy(id = secondArtistId, name = "Queen")

        coEvery { mockGetArtistDetailUseCase(firstArtistId) } returns Result.success(firstArtist)
        coEvery { mockGetArtistDetailUseCase(secondArtistId) } returns Result.success(secondArtist)

        // Act & Assert - First artist
        viewModel.onIntent(ArtistDetailIntent.Load(firstArtistId))
        advanceUntilIdle()

        coVerify { mockGetArtistDetailUseCase(firstArtistId) }
        assertEquals(
            firstArtist,
            (viewModel.event.value as ArtistDetailEvent.Success).detail
        )

        // Act & Assert - Second artist
        viewModel.onIntent(ArtistDetailIntent.Load(secondArtistId))
        advanceUntilIdle()

        coVerify { mockGetArtistDetailUseCase(secondArtistId) }
        assertEquals(
            secondArtist,
            (viewModel.event.value as ArtistDetailEvent.Success).detail
        )
    }

    @Test
    fun `should handle load then retry sequence correctly`() = runTest {
        // Arrange
        val artistId = 1L
        val loadIntent = ArtistDetailIntent.Load(artistId)
        val retryIntent = ArtistDetailIntent.Retry(artistId)

        coEvery { mockGetArtistDetailUseCase(artistId) } returns Result.success(sampleArtistDetail)

        // Act
        viewModel.onIntent(loadIntent)
        advanceUntilIdle()

        viewModel.onIntent(retryIntent)
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 2) { mockGetArtistDetailUseCase(artistId) }
        assertTrue(
            "Final state should be Success",
            viewModel.event.value is ArtistDetailEvent.Success
        )
    }

    @Test
    fun `should handle multiple consecutive load intents`() = runTest {
        // Arrange
        val artistId = 1L
        val intent = ArtistDetailIntent.Load(artistId)

        coEvery { mockGetArtistDetailUseCase(artistId) } returns Result.success(sampleArtistDetail)

        // Act
        viewModel.onIntent(intent)
        viewModel.onIntent(intent)
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 3) { mockGetArtistDetailUseCase(artistId) }
        assertTrue(
            "Final state should be Success",
            viewModel.event.value is ArtistDetailEvent.Success
        )
    }

    @Test
    fun `should handle error recovery scenario`() = runTest {
        // Arrange
        val artistId = 1L
        val exception = Exception("Network error")

        // First call fails, second succeeds
        coEvery { mockGetArtistDetailUseCase(artistId) } returnsMany listOf(
            Result.failure(exception),
            Result.success(sampleArtistDetail)
        )

        // Act - First call (Load) fails
        viewModel.onIntent(ArtistDetailIntent.Load(artistId))
        advanceUntilIdle()

        // Assert - Error state
        assertTrue(
            "State should be Error after first call",
            viewModel.event.value is ArtistDetailEvent.Error
        )

        // Act - Retry succeeds
        viewModel.onIntent(ArtistDetailIntent.Retry(artistId))
        advanceUntilIdle()

        // Assert - Success state
        coVerify(exactly = 2) { mockGetArtistDetailUseCase(artistId) }
        assertTrue(
            "State should be Success after retry",
            viewModel.event.value is ArtistDetailEvent.Success
        )
        assertEquals(
            sampleArtistDetail,
            (viewModel.event.value as ArtistDetailEvent.Success).detail
        )
    }

    @Test
    fun `should verify exact parameter matching for use case calls`() = runTest {
        // Arrange
        val artistId = 123L
        val intent = ArtistDetailIntent.Load(artistId)

        coEvery { mockGetArtistDetailUseCase(artistId) } returns Result.success(sampleArtistDetail)

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { mockGetArtistDetailUseCase(eq(artistId)) }
        coVerify(exactly = 1) { mockGetArtistDetailUseCase(any()) }
    }

    @Test
    fun `should handle negative artist id`() = runTest {
        // Arrange
        val negativeArtistId = -1L
        val intent = ArtistDetailIntent.Load(negativeArtistId)
        val exception = IllegalArgumentException("Invalid artist ID")

        coEvery { mockGetArtistDetailUseCase(negativeArtistId) } returns Result.failure(exception)

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify { mockGetArtistDetailUseCase(negativeArtistId) }
        assertTrue("State should be Error", viewModel.event.value is ArtistDetailEvent.Error)
        assertEquals(
            "Invalid artist ID",
            (viewModel.event.value as ArtistDetailEvent.Error).message
        )
    }

    @Test
    fun `should handle large artist id`() = runTest {
        // Arrange
        val largeArtistId = Long.MAX_VALUE
        val intent = ArtistDetailIntent.Load(largeArtistId)
        val largeArtist = sampleArtistDetail.copy(id = largeArtistId)

        coEvery { mockGetArtistDetailUseCase(largeArtistId) } returns Result.success(largeArtist)

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify { mockGetArtistDetailUseCase(largeArtistId) }
        assertTrue("State should be Success", viewModel.event.value is ArtistDetailEvent.Success)
        assertEquals(
            largeArtist,
            (viewModel.event.value as ArtistDetailEvent.Success).detail
        )
    }

    @Test
    fun `should handle timeout exception correctly`() = runTest {
        // Arrange
        val artistId = 1L
        val intent = ArtistDetailIntent.Load(artistId)
        val exception = java.util.concurrent.TimeoutException("Request timeout")

        coEvery { mockGetArtistDetailUseCase(artistId) } returns Result.failure(exception)

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify { mockGetArtistDetailUseCase(artistId) }
        assertTrue("State should be Error", viewModel.event.value is ArtistDetailEvent.Error)
        assertEquals(
            "Request timeout",
            (viewModel.event.value as ArtistDetailEvent.Error).message
        )
    }

    @Test
    fun `should handle network exception correctly`() = runTest {
        // Arrange
        val artistId = 1L
        val intent = ArtistDetailIntent.Load(artistId)
        val exception = RuntimeException("Network connection failed")

        coEvery { mockGetArtistDetailUseCase(artistId) } returns Result.failure(exception)

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify { mockGetArtistDetailUseCase(artistId) }
        assertTrue("State should be Error", viewModel.event.value is ArtistDetailEvent.Error)
        assertEquals(
            "Network connection failed",
            (viewModel.event.value as ArtistDetailEvent.Error).message
        )
    }
}
