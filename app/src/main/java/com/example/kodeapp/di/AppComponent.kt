package com.example.kodeapp.di

import com.example.kodeapp.data.ApiInterceptor
import com.example.kodeapp.data.ApiService
import com.example.kodeapp.data.Repository
import com.example.kodeapp.data.RepositoryImpl
import com.example.kodeapp.ui.*
import com.example.kodeapp.utils.Constants.BASE_URL
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton
@Singleton
@Component(modules = [NetworkModule::class, NavigationModule::class, AppBindModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: UserListFragment)
    fun inject(fragment: HostFragment)
    fun inject(fragment: ErrorFragment)
    fun inject(fragment: UserDetailFragment)
    fun inject(fragment: UserListByGroupFragment)
}

@Module
@InstallIn(SingletonComponent::class)
class NavigationModule {
    private val cicerone: Cicerone<Router> = Cicerone.create()

    @Provides
    fun provideRouter(): Router {
        return cicerone.router
    }

    @Provides
    fun provideNavigatorHolder(): NavigatorHolder {
        return cicerone.getNavigatorHolder()
    }
}

@Module
@InstallIn(SingletonComponent::class)
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
@InstallIn(SingletonComponent::class)
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