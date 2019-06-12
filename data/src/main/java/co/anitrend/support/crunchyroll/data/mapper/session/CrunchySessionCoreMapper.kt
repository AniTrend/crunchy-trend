package co.anitrend.support.crunchyroll.data.mapper.session

import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyMapper
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySessionCore
import co.anitrend.support.crunchyroll.data.dao.query.CrunchySessionCoreDao
import kotlinx.coroutines.Job

class CrunchySessionCoreMapper(
    parentJob: Job,
    private val sessionCoreDao: CrunchySessionCoreDao
) : CrunchyMapper<CrunchySessionCore, CrunchySessionCore>(parentJob) {

    /**
     * Creates mapped objects and handles the database operations which may be required to map various objects,
     * called in [retrofit2.Callback.onResponse] after assuring that the response was a success
     * @see [handleResponse]
     *
     * @param source the incoming data source type
     * @return Mapped object that will be consumed by [onResponseDatabaseInsert]
     */
    override suspend fun onResponseMapFrom(source: CrunchySessionCore): CrunchySessionCore {
        return source
    }

    /**
     * Inserts the given object into the implemented room database,
     * called in [retrofit2.Callback.onResponse]
     * @see [handleResponse]
     *
     * @param mappedData mapped object from [onResponseMapFrom] to insert into the database
     */
    override suspend fun onResponseDatabaseInsert(mappedData: CrunchySessionCore) {
        sessionCoreDao.upsert(mappedData)
    }
}