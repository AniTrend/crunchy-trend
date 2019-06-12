package co.anitrend.support.crunchyroll.data.dao.query

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.anitrend.support.crunchyroll.data.entity.auth.CrunchyAuthenticationWithUser
import io.wax911.support.data.dao.ISupportQuery

@Dao
interface CrunchyAuthenticationWithUserDao : ISupportQuery<CrunchyAuthenticationWithUser> {

    @Query("select count(auth) from CrunchyAuthenticationWithUser")
    fun count(): Int

    @Query("select * from CrunchyAuthenticationWithUser order by date(expires) desc limit 1")
    fun findLatest(): CrunchyAuthenticationWithUser?

    @Query("select * from CrunchyAuthenticationWithUser")
    suspend fun findAll(): List<CrunchyAuthenticationWithUser>?

    @Query("delete from CrunchyAuthenticationWithUser")
    fun clearTable()

    @Query("select * from CrunchyAuthenticationWithUser order by date(expires) desc limit 1")
    fun findLatestLiveData(): LiveData<CrunchyAuthenticationWithUser?>

    @Query("select * from CrunchyAuthenticationWithUser")
    fun findAllLiveData(): LiveData<List<CrunchyAuthenticationWithUser>?>

    /**
     * Inserts or updates matching attributes on conflict
     *
     * @param attribute item/s to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(attribute: CrunchyAuthenticationWithUser): Long
}