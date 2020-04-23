package personal.ivan.silkrode.navigation.podcast.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import personal.ivan.silkrode.di.ViewModelKey
import personal.ivan.silkrode.navigation.podcast.view.PodcastActivity
import personal.ivan.silkrode.navigation.podcast.view.fragment.ChannelListFragment
import personal.ivan.silkrode.navigation.podcast.view.fragment.PlayFragment
import personal.ivan.silkrode.navigation.podcast.view.fragment.ProgramListFragment
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
    @ContributesAndroidInjector(
        modules = [
            PodcastViewModelModule::class,
            PodcastFragmentModule::class]
    )
    abstract fun contributePodcastActivity(): PodcastActivity
}

/* ------------------------------ Fragment */

@Suppress("unused")
@Module
abstract class PodcastFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeChannelListFragment(): ChannelListFragment

    @ContributesAndroidInjector
    abstract fun contributeProgramListFragment(): ProgramListFragment

    @ContributesAndroidInjector
    abstract fun contributePlayFragment(): PlayFragment
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