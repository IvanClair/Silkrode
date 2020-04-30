package personal.ivan.silkrode.api

import personal.ivan.silkrode.db.PodcastDao
import personal.ivan.silkrode.navigation.podcast.model.CollectionBindingModel
import personal.ivan.silkrode.util.DateFormatUtil
import javax.inject.Inject

class PodcastRepository @Inject constructor(
    private val mService: PodcastApiService,
    private val mPodcastDao: PodcastDao,
    private val mUtil: DateFormatUtil
) {

    /**
     * Get podcast list
     */
    fun getPodcastList() =
        object : ApiUtil<PodcastApiResponse<PodcastData>, List<Podcast>>() {
            override suspend fun loadFromDb(): List<Podcast>? =
                mPodcastDao
                    .loadAll()
                    .let { if (it.isNotEmpty()) it else null }

            override suspend fun loadFromNetwork(): PodcastApiResponse<PodcastData> =
                mService.getPodcastList()

            override suspend fun convertResponse(apiRs: PodcastApiResponse<PodcastData>) =
                apiRs.data?.podcastList

            override suspend fun saveToDb(data: List<Podcast>) {
                mPodcastDao.insertAll(dataList = data)
            }

        }.getLiveData()

    /**
     * Get collection of a certain artist
     */
    fun getCollection() =
        object : ApiUtil<PodcastApiResponse<CollectionData>, CollectionBindingModel>() {
            override suspend fun loadFromDb(): CollectionBindingModel? = null

            override suspend fun loadFromNetwork(): PodcastApiResponse<CollectionData> =
                mService.getCollection()

            override suspend fun convertResponse(apiRs: PodcastApiResponse<CollectionData>) =
                apiRs.data?.collection?.let {
                    CollectionBindingModel(
                        data = it,
                        util = mUtil
                    )
                }

            override suspend fun saveToDb(data: CollectionBindingModel) {
            }

        }.getLiveData()
}