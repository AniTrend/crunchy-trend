package co.anitrend.support.crunchyroll.data.dao.query

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.anitrend.support.crunchyroll.data.model.user.CrunchyUser
import io.wax911.support.data.dao.ISupportQuery

@Dao
interface CrunchyUserDao : ISupportQuery<CrunchyUser> {

    @Query("select count(user_id) from CrunchyUser")
    fun count(): Int

    @Query("select * from CrunchyUser")
    suspend fun findAll(): List<CrunchyUser>?

    @Query("select * from CrunchyUser where user_id = :userId")
    suspend fun findSingle(userId: Long): CrunchyUser?

    @Query("delete from CrunchyUser")
    suspend fun clearTable()

    @Query("select * from CrunchyUser")
    fun findAllLiveData(): LiveData<List<CrunchyUser>?>

    @Query("select * from CrunchyUser where user_id = :userId")
    fun findSingleLiveData(userId: Long): LiveData<CrunchyUser?>

    /**
     * Inserts or updates matching attributes on conflict
     *
     * @param attribute item/s to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(attribute: CrunchyUser): Long
}