package dev.logickoder.keyguarde.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.logickoder.keyguarde.BuildConfig
import dev.logickoder.keyguarde.app.data.dao.KeywordDao
import dev.logickoder.keyguarde.app.data.dao.SelectedAppDao
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.SelectedApp

@TypeConverters(Converters::class)
@Database(
    entities = [
        Keyword::class,
        SelectedApp::class,
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun keywordDao(): KeywordDao

    abstract fun selectedAppDao(): SelectedAppDao

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
            }
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "${BuildConfig.APPLICATION_ID}.db"
            ).addCallback(callback).build()
        }
    }
}