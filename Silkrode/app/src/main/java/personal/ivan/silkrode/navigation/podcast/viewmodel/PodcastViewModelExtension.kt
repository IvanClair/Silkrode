package personal.ivan.silkrode.navigation.podcast.viewmodel

import personal.ivan.silkrode.api.Podcast

/**
 * Get [Podcast] list from view model
 */
fun PodcastViewModel.getPodcastList(): List<Podcast>? = podcastList.value