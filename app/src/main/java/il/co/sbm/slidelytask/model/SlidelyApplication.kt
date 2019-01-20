package il.co.sbm.slidelytask.model

import android.app.Application
import com.squareup.picasso.Picasso
import il.co.sbm.slidelytask.di.application.components.DaggerSlidelyApplicationComponent
import il.co.sbm.slidelytask.di.application.components.SlidelyApplicationComponent
import il.co.sbm.slidelytask.di.application.modules.ContextModule
import il.co.sbm.slidelytask.model.network.PixabayService
import timber.log.Timber

class SlidelyApplication : Application() {

    init {
        mInstance = this
    }

    companion object {
        private lateinit var mInstance: SlidelyApplication

        fun getInstance(): SlidelyApplication {
            return mInstance
        }
    }

    private lateinit var mSlidelyApplicationComponent: SlidelyApplicationComponent

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        mSlidelyApplicationComponent =
                DaggerSlidelyApplicationComponent
                    .builder()
                    .contextModule(ContextModule(this))
                    .build()
    }

    fun getPicasso() : Picasso {
        return mSlidelyApplicationComponent.getPicasso()
    }

    fun getPixabayService() : PixabayService {
        return mSlidelyApplicationComponent.getPixabayService()
    }
}
