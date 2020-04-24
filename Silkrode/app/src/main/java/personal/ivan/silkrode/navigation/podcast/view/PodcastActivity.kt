package personal.ivan.silkrode.navigation.podcast.view

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerAppCompatActivity
import personal.ivan.silkrode.R
import personal.ivan.silkrode.databinding.ActivityPodcastBinding
import personal.ivan.silkrode.di.AppViewModelFactory
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import javax.inject.Inject

class PodcastActivity : DaggerAppCompatActivity() {

    // View Model
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val mViewModel: PodcastViewModel by viewModels { viewModelFactory }

    // View Binding
    private val mBinding: ActivityPodcastBinding by lazy {
        ActivityPodcastBinding.inflate(layoutInflater)
    }

    /* ------------------------------ Life Cycle */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        observeLiveData()
    }

    /* ------------------------------ Observe LiveData */

    /**
     * Observe LiveData in [PodcastViewModel]
     */
    private fun observeLiveData() {
        mViewModel.apply {
            // API status - loading
            apiLoading.observe(
                this@PodcastActivity,
                Observer { switchLoadingProgress(enable = it) })

            // API status - fail
            apiFail.observe(
                this@PodcastActivity,
                Observer { showApiError() })

            // Toolbar title text
            toolbarTitle.observe(
                this@PodcastActivity,
                Observer { updateToolbarTitle(it) })
        }
    }

    /* ------------------------------ UI */

    /**
     * Update Toolbar title
     */
    private fun updateToolbarTitle(title: String) {
        mBinding.toolbar.title = title
    }

    /**
     * Show or hide loading progress
     */
    private fun switchLoadingProgress(enable: Boolean) {
        mBinding.progressBarLoading.visibility = if (enable) View.VISIBLE else View.GONE
    }

    /**
     * Show API error
     */
    private fun showApiError() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.alert_title)
            .setMessage(R.string.alert_message)
            .setPositiveButton(R.string.label_ok, null)
            .show()
    }
}