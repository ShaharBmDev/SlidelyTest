package il.co.sbm.slidelytask.di.application.modules

import dagger.Module
import dagger.Provides
import il.co.sbm.slidelytask.di.application.scopes.SlidelyApplicationScope
import il.co.sbm.slidelytask.model.network.PixabayService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

@Module(includes = [NetworkModule::class])
class PixabayServiceModule {

    /**
     * Generates a retrofit PixabayService to invoke api calls to PixabayService.
     */
    @Provides
    @SlidelyApplicationScope
    fun pixabayService(retrofit: Retrofit): PixabayService {
        return retrofit.create(PixabayService::class.java)
    }

    /**
     * Generates a retrofit client with a okhttp client and a jackson converter
     */
    @Provides
    @SlidelyApplicationScope
    fun retrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit
            .Builder()
            .client(okHttpClient)
            .baseUrl(PixabayService.BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}