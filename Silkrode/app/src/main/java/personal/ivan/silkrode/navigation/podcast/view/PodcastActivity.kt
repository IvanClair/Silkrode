package personal.ivan.silkrode.navigation.podcast.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import personal.ivan.silkrode.databinding.ActivityPodcastBinding

class PodcastActivity : AppCompatActivity() {

    // View Binding
    private val mBinding: ActivityPodcastBinding by lazy {
        ActivityPodcastBinding.inflate(layoutInflater)
    }

    /* ------------------------------ Life Cycle */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
    }
}