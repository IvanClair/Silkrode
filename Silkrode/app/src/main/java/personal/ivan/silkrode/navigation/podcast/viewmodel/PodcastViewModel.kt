package personal.ivan.silkrode.navigation.podcast.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import personal.ivan.silkrode.SilkrodeApplication
import personal.ivan.silkrode.api.ApiStatus
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
    val podcastList: LiveData<ApiStatus<List<Podcast>>> = mRepository.fetchPodcastList()

    // Collection Binding Model
    private lateinit var mCollectionApiJob: Job
    val collectionBindingModel = MutableLiveData<CollectionBindingModel>()

    // Binding Model - Play
    val audioControlsEnabled = MutableLiveData<Boolean>()
    val playOrPause = MutableLiveData<Boolean>()
    val totalDuration = MutableLiveData<Int>()

    /* ------------------------------ API */

    /**
     * Request collection API
     */
    // todo boilerplate code can be improved, also maybe this API should use [Podcast.id] to request?
    fun requestCollectionApi(id: String) {
        Log.d(PodcastViewModel::class.simpleName, id)
        mCollectionApiJob =
            viewModelScope.launch {
                apiLoading.value = true
                val result = mRepository.getCastDetail()
                if (result != null) createCollectionBindingModel(data = result)
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
                mPodcastService?.startPlayer(
                    url = url,
                    prepareCompleteCallback = {
                        audioControlsEnabled.value = true
                        playOrPause.value = true
                        totalDuration.value = it
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
     *
     * @param direct indicate the change is direct to the target or not
     */
    fun seekPodcast(
        seconds: Int,
        direct: Boolean
    ) {
        mPodcastService?.seekTo(seconds = seconds, direct = direct)
    }

    /**
     * Get current duration of the podcast
     */
    fun getCurrentPodcastDuration() = mPodcastService?.getCurrentDuration() ?: 0

    /**
     * Get current playing url
     */
    fun getCurrentPlayingUrl() = mPodcastService?.getPlayingUrl() ?: ""

    /* ------------------------------ Getter */

    /**
     * Get podcast list
     */
    fun getPodcastList(): List<Podcast>? = podcastList.value?.data

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
     * Clear collection binding model
     *
     * make sure every time user can get the correct data
     */
    fun clearCollectionBindingModel() {
        mCollectionApiJob.cancel()
        collectionBindingModel.value = null
    }

    /**
     * Get selected content
     */
    fun getContent(index: Int): CollectionVhBindingModel.ContentVhBindingModel? =
        getCollectionList()?.getOrNull(index) as? CollectionVhBindingModel.ContentVhBindingModel
}