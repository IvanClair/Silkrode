package personal.ivan.silkrode.db

import androidx.room.*
import personal.ivan.silkrode.api.Collection
import personal.ivan.silkrode.api.Podcast

/* ------------------------------ Database */

@Database(
    entities = [
        Podcast::class,
        Collection::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DbTypeConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun podcastDao(): PodcastDao

    abstract fun collectionDao(): CollectionDao
}

/* ------------------------------ Data Access Object */

@Dao
interface PodcastDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dataList: List<Podcast>)

    @Query("SELECT * FROM Podcast")
    suspend fun loadAll(): List<Podcast>
}

@Dao
interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: Collection)

    @Query("SELECT * FROM Collection WHERE collectionId IN (:id)")
    suspend fun load(id: Int): Collection
}