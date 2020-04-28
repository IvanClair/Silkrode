package personal.ivan.silkrode.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaPlayer.SEEK_CLOSEST
import android.os.Build
import android.os.IBinder
import androidx.annotation.StringDef

class PodcastAudioService : Service() {

    // Constant
    companion object {
        // Action
        @StringDef(
            PODCAST_ACTION_START,
            PODCAST_ACTION_PAUSE,
            PODCAST_ACTION_RESUME,
            PODCAST_ACTION_SEEK_TO
        )
        @Retention(AnnotationRetention.SOURCE)
        annotation class PodcastServiceAction

        const val PODCAST_ACTION_START = "PODCAST_ACTION_START"
        const val PODCAST_ACTION_PAUSE = "PODCAST_ACTION_PAUSE"
        const val PODCAST_ACTION_RESUME = "PODCAST_ACTION_RESUME"
        const val PODCAST_ACTION_SEEK_TO = "PODCAST_ACTION_SEEK_TO"

        // Bundle Tag
        @StringDef(
            PODCAST_BUNDLE_TAG_URL,
            PODCAST_BUNDLE_TAG_SEEK_TO
        )
        @Retention(AnnotationRetention.SOURCE)
        annotation class PodcastServiceBundleTag

        const val PODCAST_BUNDLE_TAG_URL = "PODCAST_BUNDLE_TAG_URL"
        const val PODCAST_BUNDLE_TAG_SEEK_TO = "PODCAST_BUNDLE_TAG_SEEK_TO"
    }

    // Media Player
    private val mPlayer: MediaPlayer =
        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes
                    .Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        }

    /* ------------------------------ Override */

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        when (intent?.action) {
            // start player
            PODCAST_ACTION_START ->
                intent.extras
                    ?.getString(PODCAST_BUNDLE_TAG_URL)
                    ?.also { startPlayer(it) }

            // pause player
            PODCAST_ACTION_PAUSE -> pausePlayer()

            // resume player
            PODCAST_ACTION_RESUME -> resumePlayer()

            // seek to
            PODCAST_ACTION_SEEK_TO -> {
                intent.extras
                    ?.getInt(PODCAST_BUNDLE_TAG_SEEK_TO)
                    ?.also { seekTo(second = it) }
            }
        }
        return START_STICKY
    }

    /* ------------------------------ Private Function */

    private fun startPlayer(url: String) {
        mPlayer.apply {
            // stop playing
            if (isPlaying) {
                stop()
            }

            // start to play
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener { start() }
        }
    }

    private fun pausePlayer() {
        mPlayer.pause()
    }

    private fun resumePlayer() {
        mPlayer.start()
    }

    private fun seekTo(second: Int) {
        val position = mPlayer.currentPosition + second * 1000
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPlayer.seekTo(position.toLong(), SEEK_CLOSEST)
        } else {
            mPlayer.seekTo(position)
        }
    }
}