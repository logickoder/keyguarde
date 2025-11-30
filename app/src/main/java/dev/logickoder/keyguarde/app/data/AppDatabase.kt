package dev.logickoder.keyguarde.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
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

@TypeConverters(Converters::class)
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
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
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
            ).addMigrations(MIGRATION_1_2).addCallback(callback).build()
        }
    }
}
