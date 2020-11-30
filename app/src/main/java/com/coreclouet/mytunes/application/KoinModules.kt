package com.coreclouet.mytunes.application

import android.app.Application
import androidx.room.Room
import com.coreclouet.mytunes.dao.ArtistDao
import com.coreclouet.mytunes.dao.CollectionDao
import com.coreclouet.mytunes.dao.TrackDao
import com.coreclouet.mytunes.remote.ItunesApi
import com.coreclouet.mytunes.repository.SongRepository
import com.coreclouet.mytunes.viewmodel.MediaViewModel
import com.coreclouet.mytunes.viewmodel.SearchViewModel
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val viewModelModule = module {
    viewModel {
        SearchViewModel(get())
    }
    viewModel {
        MediaViewModel(get())
    }
}

val repositoryModule = module {
    single {
        SongRepository(get(), get(), get(), get())
    }
}

val databaseModule = module {

    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "mytunes")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideArtistDao(database: AppDatabase): ArtistDao {
        return database.artistDao
    }

    fun provideCollectionDao(database: AppDatabase): CollectionDao {
        return database.collectionDao
    }

    fun provideTrackDao(database: AppDatabase): TrackDao {
        return database.trackDao
    }

    single { provideDatabase(androidApplication()) }
    single { provideArtistDao(get()) }
    single { provideCollectionDao(get()) }
    single { provideTrackDao(get()) }
}

val apiModule = module {
    fun provideUseApi(retrofit: Retrofit): ItunesApi {
        return retrofit.create(ItunesApi::class.java)
    }

    single { provideUseApi(get()) }
}

val retrofitModule = module {

    fun provideGson(): Gson {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create()
    }

    fun provideHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()

        return okHttpClientBuilder.build()
    }

    fun provideRetrofit(factory: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(factory))
            .client(client)
            .build()
    }

    single { provideGson() }
    single { provideHttpClient() }
    single { provideRetrofit(get(), get()) }
}