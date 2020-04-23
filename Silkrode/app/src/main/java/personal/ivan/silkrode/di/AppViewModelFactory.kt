package personal.ivan.silkrode.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

/**
 * ViewModelFactory which uses Dagger to create the instances.
 */
class AppViewModelFactory @Inject constructor(
    private val mCreatorMap: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(viewModelClass: Class<T>): T {
        var provider = mCreatorMap[viewModelClass]

        // check the null view model is a superclass or superinterface
        if (provider == null) {
            for ((key, value) in mCreatorMap) {
                if (viewModelClass.isAssignableFrom(key)) {
                    provider = value
                    break
                }
            }
        }

        // if view model is not in view model map or
        // not a superclass or superinterface of the view model, throw exception
        requireNotNull(provider) { "unknown model class $viewModelClass" }

        try {
            @Suppress("UNCHECKED_CAST")
            return provider.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}