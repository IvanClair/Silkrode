package personal.ivan.silkrode.io.db

import androidx.room.TypeConverter
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import personal.ivan.silkrode.io.model.ContentFeed

class DbTypeConverter {

    @TypeConverter
    fun contentFeedToString(dataList: List<ContentFeed>?) =
        dataList
            ?.let { toJson(dataList) }
            ?: ""

    @TypeConverter
    fun stringToContentFeedList(data: String): List<ContentFeed>? =
        fromJson(data = data)


    /* ------------------------------ Convert Methods */

    @ToJson
    private inline fun <reified T> toJson(dataList: List<T>): String =
        createListBuilder<T>().toJson(dataList)

    @FromJson
    private inline fun <reified T> fromJson(data: String): List<T>? =
        createListBuilder<T>().fromJson(data)

    private inline fun <reified T> createListBuilder() =
        Moshi.Builder()
            .build()
            .adapter<List<T>>(Types.newParameterizedType(List::class.java, T::class.java))
}