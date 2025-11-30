package dev.logickoder.keyguarde.app.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Recreate the keyword_matches table to rename id to rowid
        db.execSQL(
            """
                CREATE TABLE keyword_matches_new (
                `rowid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `keywords` TEXT NOT NULL, 
                `message` TEXT NOT NULL, 
                `chat` TEXT NOT NULL, 
                `app` TEXT NOT NULL, 
                `timestamp` INTEGER NOT NULL, 
                FOREIGN KEY(`app`) REFERENCES `selected_apps`(`packageName`) ON UPDATE NO ACTION ON DELETE CASCADE 
            )
        """
        )
        db.execSQL(
            """
            INSERT INTO keyword_matches_new (rowid, keywords, message, chat, app, timestamp)
            SELECT id, keywords, message, chat, app, timestamp FROM keyword_matches
        """
        )
        db.execSQL("DROP TABLE keyword_matches")
        db.execSQL("ALTER TABLE keyword_matches_new RENAME TO keyword_matches")

        // Recreate the indices
        db.execSQL("CREATE INDEX `index_keyword_matches_app` ON `keyword_matches` (`app`)")
        db.execSQL("CREATE UNIQUE INDEX `index_keyword_matches_message_chat_app` ON `keyword_matches` (`message`, `chat`, `app`)")
    }
}
