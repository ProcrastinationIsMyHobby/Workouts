package ru.kolsanovafit.workouts.data.datasource

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.kolsanovafit.workouts.data.dto.VideoWorkoutDTO
import ru.kolsanovafit.workouts.data.dto.WorkoutDTO

interface ApiService {

    // GET /get_workouts - возвращает список тренировок
    @GET("get_workouts")
    suspend fun getWorkouts(): Response<List<WorkoutDTO>>

    // GET /get_video?id={id} - возвращает видео тренировки по id
    @GET("get_video")
    suspend fun getVideo(@Query("id") id: Int): Response<VideoWorkoutDTO>
}