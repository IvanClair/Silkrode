package personal.ivan.silkrode.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import personal.ivan.silkrode.BuildConfig
import personal.ivan.silkrode.R
import personal.ivan.silkrode.SilkrodeApplication
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlin.reflect.KClass

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