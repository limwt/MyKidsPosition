package com.wt.kids.mykidsposition.di

import com.wt.kids.mykidsposition.BuildConfig
import com.wt.kids.mykidsposition.constants.ApiConstants
import com.wt.kids.mykidsposition.data.repository.ReverseGeocoderRepository
import com.wt.kids.mykidsposition.data.repository.SearchPlaceRepository
import com.wt.kids.mykidsposition.data.source.ApiService
import com.wt.kids.mykidsposition.data.source.ReverseGeocoderService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    fun provideBaseUrl() = ApiConstants.BASE_URL.value

    @Provides
    fun provideReverseGeocoderUrl() = ApiConstants.REVERSE_GEOCODER_URL.value

    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else {
        OkHttpClient.Builder().build()
    }

    @Singleton
    @Provides
    @Named("Main")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(provideBaseUrl())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    @Named("ReverseGeocoder")
    fun provideRetrofitReverseGeocoder(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(provideReverseGeocoderUrl())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideApiService(@Named("Main") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    fun provideApiServiceReverseGeocoder(@Named("ReverseGeocoder") retrofit: Retrofit): ReverseGeocoderService {
        return retrofit.create(ReverseGeocoderService::class.java)
    }

    @Singleton
    @Provides
    fun provideMainRepository(apiService: ApiService) = SearchPlaceRepository(apiService)

    @Singleton
    @Provides
    fun provideReverseGeocoderRepository(service: ReverseGeocoderService) = ReverseGeocoderRepository(service)
}