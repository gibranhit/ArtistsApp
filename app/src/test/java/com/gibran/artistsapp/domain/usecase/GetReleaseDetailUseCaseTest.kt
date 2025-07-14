package com.gibran.artistsapp.domain.usecase

import com.gibran.artistsapp.domain.model.ReleaseDetail
import com.gibran.artistsapp.domain.repository.ArtistRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetReleaseDetailUseCaseTest {

    private lateinit var getReleaseDetailUseCase: GetReleaseDetailUseCase
    private lateinit var mockRepository: ArtistRepository

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
        mockRepository = mockk()
        getReleaseDetailUseCase = GetReleaseDetailUseCase(mockRepository)
    }

    @Test
    fun `invoke should call repository getReleaseDetail with correct id`() = runTest {
        // Arrange
        val releaseId = 1L
        val expectedResult = Result.success(sampleReleaseDetail)

        coEvery { mockRepository.getReleaseDetail(releaseId) } returns expectedResult

        // Act
        val result = getReleaseDetailUseCase.invoke(releaseId)

        // Assert
        coVerify { mockRepository.getReleaseDetail(releaseId) }
        assertEquals(expectedResult.getOrNull(), result.getOrNull())
    }

    @Test
    fun `invoke should return success result from repository`() = runTest {
        // Arrange
        val releaseId = 123L
        val expectedResult = Result.success(sampleReleaseDetail)

        coEvery { mockRepository.getReleaseDetail(releaseId) } returns expectedResult

        // Act
        val result = getReleaseDetailUseCase.invoke(releaseId)

        // Assert
        assertTrue("Result should be success", result.isSuccess)
        assertEquals(sampleReleaseDetail, result.getOrNull())
        assertEquals(expectedResult.getOrNull(), result.getOrNull())
    }

    @Test(expected = Exception::class)
    fun `invoke should return failure result from repository`() = runTest {
        // Arrange
        val releaseId = 999L
        val exception = Exception("Release not found")
        val expectedResult = Result.failure<ReleaseDetail>(exception)

        coEvery { mockRepository.getReleaseDetail(releaseId) } throws  exception

        // Act
        val result = getReleaseDetailUseCase.invoke(releaseId)

        // Assert
        coVerify { mockRepository.getReleaseDetail(releaseId) }
        assertTrue("Result should be failure", result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        assertEquals(expectedResult.getOrNull(), result.getOrNull())
    }

    @Test
    fun `invoke should handle different release ids`() = runTest {
        // Arrange
        val firstId = 1L
        val secondId = 2L
        val firstRelease = sampleReleaseDetail.copy(id = firstId, title = "Abbey Road")
        val secondRelease = sampleReleaseDetail.copy(id = secondId, title = "Dark Side of the Moon")
        val firstResult = Result.success(firstRelease)
        val secondResult = Result.success(secondRelease)

        coEvery { mockRepository.getReleaseDetail(firstId) } returns firstResult
        coEvery { mockRepository.getReleaseDetail(secondId) } returns secondResult

        // Act
        val result1 = getReleaseDetailUseCase.invoke(firstId)
        val result2 = getReleaseDetailUseCase.invoke(secondId)

        // Assert
        coVerify { mockRepository.getReleaseDetail(firstId) }
        coVerify { mockRepository.getReleaseDetail(secondId) }
        assertEquals(firstRelease, result1.getOrNull())
        assertEquals(secondRelease, result2.getOrNull())
    }

    @Test
    fun `invoke should handle negative release id`() = runTest {
        // Arrange
        val releaseId = -1L
        val exception = IllegalArgumentException("Invalid release ID")
        val expectedResult = Result.failure<ReleaseDetail>(exception)

        coEvery { mockRepository.getReleaseDetail(releaseId) } returns expectedResult

        // Act
        val result = getReleaseDetailUseCase.invoke(releaseId)

        // Assert
        coVerify { mockRepository.getReleaseDetail(releaseId) }
        assertTrue("Result should be failure", result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should handle zero release id`() = runTest {
        // Arrange
        val releaseId = 0L
        val exception = IllegalArgumentException("Invalid release ID")
        val expectedResult = Result.failure<ReleaseDetail>(exception)

        coEvery { mockRepository.getReleaseDetail(releaseId) } returns expectedResult

        // Act
        val result = getReleaseDetailUseCase.invoke(releaseId)

        // Assert
        coVerify { mockRepository.getReleaseDetail(releaseId) }
        assertTrue("Result should be failure", result.isFailure)
    }

    @Test
    fun `invoke should handle large release id`() = runTest {
        // Arrange
        val releaseId = Long.MAX_VALUE
        val expectedResult = Result.success(sampleReleaseDetail.copy(id = releaseId))

        coEvery { mockRepository.getReleaseDetail(releaseId) } returns expectedResult

        // Act
        val result = getReleaseDetailUseCase.invoke(releaseId)

        // Assert
        coVerify { mockRepository.getReleaseDetail(releaseId) }
        assertTrue("Result should be success", result.isSuccess)
        assertEquals(releaseId, result.getOrNull()?.id)
    }

    @Test
    fun `invoke should pass through repository result unchanged`() = runTest {
        // Arrange
        val releaseId = 42L
        val repositoryResult = Result.success(sampleReleaseDetail)

        coEvery { mockRepository.getReleaseDetail(releaseId) } returns repositoryResult

        // Act
        val useCaseResult = getReleaseDetailUseCase.invoke(releaseId)

        // Assert
        coVerify { mockRepository.getReleaseDetail(releaseId) }
        assertEquals(repositoryResult.getOrNull(), useCaseResult.getOrNull())
    }

    @Test
    fun `invoke should verify exact parameter matching`() = runTest {
        // Arrange
        val releaseId = 555L
        val expectedResult = Result.success(sampleReleaseDetail)

        coEvery { mockRepository.getReleaseDetail(releaseId) } returns expectedResult

        // Act
        getReleaseDetailUseCase.invoke(releaseId)

        // Assert
        coVerify(exactly = 1) { mockRepository.getReleaseDetail(eq(releaseId)) }
        coVerify(exactly = 1) { mockRepository.getReleaseDetail(any()) }
    }

    @Test
    fun `invoke operator should work correctly`() = runTest {
        // Arrange
        val releaseId = 777L
        val expectedResult = Result.success(sampleReleaseDetail)

        coEvery { mockRepository.getReleaseDetail(releaseId) } returns expectedResult

        // Act - Using operator invoke syntax
        val result = getReleaseDetailUseCase(releaseId)

        // Assert
        coVerify { mockRepository.getReleaseDetail(releaseId) }
        assertEquals(expectedResult.getOrNull(), result.getOrNull())
    }

    @Test
    fun `invoke should handle multiple calls with same id`() = runTest {
        // Arrange
        val releaseId = 888L
        val expectedResult = Result.success(sampleReleaseDetail)

        coEvery { mockRepository.getReleaseDetail(releaseId) } returns expectedResult

        // Act
        val result1 = getReleaseDetailUseCase.invoke(releaseId)
        val result2 = getReleaseDetailUseCase.invoke(releaseId)

        // Assert
        coVerify(exactly = 2) { mockRepository.getReleaseDetail(releaseId) }
        assertEquals(expectedResult.getOrNull(), result1.getOrNull())
        assertEquals(expectedResult.getOrNull(), result2.getOrNull())
    }

    @Test
    fun `invoke should handle network exception`() = runTest {
        // Arrange
        val releaseId = 999L
        val exception = RuntimeException("Network error")
        val expectedResult = Result.failure<ReleaseDetail>(exception)

        coEvery { mockRepository.getReleaseDetail(releaseId) } returns expectedResult

        // Act
        val result = getReleaseDetailUseCase.invoke(releaseId)

        // Assert
        coVerify { mockRepository.getReleaseDetail(releaseId) }
        assertTrue("Result should be failure", result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should handle timeout exception`() = runTest {
        // Arrange
        val releaseId = 404L
        val exception = java.util.concurrent.TimeoutException("Request timeout")
        val expectedResult = Result.failure<ReleaseDetail>(exception)

        coEvery { mockRepository.getReleaseDetail(releaseId) } returns expectedResult

        // Act
        val result = getReleaseDetailUseCase.invoke(releaseId)

        // Assert
        coVerify { mockRepository.getReleaseDetail(releaseId) }
        assertTrue("Result should be failure", result.isFailure)
        assertEquals("Request timeout", result.exceptionOrNull()?.message)
    }
}
