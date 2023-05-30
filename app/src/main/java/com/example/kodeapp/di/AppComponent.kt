package com.example.kodeapp.di

import com.example.kodeapp.data.ApiInterceptor
import com.example.kodeapp.data.ApiService
import com.example.kodeapp.data.Repository
import com.example.kodeapp.data.RepositoryImpl
import com.example.kodeapp.ui.*
import com.example.kodeapp.utils.Constants.BASE_URL
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Qualifier

@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: UserListFragment)
    fun inject(fragment: HostFragment)
    fun inject(fragment: ErrorFragment)
    fun inject(fragment: UserDetailFragment)
}

@Module(includes = [NetworkModule::class, AppBindModule::class])
class AppModule

@Module
class NetworkModule {

    @Provides
    fun provideProductionLessonsService(): ApiService {
        val client = OkHttpClient.Builder().apply {
            addInterceptor(ApiInterceptor())
        }.build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Stage
    fun provideStageLessonsService(): ApiService {
        val client = OkHttpClient.Builder().apply {
            addInterceptor(ApiInterceptor())
        }.build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(ApiService::class.java)
    }
}

@Module
interface AppBindModule {

    @Suppress("FunctionName")
    @Binds
    fun bindLessonsRepositoryImpl_to_NewsRepository(
        newsRepositoryImpl: RepositoryImpl
    ): Repository
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Prod


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Stage