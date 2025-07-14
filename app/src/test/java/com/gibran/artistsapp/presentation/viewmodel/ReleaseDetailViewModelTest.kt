package com.gibran.artistsapp.presentation.viewmodel

import com.gibran.artistsapp.domain.model.ReleaseDetail
import com.gibran.artistsapp.domain.usecase.GetReleaseDetailUseCase
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
class ReleaseDetailViewModelTest {

    private lateinit var viewModel: ReleaseDetailViewModel
    private lateinit var mockGetReleaseDetailUseCase: GetReleaseDetailUseCase

    private val testDispatcher = StandardTestDispatcher()

    private val sampleReleaseDetail = ReleaseDetail(
        id = 1,
        title = "Abbey Road",
        year = 1969,
        genres = listOf("Rock", "Pop"),
        styles = listOf("Pop Rock", "Psychedelic Pop"),
        tracklist = listOf(),
        images = listOf()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockGetReleaseDetailUseCase = mockk(relaxed = true)
        viewModel = ReleaseDetailViewModel(mockGetReleaseDetailUseCase)
    }

    @Test
    fun `initial state should be loading`() {
        // Arrange & Act
        val initialState = viewModel.event.value

        // Assert
        assertTrue("Initial state should be Loading", initialState is ReleaseDetailEvent.Loading)
    }

    @Test
    fun `onIntent with Load should call use case and emit success event`() = runTest {
        // Arrange
        val releaseId = 1L
        val intent = ReleaseDetailIntent.Load(releaseId)

        coEvery { mockGetReleaseDetailUseCase(releaseId) } returns Result.success(
            sampleReleaseDetail
        )

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify { mockGetReleaseDetailUseCase(releaseId) }
        assertTrue("Event should be Success", viewModel.event.value is ReleaseDetailEvent.Success)
        assertEquals(
            sampleReleaseDetail,
            (viewModel.event.value as ReleaseDetailEvent.Success).detail
        )
    }

    @Test
    fun `onIntent with Load should emit error event when use case fails`() = runTest {
        // Arrange
        val releaseId = 1L
        val intent = ReleaseDetailIntent.Load(releaseId)
        val errorMessage = "Network error"
        val exception = Exception(errorMessage)

        coEvery { mockGetReleaseDetailUseCase(releaseId) } returns Result.failure(exception)

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify { mockGetReleaseDetailUseCase(releaseId) }
        assertTrue("Event should be Error", viewModel.event.value is ReleaseDetailEvent.Error)
        assertEquals(
            errorMessage,
            (viewModel.event.value as ReleaseDetailEvent.Error).message
        )
    }

    @Test
    fun `onIntent with Load should emit error with default message when exception message is null`() =
        runTest {
            // Arrange
            val releaseId = 1L
            val intent = ReleaseDetailIntent.Load(releaseId)
            val exception = Exception(null as String?)

            coEvery { mockGetReleaseDetailUseCase(releaseId) } returns Result.failure(exception)

            // Act
            viewModel.onIntent(intent)
            advanceUntilIdle()

            // Assert
            coVerify { mockGetReleaseDetailUseCase(releaseId) }
            assertTrue("Event should be Error", viewModel.event.value is ReleaseDetailEvent.Error)
            assertEquals(
                "Error desconocido",
                (viewModel.event.value as ReleaseDetailEvent.Error).message
            )
        }

    @Test
    fun `onIntent with Retry should call use case and emit success event`() = runTest {
        // Arrange
        val releaseId = 2L
        val intent = ReleaseDetailIntent.Retry(releaseId)

        coEvery { mockGetReleaseDetailUseCase(releaseId) } returns Result.success(
            sampleReleaseDetail
        )

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify { mockGetReleaseDetailUseCase(releaseId) }
        assertTrue("Event should be Success", viewModel.event.value is ReleaseDetailEvent.Success)
        assertEquals(
            sampleReleaseDetail,
            (viewModel.event.value as ReleaseDetailEvent.Success).detail
        )
    }

    @Test
    fun `onIntent with Retry should emit error event when use case fails`() = runTest {
        // Arrange
        val releaseId = 2L
        val intent = ReleaseDetailIntent.Retry(releaseId)
        val errorMessage = "Retry failed"
        val exception = Exception(errorMessage)

        coEvery { mockGetReleaseDetailUseCase(releaseId) } returns Result.failure(exception)

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify { mockGetReleaseDetailUseCase(releaseId) }
        assertTrue("Event should be Error", viewModel.event.value is ReleaseDetailEvent.Error)
        assertEquals(
            errorMessage,
            (viewModel.event.value as ReleaseDetailEvent.Error).message
        )
    }

    @Test
    fun `should emit loading state before calling use case`() = runTest {
        // Arrange
        val releaseId = 1L
        val intent = ReleaseDetailIntent.Load(releaseId)

        coEvery { mockGetReleaseDetailUseCase(releaseId) } returns Result.success(
            sampleReleaseDetail
        )

        // Act
        viewModel.onIntent(intent)

        // Assert - Check loading state immediately
        assertTrue(
            "State should be Loading initially",
            viewModel.event.value is ReleaseDetailEvent.Loading
        )

        advanceUntilIdle()

        // Assert - Check final state
        assertTrue(
            "State should be Success after completion",
            viewModel.event.value is ReleaseDetailEvent.Success
        )
    }

