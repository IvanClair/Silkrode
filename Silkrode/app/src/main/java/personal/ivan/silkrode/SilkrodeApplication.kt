package personal.ivan.silkrode

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import personal.ivan.silkrode.di.DaggerAppComponent

class SilkrodeApplication : DaggerApplication() {

    /* ------------------------------ Dagger */

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent.factory().create(this)
}