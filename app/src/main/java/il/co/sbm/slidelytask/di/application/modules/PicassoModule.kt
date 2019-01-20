package il.co.sbm.slidelytask.di.application.modules

import android.content.Context
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import il.co.sbm.slidelytask.di.application.qualifiers.ApplicationContext
import il.co.sbm.slidelytask.di.application.scopes.SlidelyApplicationScope
import okhttp3.OkHttpClient

@Module(includes = [NetworkModule::class, ContextModule::class])
class PicassoModule {

    @Provides
    @SlidelyApplicationScope
    fun picasso(@ApplicationContext context: Context, okHttp3Downloader: OkHttp3Downloader): Picasso {
        return Picasso.Builder(context)
            .downloader(okHttp3Downloader)
            .build()
    }

    @Provides
    @SlidelyApplicationScope
    fun okHttp3Downloader(okHttpClient: OkHttpClient) : OkHttp3Downloader{
        return OkHttp3Downloader(okHttpClient)
    }
}