package ru.kolsanovafit.workouts.data.datasource

import kotlinx.coroutines.CancellationException
import retrofit2.Response
import ru.kolsanovafit.workouts.domain.entity.LoadResult
import ru.kolsanovafit.workouts.domain.entity.NetworkError
import java.io.IOException

class RemoteDataSource(private val apiService: ApiService) {

    suspend fun getWorkouts() = safeApiCall {
        apiService.getWorkouts()
    }

    suspend fun getVideo(id: Int) = safeApiCall {
        apiService.getVideo(id)
    }

    private suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): LoadResult<T> {
        return runCatching { call() }.fold(
            onSuccess = { response ->
                if (response.isSuccessful) {
                    response.body()?.let {
                        LoadResult.Success(it)
                    } ?: LoadResult.Empty
                } else {
                    LoadResult.Error(NetworkError.ServerError(response.code(), response.message()))
                }
            },
            onFailure = { throwable ->
                when (throwable) {
                    is CancellationException -> throw throwable
                    is IOException -> LoadResult.Error(NetworkError.ConnectionError)
                    else -> LoadResult.Error(NetworkError.UnknownError(throwable))
                }
            }
        )
    }
}