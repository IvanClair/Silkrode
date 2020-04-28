package personal.ivan.silkrode.navigation.podcast.viewmodel

import android.content.Intent
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
import personal.ivan.silkrode.service.PodcastAudioService
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

    /* ------------------------------ Service */

    /**
     * Start the podcast
     */
    fun startPodcast() {
        val url =
            "https://dts.podtrac.com/redirect.mp3/download.ted.com/talks/MattCutts_2019U.mp3?apikey=172BB350-0207&prx_url=https://dovetail.prxu.org/70/1b56e1b3-9eaa-4918-a9a3-f69650636d5c/MattCutts_2019U_VO_Intro.mp3"
        startPodcastService(
            intent =
            getPodcastServiceIntent(actionName = PodcastAudioService.PODCAST_ACTION_START)
                .apply { putExtra(PodcastAudioService.PODCAST_BUNDLE_TAG_URL, url) }
        )
    }

    /**
     * Pause the podcast
     */
    fun pausePodcast() {
        startPodcastService(
            intent =
            getPodcastServiceIntent(actionName = PodcastAudioService.PODCAST_ACTION_PAUSE)
        )
    }

    /**
     * Resume the podcast
     */
    fun resumePodcast() {
        startPodcastService(
            intent =
            getPodcastServiceIntent(actionName = PodcastAudioService.PODCAST_ACTION_RESUME)
        )
    }

    /**
     * Seek progress of the podcast
     */
    fun seekPodcast(second: Int) {
        startPodcastService(
            intent =
            getPodcastServiceIntent(actionName = PodcastAudioService.PODCAST_ACTION_SEEK_TO)
                .apply { putExtra(PodcastAudioService.PODCAST_BUNDLE_TAG_SEEK_TO, second) }
        )
    }

    private fun getPodcastServiceIntent(@PodcastAudioService.Companion.PodcastServiceAction actionName: String): Intent =
        Intent(
            getApplication<SilkrodeApplication>(),
            PodcastAudioService::class.java
        ).apply { action = actionName }

    private fun startPodcastService(intent: Intent) {
        getApplication<SilkrodeApplication>().startService(intent)
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