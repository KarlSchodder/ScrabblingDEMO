package com.example.ccl3_scrabbling.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context

@Database(
    entities = [
        StatsEntity::class,
        SettingsEntity::class,
        WordHistoryEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class OfflineDatabase : RoomDatabase() {
    abstract fun statsDao(): StatsDao
    abstract fun settingsDao(): SettingsDao
    abstract fun wordHistoryDao(): WordHistoryDao

    companion object {
        // Previous migration
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE stats_table ADD COLUMN highestWordScore INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        // New migration
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS word_history_table (
                        word TEXT PRIMARY KEY NOT NULL,
                        timesUsed INTEGER NOT NULL,
                        totalScore INTEGER NOT NULL
                    )
                """)
            }
        }
    }
}

object DatabaseProvider {
    @Volatile
    private var INSTANCE: OfflineDatabase? = null

    fun getDatabase(context: Context): OfflineDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                OfflineDatabase::class.java,
                "offline_database"
            )
                .addMigrations(OfflineDatabase.MIGRATION_1_2, OfflineDatabase.MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build()
            INSTANCE = instance
            instance
        }
    }
}