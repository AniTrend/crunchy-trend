package co.anitrend.support.crunchyroll.data.mapper.auth

import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyMapper
import co.anitrend.support.crunchyroll.data.auth.model.CrunchyAuthUser
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySession
import co.anitrend.support.crunchyroll.data.dao.query.CrunchyAuthenticationWithUserDao
import co.anitrend.support.crunchyroll.data.dao.query.CrunchyUserDao
import co.anitrend.support.crunchyroll.data.entity.auth.CrunchyAuthenticationWithUser
import kotlinx.coroutines.Job

class CrunchyAuthenticationMapper(
    parentJob: Job,
    private val authenticatedUserDao: CrunchyUserDao,
    private val authenticationWithUserDao: CrunchyAuthenticationWithUserDao
    ) : CrunchyMapper<CrunchyAuthUser, CrunchyAuthenticationWithUser>(parentJob) {

    private suspend fun persistAuthenticatedUserWith(source: CrunchyAuthUser): Long {
        return authenticatedUserDao.insertOrUpdate(source.user)
    }

    /**
     * Creates mapped objects and handles the database operations which may be required to map various objects,
     * called in [retrofit2.Callback.onResponse] after assuring that the response was a success
     * @see [handleResponse]
     *
     * @param source the incoming data source type
     * @return Mapped object that will be consumed by [onResponseDatabaseInsert]
     */
    override suspend fun onResponseMapFrom(source: CrunchyAuthUser): CrunchyAuthenticationWithUser {
        val userId = persistAuthenticatedUserWith(source)

        return CrunchyAuthenticationWithUser(
            userId = userId,
            auth = source.auth,
            expires = source.expires
        )
    }

    /**
     * Inserts the given object into the implemented room database,
     * called in [retrofit2.Callback.onResponse]
     * @see [handleResponse]
     *
     * @param mappedData mapped object from [onResponseMapFrom] to insert into the database
     */
    override suspend fun onResponseDatabaseInsert(mappedData: CrunchyAuthenticationWithUser) {
        authenticationWithUserDao.upsert(mappedData)
    }

}