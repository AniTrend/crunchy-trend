package co.anitrend.support.crunchyroll.data.dao.query

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySessionCore
import io.wax911.support.data.dao.ISupportQuery

@Dao
interface CrunchySessionCoreDao : ISupportQuery<CrunchySessionCore> {

    @Query("select count(session_id) from CrunchySessionCore")
    fun count(): Int

    @Query("select * from CrunchySessionCore order by date(sessionCoreId) desc limit 1")
    suspend fun findLatest(): CrunchySessionCore?

    @Query("select * from CrunchySessionCore")
    suspend fun findAll(): List<CrunchySessionCore>?

    @Query("delete from CrunchySessionCore")
    suspend fun clearTable()

    @Query("select * from CrunchySessionCore order by date(sessionCoreId) desc limit 1")
    fun findLatestLiveData(): LiveData<CrunchySessionCore?>

    @Query("select * from CrunchySessionCore")
    fun findAllLiveData(): LiveData<List<CrunchySessionCore>?>
}