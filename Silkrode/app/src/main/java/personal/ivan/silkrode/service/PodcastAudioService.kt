package personal.ivan.silkrode.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaPlayer.SEEK_CLOSEST
import android.os.Binder
import android.os.Build
import android.os.IBinder

class PodcastAudioService : Service() {

    // Media Player
    private lateinit var mPlayer: MediaPlayer

    // Local Binder
    private val mBinder = PodcastServiceBinder()

    // Flag
    private var prepared: Boolean = false

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
        prepareCompleteCallback: () -> Unit
    ) {
        mPlayer.apply {
            // stop playing
            stopPlayer()

            // start to play
            setDataSource(url)
            prepared = false
            prepareAsync()
            setOnPreparedListener {
                start()
                prepared = true
                prepareCompleteCallback.invoke()
            }
        }
    }

    /**
     * Check the play is prepared or not
     */
    fun isPlayerPrepared() = prepared

    /**
     * Check the player is playing or not
     */
    fun isPlaying() = mPlayer.isPlaying

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
     */
    fun seekTo(seconds: Int) {
        if (mPlayer.isPlaying) {
            val duration = mPlayer.duration
            val adjustedPosition = mPlayer.currentPosition + seconds * 1000
            val finalPosition = if (adjustedPosition > duration) duration else adjustedPosition
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mPlayer.seekTo(finalPosition.toLong(), SEEK_CLOSEST)
            } else {
                mPlayer.seekTo(finalPosition)
            }
        }
    }

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