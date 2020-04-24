package personal.ivan.silkrode.navigation.podcast.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import personal.ivan.silkrode.api.Podcast
import personal.ivan.silkrode.api.PodcastRepository
import javax.inject.Inject

class PodcastViewModel @Inject constructor(private val mRepository: PodcastRepository) :
    ViewModel() {

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
}