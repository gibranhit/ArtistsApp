package com.gibran.artistsapp.domain.usecase

import com.gibran.artistsapp.domain.model.ArtistDetail
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

class GetArtistDetailUseCaseTest {

    private lateinit var getArtistDetailUseCase: GetArtistDetailUseCase
    private lateinit var mockRepository: ArtistRepository

    private val sampleArtistDetail = ArtistDetail(
        id = 1,
        name = "The Beatles",
        profile = "The Beatles were an English rock band formed in Liverpool in 1960.",
        images = listOf(),
        members = listOf()
    )

    @Before
    fun setUp() {
        mockRepository = mockk()
        getArtistDetailUseCase = GetArtistDetailUseCase(mockRepository)
    }

    @Test
    fun `invoke should call repository getArtistDetail with correct id`() = runTest {
        // Arrange
        val artistId = 1L
        val expectedResult = Result.success(sampleArtistDetail)

        coEvery { mockRepository.getArtistDetail(artistId) } returns expectedResult

        // Act
        val result = getArtistDetailUseCase.invoke(artistId)

        // Assert
        coVerify { mockRepository.getArtistDetail(artistId) }
        assertEquals(expectedResult.getOrNull(), result.getOrNull())
    }

    @Test
    fun `invoke should return success result from repository`() = runTest {
        // Arrange
        val artistId = 123L
        val expectedResult = Result.success(sampleArtistDetail)

        coEvery { mockRepository.getArtistDetail(artistId) } returns expectedResult

        // Act
        val result = getArtistDetailUseCase.invoke(artistId)

        // Assert
        assertTrue("Result should be success", result.isSuccess)
        assertEquals(sampleArtistDetail, result.getOrNull())
        assertEquals(expectedResult.getOrNull(), result.getOrNull())
    }

