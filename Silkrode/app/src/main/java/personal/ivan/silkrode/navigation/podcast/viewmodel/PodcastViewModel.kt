package personal.ivan.silkrode.navigation.podcast.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import personal.ivan.silkrode.SilkrodeApplication
import personal.ivan.silkrode.api.Podcast
import personal.ivan.silkrode.api.PodcastRepository
import personal.ivan.silkrode.navigation.podcast.model.CollectionVhBindingModel
import personal.ivan.silkrode.service.PodcastAudioService
import javax.inject.Inject

class PodcastViewModel @Inject constructor(
    application: SilkrodeApplication,
    private val mRepository: PodcastRepository
) : AndroidViewModel(application) {

    // API response - podcast list
    val podcastList = mRepository.getPodcastList()

    // API response - Collection Binding Model
    private val mSelectedPodcastId = MutableLiveData<String>()
    val collectionBindingModel = mSelectedPodcastId.switchMap { mRepository.getCollection() }
    val expandCollapsingToolBarLayout = MutableLiveData<Boolean>()

    // Binding Model - Play
    val audioControlsEnabled = MutableLiveData<Boolean>()
    val playOrPause = MutableLiveData<Boolean>()
    val totalDuration = MutableLiveData<Int>()

    /* ------------------------------ API */

    /**
     * Request collection API
     */
    fun requestCollectionApi(id: String) {
        mSelectedPodcastId.value = id
    }

    /* ------------------------------ UI Config - Collection */

    /**
     * Since CollapsingToolBarLayout does not implement save
     * view state internally in fragment, need to control it
     */
    fun setCoverImageExpand(expand: Boolean) {
        expandCollapsingToolBarLayout.value = expand
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
     * Get collection by id
     */
    fun didCollection(id: String): Boolean = id == mSelectedPodcastId.value

    /**
     * Get collection view holder list
     */
    fun getContentFeedList(): List<CollectionVhBindingModel>? =
        collectionBindingModel.value?.data?.vhModelList

    /**
     * Get selected cover image
     */
    fun getSelectedCollectionCoverImageUrl(): String =
        collectionBindingModel.value?.data?.coverImageUrl ?: ""

    /**
     * Get selected content
     */
    fun getContentFeed(index: Int): CollectionVhBindingModel.ContentVhBindingModel? =
        getContentFeedList()?.getOrNull(index) as? CollectionVhBindingModel.ContentVhBindingModel
}