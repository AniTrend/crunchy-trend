package co.anitrend.support.crunchyroll.data.arch.database.dao

interface ISourceDao {
    suspend fun clearTable()
    suspend fun count(): Int
}