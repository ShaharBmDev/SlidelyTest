package il.co.sbm.slidelytask.di.application.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import il.co.sbm.slidelytask.di.application.qualifiers.ApplicationContext
import il.co.sbm.slidelytask.di.application.scopes.SlidelyApplicationScope

@Module
class ContextModule(context: Context) {

    private var context: Context = context.applicationContext

    @Provides
    @SlidelyApplicationScope
    @ApplicationContext
    fun context(): Context {
        return context
    }
}