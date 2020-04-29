package personal.ivan.silkrode.api

import javax.inject.Inject

/*
    TODO Retrofit throws exception if network is unavailable,
     maybe they will update in future, keep an eye on new release
 */
class PodcastRepository @Inject constructor(private val mService: PodcastApiService) {

    /**
     * Get podcast list
     */
    fun fetchPodcastList() =
        object : ApiUtil<PodcastApiResponse<PodcastData>, List<Podcast>>() {

            override suspend fun getApiResponse(): PodcastApiResponse<PodcastData> =
                mService.getPodcastList()

            override suspend fun convertResponse(apiRs: PodcastApiResponse<PodcastData>): List<Podcast>? =
                apiRs.data?.podcastList

        }.getLiveData()

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