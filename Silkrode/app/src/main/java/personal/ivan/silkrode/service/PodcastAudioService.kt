package personal.ivan.silkrode.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaPlayer.SEEK_CLOSEST
import android.os.Binder
import android.os.Build
import android.os.IBinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PodcastAudioService : Service() {

    // Media Player
    private lateinit var mPlayer: MediaPlayer

    // Local Binder
    private val mBinder = PodcastServiceBinder()

    // Flag
    private var mPrepared: Boolean = false

    // Url
    private var mPlayingUrl: String = ""

    /* ------------------------------ Binder */

    inner class PodcastServiceBinder : Binder() {
        fun getPodCastService() = this@PodcastAudioService
    }

    /* ------------------------------ Override */

    override fun onBind(p0: Intent?): IBinder? = mBinder

    override fun onCreate() {
        super.onCreate()
        mPlayer =
            MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes
                        .Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayer()
        mPlayer.release()
    }

    /* ------------------------------ Public Function */

    /**
     * Start to play from url
     */
    fun startPlayer(
        url: String,
        prepareCompleteCallback: (Int) -> Unit
    ) {
        mPlayer.apply {
            // stop playing
            stopPlayer()

            // start to play
            setDataSource(url)
            mPrepared = false
            prepareAsync()
            setOnPreparedListener {
                start()
                mPrepared = true
                mPlayingUrl = url
                prepareCompleteCallback.invoke(mPlayer.duration)
            }

            // clear playing url
            setOnCompletionListener { mPlayingUrl = "" }
        }
    }

    /**
     * Check the play is prepared or not
     */
    fun isPlayerPrepared() = mPrepared

    /**
     * Check the player is playing or not
     */
    fun isPlaying() = mPlayer.isPlaying

    /**
     * Get current duration of the player
     */
    fun getCurrentDuration() = mPlayer.currentPosition

    /**
     * Pause the player
     */
    fun pausePlayer() {
        mPlayer.pause()
    }

    /**
     * resume the player
     */
    fun resumePlayer() {
        mPlayer.start()
    }

    /**
     * move to assigned seconds
     *
     * @param direct indicate that this process is seek to the position directly or not
     */
    fun seekTo(
        seconds: Int,
        direct: Boolean
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            // total duration of the podcast
            val duration = mPlayer.duration
            // adjusted position of the podcast
            val adjustedPosition =
                if (direct) seconds
                else mPlayer.currentPosition + seconds * 1000
            // final position depends on the params above
            val finalPosition =
                if (adjustedPosition > duration) duration
                else adjustedPosition
            // seek to position
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mPlayer.seekTo(finalPosition.toLong(), SEEK_CLOSEST)
            } else {
                mPlayer.seekTo(finalPosition)
            }
        }
    }

    /**
     * Get current playing url
     */
    fun getPlayingUrl() = mPlayingUrl

    /* ------------------------------ Private Function */

    private fun stopPlayer() {
        mPlayer.apply {
            if (isPlaying) {
                stop()
                reset()
            }
        }
    }
}