package personal.ivan.silkrode.navigation.podcast.viewmodel

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import personal.ivan.silkrode.SilkrodeApplication
import personal.ivan.silkrode.api.Collection
import personal.ivan.silkrode.api.Podcast
import personal.ivan.silkrode.api.PodcastRepository
import javax.inject.Inject

class PodcastViewModel @Inject constructor(
    application: SilkrodeApplication,
    private val mRepository: PodcastRepository
) : AndroidViewModel(application) {

    // API status
    val apiLoading: MutableLiveData<Boolean> = MutableLiveData()
    val apiFail: MutableLiveData<Boolean> = MutableLiveData()

    // API response - podcast list
    val podcastList: LiveData<List<Podcast>> =
        liveData {
            apiLoading.value = true
            val result = mRepository.getPodcastList()?.also { emit(it) }
            if (result == null) apiFail.value = true
            apiLoading.value = false
        }
    val collection: MutableLiveData<Collection> = MutableLiveData()

    /* ------------------------------ API */

    /**
     * Request collection API
     */
    // todo boilerplate code can be improved, also maybe this API should use [Podcast.id] to request?
    fun requestCollectionApi(id: String) {
        Log.d(PodcastViewModel::class.simpleName, id)
        viewModelScope.launch {
            apiLoading.value = true
            val result = mRepository.getCastDetail()?.also { collection.value = it }
            if (result == null) apiFail.value = true
            apiLoading.value = false
        }
    }

    /* ------------------------------ Getter */

    /**
     * Get podcast list
     */
    fun getPodcastList(): List<Podcast>? = podcastList.value
}