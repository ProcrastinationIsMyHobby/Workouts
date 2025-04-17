package ru.kolsanovafit.workouts.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.kolsanovafit.workouts.data.datasource.ApiService
import ru.kolsanovafit.workouts.data.datasource.Client
import ru.kolsanovafit.workouts.data.datasource.RemoteDataSource
import ru.kolsanovafit.workouts.data.repo.RepositoryImpl
import ru.kolsanovafit.workouts.domain.repo.Repository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideClient(): Client {
        return Client()
    }

    @Singleton
    @Provides
    fun provideApiService(client: Client): ApiService {
        return client.retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideRemoteDataSource(api: ApiService): RemoteDataSource {
        return RemoteDataSource(api)
    }

    @Singleton
    @Provides
    fun provideRepositoryImpl(remoteDataSource: RemoteDataSource): Repository {
        return RepositoryImpl(source = remoteDataSource)
    }
}