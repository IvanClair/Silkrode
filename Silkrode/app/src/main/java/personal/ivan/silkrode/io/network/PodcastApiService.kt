package personal.ivan.silkrode.io.network

import personal.ivan.silkrode.io.model.CollectionData
import personal.ivan.silkrode.io.model.PodcastApiResponse
import personal.ivan.silkrode.io.model.PodcastData
import retrofit2.http.GET

interface PodcastApiService {

    @GET("getcasts")
    suspend fun getPodcastList(): PodcastApiResponse<PodcastData>

    @GET("getcastdetail")
    suspend fun getCollection(): PodcastApiResponse<CollectionData>
}