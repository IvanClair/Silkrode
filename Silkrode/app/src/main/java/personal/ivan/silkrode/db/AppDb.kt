package personal.ivan.silkrode.db

import androidx.room.*
import personal.ivan.silkrode.api.Podcast

/* ------------------------------ Database */

@Database(entities = [Podcast::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun podcastDao(): PodcastDao
}

/* ------------------------------ Data Access Object */

@Dao
interface PodcastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dataList: List<Podcast>)

    @Query("SELECT * FROM Podcast")
    suspend fun loadAll(): List<Podcast>
}