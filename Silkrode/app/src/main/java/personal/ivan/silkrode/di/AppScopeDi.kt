package personal.ivan.silkrode.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import dagger.*
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import personal.ivan.silkrode.BuildConfig
import personal.ivan.silkrode.R
import personal.ivan.silkrode.SilkrodeApplication
import personal.ivan.silkrode.db.AppDb
import personal.ivan.silkrode.navigation.podcast.di.PodcastActivityModule
import personal.ivan.silkrode.util.DateFormatUtil
import personal.ivan.silkrode.util.GlideUtil
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlin.reflect.KClass

/* ------------------------------ Application Scope Component */

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ViewModelModule::class,
        DbModule::class,
        RetrofitModule::class,
        GlideModule::class,
        UtilModule::class,
        PodcastActivityModule::class
    ]
)
interface AppComponent : AndroidInjector<SilkrodeApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: SilkrodeApplication): AppComponent
    }
}

/* ------------------------------ ViewModel */

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory
}

@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

/* ------------------------------ Database */

@Module
object DbModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideAppDb(application: SilkrodeApplication) =
        Room
            .databaseBuilder(
                application,
                AppDb::class.java,
                application.packageName
            )
            .fallbackToDestructiveMigration()
            .build()

    @JvmStatic
    @Singleton
    @Provides
    fun providePodcastDao(db: AppDb) = db.podcastDao()
}

/* ------------------------------ Retrofit */

@Module
object RetrofitModule {

    /**
     * Create Retrofit for API call
     */
    @JvmStatic
    @Singleton
    @Provides
    fun provideRetrofit(
        application: SilkrodeApplication,
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(application.getString(R.string.base_url_podcast))
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()

    /**
     * Setting HTTP configs
     */
    @JvmStatic
    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor) =
        OkHttpClient.Builder()
            .apply {
                // setting timeout
                connectTimeout(5L, TimeUnit.SECONDS)
                readTimeout(5L, TimeUnit.SECONDS)

                // log on debug mode
                if (BuildConfig.DEBUG) {
                    addInterceptor(interceptor)
                }
            }
            .build()

    /**
     * Choose to log HTTP detailed information
     */
    @JvmStatic
    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor()
            .apply { level = HttpLoggingInterceptor.Level.BODY }
}

/* ------------------------------ Glide */

@Module
object GlideModule {

    /**
     * [RequestOptions]
     */
    @JvmStatic
    @Singleton
    @Provides
    fun provideGlideRequestOptions() =
        RequestOptions()
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .fallback(R.drawable.ic_launcher_foreground)


    /**
     * [DrawableTransitionOptions]
     */
    @JvmStatic
    @Singleton
    @Provides
    fun provideGlideTransitionOptions() =
        DrawableTransitionOptions.withCrossFade()

    /**
     * [GlideUtil]
     */
    @JvmStatic
    @Singleton
    @Provides
    fun provideGlideUtil(
        options: RequestOptions,
        transitionOptions: DrawableTransitionOptions
    ) =
        GlideUtil(
            mRequestOptions = options,
            mTransitionOptions = transitionOptions
        )
}

/* ------------------------------ Util */

@Module
object UtilModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideDateFormatUtil() = DateFormatUtil()
}