    @Test(expected = Exception::class)
    fun `invoke should return failure result from repository`() = runTest {
        // Arrange
        val artistId = 999L
        val exception = Exception("Artist not found")
        val expectedResult = Result.failure<ArtistDetail>(exception)

        coEvery { mockRepository.getArtistDetail(artistId) } throws exception

        // Act
        val result = getArtistDetailUseCase.invoke(artistId)

        // Assert
        coVerify { mockRepository.getArtistDetail(artistId) }
        assertTrue("Result should be failure", result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        assertEquals(expectedResult.exceptionOrNull(), result.exceptionOrNull())
    }

    @Test
    fun `invoke should handle different artist ids`() = runTest {
        // Arrange
        val firstId = 1L
        val secondId = 2L
        val firstArtist = sampleArtistDetail.copy(id = firstId, name = "The Beatles")
        val secondArtist = sampleArtistDetail.copy(id = secondId, name = "Queen")
        val firstResult = Result.success(firstArtist)
        val secondResult = Result.success(secondArtist)

        coEvery { mockRepository.getArtistDetail(firstId) } returns firstResult
        coEvery { mockRepository.getArtistDetail(secondId) } returns secondResult

        // Act
        val result1 = getArtistDetailUseCase.invoke(firstId)
        val result2 = getArtistDetailUseCase.invoke(secondId)

        // Assert
        coVerify { mockRepository.getArtistDetail(firstId) }
        coVerify { mockRepository.getArtistDetail(secondId) }
        assertEquals(firstArtist, result1.getOrNull())
        assertEquals(secondArtist, result2.getOrNull())
    }

    @Test
    fun `invoke should handle negative artist id`() = runTest {
        // Arrange
        val artistId = -1L
        val exception = IllegalArgumentException("Invalid artist ID")
        val expectedResult = Result.failure<ArtistDetail>(exception)

        coEvery { mockRepository.getArtistDetail(artistId) } returns expectedResult

        // Act
        val result = getArtistDetailUseCase.invoke(artistId)

        // Assert
        coVerify { mockRepository.getArtistDetail(artistId) }
        assertTrue("Result should be failure", result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should handle zero artist id`() = runTest {
        // Arrange
        val artistId = 0L
        val exception = IllegalArgumentException("Invalid artist ID")
        val expectedResult = Result.failure<ArtistDetail>(exception)

        coEvery { mockRepository.getArtistDetail(artistId) } returns expectedResult

        // Act
        val result = getArtistDetailUseCase.invoke(artistId)

        // Assert
        coVerify { mockRepository.getArtistDetail(artistId) }
        assertTrue("Result should be failure", result.isFailure)
    }

    @Test
    fun `invoke should handle large artist id`() = runTest {
        // Arrange
        val artistId = Long.MAX_VALUE
        val expectedResult = Result.success(sampleArtistDetail.copy(id = artistId))

        coEvery { mockRepository.getArtistDetail(artistId) } returns expectedResult

        // Act
        val result = getArtistDetailUseCase.invoke(artistId)

        // Assert
        coVerify { mockRepository.getArtistDetail(artistId) }
        assertTrue("Result should be success", result.isSuccess)
        assertEquals(artistId, result.getOrNull()?.id)
    }

    @Test
    fun `invoke should pass through repository result unchanged`() = runTest {
        // Arrange
        val artistId = 42L
        val repositoryResult = Result.success(sampleArtistDetail)

        coEvery { mockRepository.getArtistDetail(artistId) } returns repositoryResult

        // Act
        val useCaseResult = getArtistDetailUseCase.invoke(artistId)

        // Assert
        coVerify { mockRepository.getArtistDetail(artistId) }
        assertEquals(repositoryResult.getOrNull(), useCaseResult.getOrNull())
    }

    @Test
    fun `invoke should verify exact parameter matching`() = runTest {
        // Arrange
        val artistId = 555L
        val expectedResult = Result.success(sampleArtistDetail)

        coEvery { mockRepository.getArtistDetail(artistId) } returns expectedResult

        // Act
        getArtistDetailUseCase.invoke(artistId)

        // Assert
        coVerify(exactly = 1) { mockRepository.getArtistDetail(eq(artistId)) }
        coVerify(exactly = 1) { mockRepository.getArtistDetail(any()) }
    }

    @Test
    fun `invoke operator should work correctly`() = runTest {
        // Arrange
        val artistId = 777L
        val expectedResult = Result.success(sampleArtistDetail)

        coEvery { mockRepository.getArtistDetail(artistId) } returns expectedResult

        // Act - Using operator invoke syntax
        val result = getArtistDetailUseCase(artistId)

        // Assert
        coVerify { mockRepository.getArtistDetail(artistId) }
        assertEquals(expectedResult.getOrNull(), result.getOrNull())
    }

    @Test
    fun `invoke should handle multiple calls with same id`() = runTest {
        // Arrange
        val artistId = 888L
        val expectedResult = Result.success(sampleArtistDetail)

        coEvery { mockRepository.getArtistDetail(artistId) } returns expectedResult

        // Act
        val result1 = getArtistDetailUseCase.invoke(artistId)
        val result2 = getArtistDetailUseCase.invoke(artistId)

        // Assert
        coVerify(exactly = 2) { mockRepository.getArtistDetail(artistId) }
        assertEquals(expectedResult.getOrNull(), result1.getOrNull())
        assertEquals(expectedResult.getOrNull(), result2.getOrNull())
    }

    @Test
    fun `invoke should handle network exception`() = runTest {
        // Arrange
        val artistId = 999L
        val exception = RuntimeException("Network error")
        val expectedResult = Result.failure<ArtistDetail>(exception)

        coEvery { mockRepository.getArtistDetail(artistId) } returns expectedResult

        // Act
        val result = getArtistDetailUseCase.invoke(artistId)

        // Assert
        coVerify { mockRepository.getArtistDetail(artistId) }
        assertTrue("Result should be failure", result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}