package co.anitrend.support.crunchyroll.data.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySession
import co.anitrend.support.crunchyroll.data.dao.migration.MIGRATION_1_2
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySessionCore
import co.anitrend.support.crunchyroll.data.dao.query.CrunchyAuthenticationWithUserDao
import co.anitrend.support.crunchyroll.data.dao.query.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.dao.query.CrunchySessionWithAuthenticatedUserDao
import co.anitrend.support.crunchyroll.data.dao.query.CrunchyUserDao
import co.anitrend.support.crunchyroll.data.entity.auth.CrunchyAuthenticationWithUser
import co.anitrend.support.crunchyroll.data.entity.session.CrunchySessionWithAuthenticatedUser
import co.anitrend.support.crunchyroll.data.model.user.CrunchyUser

@Database(
    entities = [
        CrunchySessionWithAuthenticatedUser::class, CrunchyAuthenticationWithUser::class,
        CrunchySessionCore::class, CrunchyUser::class
    ],
    version = BuildConfig.DATABASE_SCHEMA_VERSION
)
abstract class CrunchyDatabase: RoomDatabase() {

    abstract fun crunchySessionDao(): CrunchySessionWithAuthenticatedUserDao
    abstract fun crunchySessionCoreDao(): CrunchySessionCoreDao

    abstract fun crunchyUserDao(): CrunchyUserDao
    abstract fun crunchyAuthenticationWithUserDao(): CrunchyAuthenticationWithUserDao

    companion object {
        fun newInstance(context: Context): CrunchyDatabase {
            return Room.databaseBuilder(
                context,
                CrunchyDatabase::class.java,
                "crunchy-db"
            ).fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }
}