package personal.ivan.silkrode.api

import javax.inject.Inject

/*
    TODO Retrofit throws exception if network is unavailable,
     maybe they will update in future,
     keep an eye on new release
 */
class PodcastRepository @Inject constructor(private val mService: PodcastService) {

    /**
     * Get podcast list
     */
    suspend fun getPodcastList(): List<Podcast>? =
        try {
            mService.getPodcastList().data?.podcastList
        } catch (e: Exception) {
            null
        }

    /**
     * Get collection of a certain artist
     */
    suspend fun getCastDetail(): Collection? =
        try {
            mService.getCastDetail().data?.collection
        } catch (e: Exception) {
            null
        }
}