    @Test
    fun `should handle different release ids correctly`() = runTest {
        // Arrange
        val firstReleaseId = 1L
        val secondReleaseId = 2L
        val firstRelease = sampleReleaseDetail.copy(id = firstReleaseId, title = "Abbey Road")
        val secondRelease =
            sampleReleaseDetail.copy(id = secondReleaseId, title = "Dark Side of the Moon")

        coEvery { mockGetReleaseDetailUseCase(firstReleaseId) } returns Result.success(firstRelease)
        coEvery { mockGetReleaseDetailUseCase(secondReleaseId) } returns Result.success(
            secondRelease
        )

        // Act & Assert - First release
        viewModel.onIntent(ReleaseDetailIntent.Load(firstReleaseId))
        advanceUntilIdle()

        coVerify { mockGetReleaseDetailUseCase(firstReleaseId) }
        assertEquals(
            firstRelease,
            (viewModel.event.value as ReleaseDetailEvent.Success).detail
        )

        // Act & Assert - Second release
        viewModel.onIntent(ReleaseDetailIntent.Load(secondReleaseId))
        advanceUntilIdle()

        coVerify { mockGetReleaseDetailUseCase(secondReleaseId) }
        assertEquals(
            secondRelease,
            (viewModel.event.value as ReleaseDetailEvent.Success).detail
        )
    }

    @Test
    fun `should handle load then retry sequence correctly`() = runTest {
        // Arrange
        val releaseId = 1L
        val loadIntent = ReleaseDetailIntent.Load(releaseId)
        val retryIntent = ReleaseDetailIntent.Retry(releaseId)

        coEvery { mockGetReleaseDetailUseCase(releaseId) } returns Result.success(
            sampleReleaseDetail
        )

        // Act
        viewModel.onIntent(loadIntent)
        advanceUntilIdle()

        viewModel.onIntent(retryIntent)
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 2) { mockGetReleaseDetailUseCase(releaseId) }
        assertTrue(
            "Final state should be Success",
            viewModel.event.value is ReleaseDetailEvent.Success
        )
    }

    @Test
    fun `should handle multiple consecutive load intents`() = runTest {
        // Arrange
        val releaseId = 1L
        val intent = ReleaseDetailIntent.Load(releaseId)

        coEvery { mockGetReleaseDetailUseCase(releaseId) } returns Result.success(
            sampleReleaseDetail
        )

        // Act
        viewModel.onIntent(intent)
        viewModel.onIntent(intent)
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 3) { mockGetReleaseDetailUseCase(releaseId) }
        assertTrue(
            "Final state should be Success",
            viewModel.event.value is ReleaseDetailEvent.Success
        )
    }

    @Test
    fun `should handle error recovery scenario`() = runTest {
        // Arrange
        val releaseId = 1L
        val exception = Exception("Network error")

        // First call fails, second succeeds
        coEvery { mockGetReleaseDetailUseCase(releaseId) } returnsMany listOf(
            Result.failure(exception),
            Result.success(sampleReleaseDetail)
        )

        // Act - First call (Load) fails
        viewModel.onIntent(ReleaseDetailIntent.Load(releaseId))
        advanceUntilIdle()

        // Assert - Error state
        assertTrue(
            "State should be Error after first call",
            viewModel.event.value is ReleaseDetailEvent.Error
        )

        // Act - Retry succeeds
        viewModel.onIntent(ReleaseDetailIntent.Retry(releaseId))
        advanceUntilIdle()

        // Assert - Success state
        coVerify(exactly = 2) { mockGetReleaseDetailUseCase(releaseId) }
        assertTrue(
            "State should be Success after retry",
            viewModel.event.value is ReleaseDetailEvent.Success
        )
        assertEquals(
            sampleReleaseDetail,
            (viewModel.event.value as ReleaseDetailEvent.Success).detail
        )
    }

    @Test
    fun `should verify exact parameter matching for use case calls`() = runTest {
        // Arrange
        val releaseId = 123L
        val intent = ReleaseDetailIntent.Load(releaseId)

        coEvery { mockGetReleaseDetailUseCase(releaseId) } returns Result.success(
            sampleReleaseDetail
        )

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { mockGetReleaseDetailUseCase(eq(releaseId)) }
        coVerify(exactly = 1) { mockGetReleaseDetailUseCase(any()) }
    }

    @Test
    fun `should handle negative release id`() = runTest {
        // Arrange
        val negativeReleaseId = -1L
        val intent = ReleaseDetailIntent.Load(negativeReleaseId)
        val exception = IllegalArgumentException("Invalid release ID")

        coEvery { mockGetReleaseDetailUseCase(negativeReleaseId) } returns Result.failure(exception)

        // Act
        viewModel.onIntent(intent)
        advanceUntilIdle()

        // Assert
        coVerify { mockGetReleaseDetailUseCase(negativeReleaseId) }
        assertTrue("State should be Error", viewModel.event.value is ReleaseDetailEvent.Error)
        assertEquals(
            "Invalid release ID",
            (viewModel.event.value as ReleaseDetailEvent.Error).message
        )
    }
}
