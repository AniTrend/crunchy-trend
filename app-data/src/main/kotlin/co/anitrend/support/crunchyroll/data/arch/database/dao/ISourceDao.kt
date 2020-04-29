package co.anitrend.support.crunchyroll.data.arch.database.dao

internal interface ISourceDao {
    suspend fun clearTable()
    suspend fun count(): Int
}