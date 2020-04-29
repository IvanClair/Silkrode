package personal.ivan.silkrode.api

import personal.ivan.silkrode.navigation.podcast.model.CollectionBindingModel
import personal.ivan.silkrode.util.DateFormatUtil
import javax.inject.Inject

class PodcastRepository @Inject constructor(
    private val mService: PodcastApiService,
    private val mUtil: DateFormatUtil
) {

    /**
     * Get podcast list
     */
    fun getPodcastList() =
        object : ApiUtil<PodcastApiResponse<PodcastData>, List<Podcast>>() {
            override suspend fun getApiResponse(): PodcastApiResponse<PodcastData> =
                mService.getPodcastList()

            override suspend fun convertResponse(apiRs: PodcastApiResponse<PodcastData>) =
                apiRs.data?.podcastList

        }.getLiveData()

    fun getCollectionn() =
        object : ApiUtil<PodcastApiResponse<CollectionData>, CollectionBindingModel>() {
            override suspend fun getApiResponse(): PodcastApiResponse<CollectionData> =
                mService.getCollection()

            override suspend fun convertResponse(apiRs: PodcastApiResponse<CollectionData>) =
                apiRs.data?.collection?.let {
                    CollectionBindingModel(
                        data = it,
                        util = mUtil
                    )
                }

        }.getLiveData()

    /**
     * Get collection of a certain artist
     */
    suspend fun getCollection(): Collection? =
        try {
            mService.getCollection().data?.collection
        } catch (e: Exception) {
            null
        }
}