package co.anitrend.support.crunchyroll.data.mapper.session

import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyMapper
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySession
import co.anitrend.support.crunchyroll.data.dao.query.CrunchyAuthenticationWithUserDao
import co.anitrend.support.crunchyroll.data.dao.query.CrunchySessionWithAuthenticatedUserDao
import co.anitrend.support.crunchyroll.data.dao.query.CrunchyUserDao
import co.anitrend.support.crunchyroll.data.entity.auth.CrunchyAuthenticationWithUser
import co.anitrend.support.crunchyroll.data.entity.session.CrunchySessionWithAuthenticatedUser
import kotlinx.coroutines.Job

class CrunchySessionMapper(
    parentJob: Job,
    private val authenticationWithUserDao: CrunchyAuthenticationWithUserDao,
    private val sessionWithAuthenticatedUserDao: CrunchySessionWithAuthenticatedUserDao
) : CrunchyMapper<CrunchySession, CrunchySessionWithAuthenticatedUser>(parentJob) {

    private suspend fun persistAuthenticationWith(source: CrunchySession): Long {
        val authenticationWithUser = CrunchyAuthenticationWithUser(
            userId = source.user.user_id,
            auth = source.auth,
            expires = source.expires
        )
        return authenticationWithUserDao.insertOrUpdate(authenticationWithUser)
    }


    /**
     * Creates mapped objects and handles the database operations which may be required to map various objects,
     * called in [retrofit2.Callback.onResponse] after assuring that the response was a success
     * @see [handleResponse]
     *
     * @param source the incoming data source type
     * @return Mapped object that will be consumed by [onResponseDatabaseInsert]
     */
    override suspend fun onResponseMapFrom(source: CrunchySession): CrunchySessionWithAuthenticatedUser {
        val authId = persistAuthenticationWith(source)

        return CrunchySessionWithAuthenticatedUser(
            session_id = source.session_id,
            country_code = source.country_code,
            device_type = source.device_type,
            device_id = source.device_id,
            version = source.version,
            authenticationId = authId,
            expires = source.expires,
            userId = source.user.user_id
        )
    }

    /**
     * Inserts the given object into the implemented room database,
     * called in [retrofit2.Callback.onResponse]
     * @see [handleResponse]
     *
     * @param mappedData mapped object from [onResponseMapFrom] to insert into the database
     */
    override suspend fun onResponseDatabaseInsert(mappedData: CrunchySessionWithAuthenticatedUser) {
        sessionWithAuthenticatedUserDao.upsert(mappedData)
    }
}