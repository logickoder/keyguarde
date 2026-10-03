package dev.logickoder.keyguarde.app.data

import android.content.Context
import androidx.room3.ColumnTypeConverters
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.logickoder.keyguarde.BuildConfig
import dev.logickoder.keyguarde.app.data.dao.KeywordDao
import dev.logickoder.keyguarde.app.data.dao.KeywordMatchDao
import dev.logickoder.keyguarde.app.data.dao.WatchedAppDao
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.KeywordMatchFts
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.domain.AppScope
import dev.logickoder.keyguarde.app.domain.usecase.PrepopulateDatabaseUsecase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ColumnTypeConverters(Converters::class)
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
@Database(
    entities = [
        Keyword::class,
        WatchedApp::class,
        KeywordMatch::class,
        KeywordMatchFts::class,
    ],
    version = 2,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun keywordDao(): KeywordDao

    abstract fun watchedAppDao(): WatchedAppDao

    abstract fun keywordMatchDao(): KeywordMatchDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance = buildDatabase(context)
                instance!!
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            val callback = object : Callback() {
                override suspend fun onCreate(connection: SQLiteConnection) {
                    super.onCreate(connection)
                    val db = instance
                    if (db == null || !BuildConfig.DEBUG) {
                        return
                    }
                    AppScope.launch(Dispatchers.IO) {
                        PrepopulateDatabaseUsecase(
                            keywordDao = db.keywordDao(),
                            watchedAppDao = db.watchedAppDao(),
                            keywordMatchDao = db.keywordMatchDao(),
                        )()
                    }
                }
            }
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "${BuildConfig.APPLICATION_ID}.db"
            ).setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .addMigrations(MIGRATION_1_2)
                .addCallback(callback)
                .build()
        }
    }
}
