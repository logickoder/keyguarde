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
    version = 3,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun keywordDao(): KeywordDao

    abstract fun watchedAppDao(): WatchedAppDao

    abstract fun keywordMatchDao(): KeywordMatchDao

    companion object {
        fun build(context: Context): AppDatabase {
            lateinit var database: AppDatabase
            val callback = object : Callback() {
                override suspend fun onCreate(connection: SQLiteConnection) {
                    super.onCreate(connection)
                    if (!BuildConfig.DEBUG) {
                        return
                    }
                    AppScope.launch(Dispatchers.IO) {
                        PrepopulateDatabaseUsecase(
                            keywordDao = database.keywordDao(),
                            watchedAppDao = database.watchedAppDao(),
                            keywordMatchDao = database.keywordMatchDao(),
                        )()
                    }
                }
            }
            database = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "${BuildConfig.APPLICATION_ID}.db"
            ).setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .addCallback(callback)
                .build()
            return database
        }
    }
}
