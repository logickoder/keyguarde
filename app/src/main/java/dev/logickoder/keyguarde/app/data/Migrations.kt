package dev.logickoder.keyguarde.app.data

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_1_2 = object : Migration(1, 2) {
    override suspend fun migrate(connection: SQLiteConnection) {
        // Recreate the keyword_matches table to rename id to rowid
        connection.execSQL(
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
        connection.execSQL(
            """
            INSERT INTO keyword_matches_new (rowid, keywords, message, chat, app, timestamp)
            SELECT id, keywords, message, chat, app, timestamp FROM keyword_matches
        """
        )
        connection.execSQL("DROP TABLE keyword_matches")
        connection.execSQL("ALTER TABLE keyword_matches_new RENAME TO keyword_matches")

        // Recreate the indices
        connection.execSQL("CREATE INDEX `index_keyword_matches_app` ON `keyword_matches` (`app`)")
        connection.execSQL("CREATE UNIQUE INDEX `index_keyword_matches_message_chat_app` ON `keyword_matches` (`message`, `chat`, `app`)")

        // Recreate the fts table for keyword matches
        connection.execSQL(
            """
            CREATE VIRTUAL TABLE `keyword_matches_fts` USING fts4(
                content=`keyword_matches`,
                keywords,
                message,
                chat
            )
            """
        )
        // Repopulate the fts table
        connection.execSQL(
            """
            INSERT INTO `keyword_matches_fts` (docid, keywords, message, chat)
            SELECT `rowid`, keywords, message, chat FROM `keyword_matches`
            """
        )
    }
}
