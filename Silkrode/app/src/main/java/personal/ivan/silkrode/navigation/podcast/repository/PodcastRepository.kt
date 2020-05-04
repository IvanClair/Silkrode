package personal.ivan.silkrode.navigation.podcast.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import personal.ivan.silkrode.io.db.CollectionDao
import personal.ivan.silkrode.io.db.PodcastDao
import personal.ivan.silkrode.io.model.ApiStatus
import personal.ivan.silkrode.io.component.IoComponent
import personal.ivan.silkrode.io.model.Collection
import personal.ivan.silkrode.io.model.Podcast
import personal.ivan.silkrode.io.network.PodcastApiService
import personal.ivan.silkrode.navigation.podcast.model.CollectionBindingModel
import personal.ivan.silkrode.util.DateFormatUtil
import javax.inject.Inject

class PodcastRepository @Inject constructor(
    private val mService: PodcastApiService,
    private val mPodcastDao: PodcastDao,
    private val mCollectionDao: CollectionDao,
    private val mUtil: DateFormatUtil
) {

    /**
     * Get podcast list
     */
    fun getPodcastList() =
        object : IoComponent<List<Podcast>, List<Podcast>>() {
            override suspend fun loadFromDb(): List<Podcast>? =
                mPodcastDao.loadAll()

            override suspend fun loadFromNetwork(): List<Podcast>? =
                mService.getPodcastList().data?.podcastList

            override suspend fun convertFromSource(source: List<Podcast>?): List<Podcast>? =
                source

            override suspend fun saveToDb(data: List<Podcast>) =
                mPodcastDao.insertAll(dataList = data)

        }.getLiveData()

    /**
     * Get collection
     */
    fun getCollection(collectionId: Int): LiveData<ApiStatus<CollectionBindingModel>> {

        // convert collection to collection binding model
        suspend fun convert(data: Collection?) =
            data?.let {
                withContext(Dispatchers.IO) {
                    CollectionBindingModel(
                        data = it,
                        util = mUtil
                    )
                }
            }

        return object : IoComponent<Collection, CollectionBindingModel>() {
            override suspend fun loadFromDb(): Collection? =
                mCollectionDao.load(id = collectionId)

            override suspend fun loadFromNetwork(): Collection? =
                mService.getCollection().data?.collection

            override suspend fun convertFromSource(source: Collection?): CollectionBindingModel? =
                convert(data = source)

            override suspend fun saveToDb(data: Collection) =
                mCollectionDao.insert(data = data)

        }.getLiveData()
    }
}