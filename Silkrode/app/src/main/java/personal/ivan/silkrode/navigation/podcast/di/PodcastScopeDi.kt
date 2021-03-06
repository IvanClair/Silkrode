package personal.ivan.silkrode.navigation.podcast.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import personal.ivan.silkrode.io.network.PodcastApiService
import personal.ivan.silkrode.navigation.podcast.repository.PodcastRepository
import personal.ivan.silkrode.io.db.CollectionDao
import personal.ivan.silkrode.io.db.PodcastDao
import personal.ivan.silkrode.di.ViewModelKey
import personal.ivan.silkrode.navigation.podcast.view.PodcastActivity
import personal.ivan.silkrode.navigation.podcast.view.fragment.PlayFragment
import personal.ivan.silkrode.navigation.podcast.view.fragment.collection.CollectionFragment
import personal.ivan.silkrode.navigation.podcast.view.fragment.pod_cast_list.PodcastListFragment
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import personal.ivan.silkrode.util.DateFormatUtil
import retrofit2.Retrofit
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
            PodcastFragmentModule::class,
            PodcastRepositoryModule::class]
    )
    abstract fun contributePodcastActivity(): PodcastActivity
}

/* ------------------------------ Fragment */

@Suppress("unused")
@Module
abstract class PodcastFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributePodcastListFragment(): PodcastListFragment

    @ContributesAndroidInjector
    abstract fun contributeCollectionListFragment(): CollectionFragment

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

/* ------------------------------ Repository */

@Module
object PodcastRepositoryModule {

    @JvmStatic
    @PodcastScope
    @Provides
    fun providePodCastRepository(
        service: PodcastApiService,
        podcastDao: PodcastDao,
        collectionDao: CollectionDao,
        util: DateFormatUtil
    ) =
        PodcastRepository(
            mService = service,
            mPodcastDao = podcastDao,
            mCollectionDao = collectionDao,
            mUtil = util
        )

    @JvmStatic
    @PodcastScope
    @Provides
    fun providePodCastService(retrofit: Retrofit): PodcastApiService =
        retrofit.create(PodcastApiService::class.java)
}