package ru.kolsanovafit.workouts.data.repo

import ru.kolsanovafit.workouts.data.datasource.RemoteDataSource
import ru.kolsanovafit.workouts.data.dto.toDomain
import ru.kolsanovafit.workouts.domain.entity.LoadState
import ru.kolsanovafit.workouts.domain.entity.Workout
import ru.kolsanovafit.workouts.domain.entity.WorkoutVideo
import ru.kolsanovafit.workouts.domain.repo.Repository

class RepositoryImpl(private val source: RemoteDataSource): Repository {

    override suspend fun getWorkouts(): LoadState<List<Workout>> {
        return source.getWorkouts().map { dtoList -> dtoList.toDomain() }
    }

    override suspend fun getVideoById(id: Int): LoadState<WorkoutVideo> {
        return source.getVideo(id).map { it.toDomain() }
    }

    private fun <T, R> LoadState<T>.map(transform: (T) -> R): LoadState<R> {
        return when (this) {
            is LoadState.Success -> LoadState.Success(transform(data))
            is LoadState.Error -> LoadState.Error(message)
            is LoadState.Empty -> LoadState.Empty
        }
    }
}