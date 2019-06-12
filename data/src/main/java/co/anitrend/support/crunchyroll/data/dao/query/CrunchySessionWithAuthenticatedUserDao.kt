package co.anitrend.support.crunchyroll.data.dao.query

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySession
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySessionCore
import co.anitrend.support.crunchyroll.data.entity.session.CrunchySessionWithAuthenticatedUser
import io.wax911.support.data.dao.ISupportQuery

@Dao
interface CrunchySessionWithAuthenticatedUserDao : ISupportQuery<CrunchySessionWithAuthenticatedUser> {

    @Query("select count(session_id) from CrunchySessionWithAuthenticatedUser")
    fun count(): Int

    @Query("select * from CrunchySessionWithAuthenticatedUser order by date(expires) desc limit 1")
    fun findLatest(): CrunchySessionWithAuthenticatedUser?

    @Query("select * from CrunchySessionWithAuthenticatedUser")
    suspend fun findAll(): List<CrunchySessionWithAuthenticatedUser>?

    @Query("delete from CrunchySessionWithAuthenticatedUser")
    fun clearTable()

    @Query("select * from CrunchySessionWithAuthenticatedUser order by date(expires) desc limit 1")
    fun findLatestLiveData(): LiveData<CrunchySessionWithAuthenticatedUser?>

    @Query("select * from CrunchySessionWithAuthenticatedUser")
    fun findAllLiveData(): LiveData<List<CrunchySessionWithAuthenticatedUser>?>
}