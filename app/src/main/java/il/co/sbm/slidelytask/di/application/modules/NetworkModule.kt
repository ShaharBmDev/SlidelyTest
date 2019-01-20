package il.co.sbm.slidelytask.di.application.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import il.co.sbm.slidelytask.di.application.qualifiers.ApplicationContext
import il.co.sbm.slidelytask.di.application.scopes.SlidelyApplicationScope
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

@Module(includes = [ContextModule::class])
class NetworkModule {

    companion object {
        private const val MAX_CACHE_SIZE: Long = 10 * 1024 * 1024
        private const val CACHE_FILE_NAME = "okhttp_cache"
        private const val TIMEOUT_SECONDS: Long = 5
    }

    @Provides
    @SlidelyApplicationScope
    fun loggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> Timber.i(message) })
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        return interceptor
    }

    @Provides
    @SlidelyApplicationScope
    fun cache(cacheFile: File): Cache {
        return Cache(cacheFile, MAX_CACHE_SIZE)
    }

    @Provides
    @SlidelyApplicationScope
    fun cacheFile(@ApplicationContext context: Context): File {
        return File(context.cacheDir,
            CACHE_FILE_NAME
        )
    }

    @Provides
    @SlidelyApplicationScope
    fun okHttpClient(loggingInterceptor: HttpLoggingInterceptor, cache: Cache): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .cache(cache)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }
}