package com.gibran.artistsapp.util

import retrofit2.Response
import retrofit2.HttpException
import kotlin.Result
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.io.IOException

// Custom exceptions for domain-level error handling
class NetworkUnavailableException(message: String = "Sin conexión a Internet") :
    IOException(message)

class EmptyBodyException(message: String = "Respuesta vacía del servidor") : IOException(message)

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> Response<T>
): Result<T> = withContext(dispatcher) {
    runCatching {
        val response = try {
            apiCall()
        } catch (io: IOException) {
            throw NetworkUnavailableException()
        }

        if (response.isSuccessful) {
            response.body() ?: throw EmptyBodyException()
        } else {
            throw HttpException(response)
        }
    }
}
