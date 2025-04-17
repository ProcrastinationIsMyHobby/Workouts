package ru.kolsanovafit.workouts.data.datasource

import kotlinx.coroutines.CancellationException
import retrofit2.Response
import ru.kolsanovafit.workouts.domain.entity.LoadState
import java.io.IOException

class RemoteDataSource(private val apiService: ApiService) {

    suspend fun getWorkouts() = safeApiCall {
        apiService.getWorkouts()
    }

    suspend fun getVideo(id: Int) = safeApiCall {
        apiService.getVideo(id)
    }

    private suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): LoadState<T> {
        return runCatching { call() }.fold(onSuccess = { response ->
            if (response.isSuccessful) {
                response.body()?.let {
                    LoadState.Success(it)
                } ?: LoadState.Empty
            } else {
                LoadState.Error("Ошибка сервера: ${response.code()} ${response.message()}")
            }
        }, onFailure = { throwable ->
            when (throwable) {
                is CancellationException -> throw throwable
                is IOException -> LoadState.Error("Ошибка сети, проверьте подключение")
                else -> LoadState.Error(throwable.message ?: "Неизвестная ошибка")
            }
        })
    }
}