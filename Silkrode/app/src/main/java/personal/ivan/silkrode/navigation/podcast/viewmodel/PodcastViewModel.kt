package personal.ivan.silkrode.navigation.podcast.viewmodel

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import personal.ivan.silkrode.SilkrodeApplication
import personal.ivan.silkrode.api.Collection
import personal.ivan.silkrode.api.Podcast
import personal.ivan.silkrode.api.PodcastRepository
import personal.ivan.silkrode.navigation.podcast.model.CollectionBindingModel
import personal.ivan.silkrode.navigation.podcast.model.CollectionVhBindingModel
import personal.ivan.silkrode.util.DateFormatUtil
import javax.inject.Inject

class PodcastViewModel @Inject constructor(
    application: SilkrodeApplication,
    private val mRepository: PodcastRepository,
    private val mDateFormatUtil: DateFormatUtil
) : AndroidViewModel(application) {

    // API status
    val apiLoading = MutableLiveData<Boolean>()
    val apiFail = MutableLiveData<Boolean>()

    // API response - podcast list
    val podcastList: LiveData<List<Podcast>> =
        liveData {
            apiLoading.value = true
            val result = mRepository.getPodcastList()?.also { emit(it) }
            if (result == null) apiFail.value = true
            apiLoading.value = false
        }

    // Collection binding model
    val collectionBindingModel = MutableLiveData<CollectionBindingModel>()

    /* ------------------------------ API */

    /**
     * Request collection API
     */
    // todo boilerplate code can be improved, also maybe this API should use [Podcast.id] to request?
    fun requestCollectionApi(id: String) {
        Log.d(PodcastViewModel::class.simpleName, id)
        viewModelScope.launch {
            apiLoading.value = true
            val result = mRepository.getCastDetail()
            if (result == null) apiFail.value = true
            else createCollectionBindingModel(data = result)
            apiLoading.value = false
        }
    }

    /**
     * Due to format date string cost too much performance,
     * so it is better to format it on background
     */
    private suspend fun createCollectionBindingModel(data: Collection) {
        withContext(Dispatchers.IO) {
            val bindingModel =
                CollectionBindingModel(
                    data = data,
                    util = mDateFormatUtil
                )
            collectionBindingModel.postValue(bindingModel)
        }
    }

    /* ------------------------------ Getter */

    /**
     * Get podcast list
     */
    fun getPodcastList(): List<Podcast>? = podcastList.value

    /**
     * Get collection view holder list
     */
    fun getCollectionList(): List<CollectionVhBindingModel>? =
        collectionBindingModel.value?.vhModelList

    /**
     * Get selected cover image
     */
    fun getSelectedCoverImageUrl(): String =
        collectionBindingModel.value?.coverImageUrl ?: ""

    /**
     * Get selected content
     */
    fun getSelectedContent(index: Int): CollectionVhBindingModel.ContentVhBindingModel? =
        getCollectionList()?.getOrNull(index) as? CollectionVhBindingModel.ContentVhBindingModel
}