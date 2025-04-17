package ru.kolsanovafit.workouts.data.repo

import ru.kolsanovafit.workouts.data.datasource.RemoteDataSource
import ru.kolsanovafit.workouts.data.dto.toDomain
import ru.kolsanovafit.workouts.domain.entity.LoadResult
import ru.kolsanovafit.workouts.domain.entity.Workout
import ru.kolsanovafit.workouts.domain.entity.WorkoutVideo
import ru.kolsanovafit.workouts.domain.repo.Repository

class RepositoryImpl(private val source: RemoteDataSource) : Repository {

    override suspend fun getWorkouts(): LoadResult<List<Workout>> {
        return source.getWorkouts().map { dtoList -> dtoList.toDomain() }
    }

    override suspend fun getVideoById(id: Int): LoadResult<WorkoutVideo> {
        return source.getVideo(id).map { it.toDomain() }
    }

    private fun <T, R> LoadResult<T>.map(transform: (T) -> R): LoadResult<R> {
        return when (this) {
            is LoadResult.Success -> LoadResult.Success(transform(data))
            is LoadResult.Error -> LoadResult.Error(error)
            is LoadResult.Empty -> LoadResult.Empty
        }
    }
}