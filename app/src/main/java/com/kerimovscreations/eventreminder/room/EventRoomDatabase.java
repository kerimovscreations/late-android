package com.kerimovscreations.eventreminder.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.kerimovscreations.eventreminder.DAOs.EventDao;
import com.kerimovscreations.eventreminder.models.Event;

@Database(entities = {Event.class}, version = 2, exportSchema = false)
public abstract class EventRoomDatabase extends RoomDatabase {
    public abstract EventDao eventDao();

    private static volatile EventRoomDatabase INSTANCE;

    static EventRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EventRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EventRoomDatabase.class, "event_database")
                            .addMigrations(MIGRATION_1_2)
                            .build();


                }
            }
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE event_table "
                    + " ADD COLUMN duration_mins INTEGER default 0 NOT NULL");
        }
    };
}