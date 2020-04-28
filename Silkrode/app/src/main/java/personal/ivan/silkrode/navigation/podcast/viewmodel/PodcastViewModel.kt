package personal.ivan.silkrode.navigation.podcast.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
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

    // Binding Model - Play
    val audioControlsEnabled = MutableLiveData<Boolean>().apply { value = true }
    val playOrPause = MutableLiveData<Boolean>()

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

    // Podcast Service
    private var mPodcastService: PodcastAudioService? = null

    /**
     * Start podcast service
     */
    fun startPodcastService() {
        val context = getApplication<SilkrodeApplication>()
        val intent = Intent(context, PodcastAudioService::class.java)
        context.bindService(
            intent,
            object : ServiceConnection {
                override fun onServiceDisconnected(p0: ComponentName?) {
                    mPodcastService = null
                }

                override fun onServiceConnected(
                    p0: ComponentName?,
                    p1: IBinder?
                ) {
                    mPodcastService =
                        (p1 as? PodcastAudioService.PodcastServiceBinder)?.getPodCastService()
                }
            },
            Context.BIND_AUTO_CREATE
        )
    }

    /**
     * Start the podcast
     *
     * @param forceUpdate indicate that need to restart a new play or not
     */
    fun playPodcast(
        url: String,
        forceUpdate: Boolean
    ) {
        when {
            // force to restart a new play
            forceUpdate -> {
                audioControlsEnabled.value = false
                val aurl =
                    "https://dts.podtrac.com/redirect.mp3/download.ted.com/talks/MattCutts_2019U.mp3?apikey=172BB350-0207&prx_url=https://dovetail.prxu.org/70/1b56e1b3-9eaa-4918-a9a3-f69650636d5c/MattCutts_2019U_VO_Intro.mp3"
                mPodcastService?.startPlayer(
                    url = aurl,
                    prepareCompleteCallback = {
                        audioControlsEnabled.value = true
                        playOrPause.value = true
                    })
            }

            // is playing, pause the podcast
            mPodcastService?.isPlaying() == true -> pausePodcast()

            // is not playing but prepared, it means is paused, try to resume the podcast
            mPodcastService?.isPlaying() == false
                    && mPodcastService?.isPlayerPrepared() == true -> resumePodcast()
        }
    }

    /**
     * Pause the podcast
     */
    private fun pausePodcast() {
        mPodcastService?.pausePlayer()
        playOrPause.value = false
    }

    /**
     * Resume the podcast
     */
    private fun resumePodcast() {
        mPodcastService?.resumePlayer()
        playOrPause.value = true
    }

    /**
     * Seek progress of the podcast
     */
    fun seekPodcast(seconds: Int) {
        mPodcastService?.seekTo(seconds = seconds)
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
    fun getContent(index: Int): CollectionVhBindingModel.ContentVhBindingModel? =
        getCollectionList()?.getOrNull(index) as? CollectionVhBindingModel.ContentVhBindingModel
}