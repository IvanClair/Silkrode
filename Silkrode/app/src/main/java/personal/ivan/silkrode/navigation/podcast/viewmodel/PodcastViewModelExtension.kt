package personal.ivan.silkrode.navigation.podcast.viewmodel

import androidx.annotation.StringRes
import personal.ivan.silkrode.SilkrodeApplication
import personal.ivan.silkrode.api.Podcast

/**
 * Set up Toolbar title
 *
 * @param resId id of the string in string file
 */
infix fun PodcastViewModel.setToolbarTitle(@StringRes resId: Int) {
    toolbarTitle.value = getApplication<SilkrodeApplication>().getString(resId)
}

/**
 * Get [Podcast] list from view model
 */
fun PodcastViewModel.getPodcastList(): List<Podcast>? = podcastList.value