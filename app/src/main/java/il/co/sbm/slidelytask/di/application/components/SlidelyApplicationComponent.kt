package il.co.sbm.slidelytask.di.application.components

import com.squareup.picasso.Picasso
import dagger.Component
import il.co.sbm.slidelytask.di.application.modules.PicassoModule
import il.co.sbm.slidelytask.di.application.modules.PixabayServiceModule
import il.co.sbm.slidelytask.di.application.scopes.SlidelyApplicationScope
import il.co.sbm.slidelytask.model.network.PixabayService

@SlidelyApplicationScope
@Component(modules = [PixabayServiceModule::class, PicassoModule::class])
interface SlidelyApplicationComponent {

    fun getPicasso(): Picasso

    fun getPixabayService(): PixabayService
}