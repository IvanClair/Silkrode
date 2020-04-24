package personal.ivan.silkrode.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import personal.ivan.silkrode.SilkrodeApplication
import personal.ivan.silkrode.navigation.podcast.di.PodcastActivityModule
import javax.inject.Singleton

/**
 * Application scope component
 */
@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ViewModelModule::class,
        RetrofitModule::class,
        PodcastActivityModule::class
    ]
)
interface AppComponent : AndroidInjector<SilkrodeApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: SilkrodeApplication): AppComponent
    }
}