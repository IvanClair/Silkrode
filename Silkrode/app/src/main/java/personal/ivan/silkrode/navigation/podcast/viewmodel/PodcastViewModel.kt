package personal.ivan.silkrode.navigation.podcast.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import personal.ivan.silkrode.SilkrodeApplication
import personal.ivan.silkrode.api.Podcast
import personal.ivan.silkrode.api.PodcastRepository
import javax.inject.Inject

class PodcastViewModel @Inject constructor(
    application: SilkrodeApplication,
    private val mRepository: PodcastRepository
) : AndroidViewModel(application) {

    // API status
    val apiLoading: MutableLiveData<Boolean> = MutableLiveData()
    val apiFail: MutableLiveData<Boolean> = MutableLiveData()

    // API response - podcast list
    val podcastList: LiveData<List<Podcast>> =
        liveData {
            apiLoading.value = true
            val result = mRepository.getPodcastList()?.also { emit(it) }
            if (result == null) apiFail.value = true
            apiLoading.value = false
        }

    // UI
    val toolbarTitle: MutableLiveData<String> = MutableLiveData()
}