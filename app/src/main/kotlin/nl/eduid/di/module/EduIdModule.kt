package nl.eduid.di.module

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nl.eduid.di.api.EduIdApi
import nl.eduid.di.assist.AuthenticationAssistant
import nl.eduid.di.repository.EduIdRepository
import nl.eduid.di.repository.StorageRepository
import okhttp3.OkHttpClient
import org.tiqr.data.api.response.ApiResponseAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Module which serves the repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

    @Provides
    @Singleton
    internal fun provideEduApi(retrofit: Retrofit): EduIdApi = retrofit.create(EduIdApi::class.java)


    @Provides
    @Singleton
    internal fun providesEduIdRepository(
        api: EduIdApi,
    ) = EduIdRepository(api)

    @Provides
    @Singleton
    internal fun providesStorageRepository(
        @ApplicationContext context: Context,
    ) = StorageRepository(context)

    @Provides
    @Singleton
    internal fun providesAuthenticationAssist(
    ) = AuthenticationAssistant()

    @Provides
    @Singleton
    internal fun provideApiRetrofit(
        client: Lazy<OkHttpClient>, moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder().callFactory { client.get().newCall(it) }
            .addCallAdapterFactory(ApiResponseAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://login.eduid.nl/").build()
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClientBuilder(): OkHttpClient {
        return OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).build()
    }

}