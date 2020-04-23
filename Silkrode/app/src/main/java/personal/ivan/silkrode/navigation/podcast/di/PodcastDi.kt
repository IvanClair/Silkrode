package personal.ivan.silkrode.navigation.podcast.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import personal.ivan.silkrode.di.ViewModelKey
import personal.ivan.silkrode.navigation.podcast.view.PodcastActivity
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import javax.inject.Scope

/* ------------------------------ Scope */

@Scope
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class PodcastScope

/* ------------------------------ Activity */

@Suppress("unused")
@Module
abstract class PodcastActivityModule {

    @PodcastScope
    @ContributesAndroidInjector(modules = [PodcastViewModelModule::class])
    abstract fun contributePodcastActivity(): PodcastActivity
}

/* ------------------------------ ViewModel */

@Suppress("unused")
@Module
abstract class PodcastViewModelModule {

    @PodcastScope
    @Binds
    @IntoMap
    @ViewModelKey(PodcastViewModel::class)
    abstract fun bindPodcastViewModel(viewModel: PodcastViewModel): ViewModel
}