package com.kerimovscreations.eventreminder.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.kerimovscreations.eventreminder.DAOs.EventDao;
import com.kerimovscreations.eventreminder.models.Event;

@Database(entities = {Event.class}, version = 1, exportSchema = false)
public abstract class EventRoomDatabase extends RoomDatabase {
    public abstract EventDao eventDao();

    private static volatile EventRoomDatabase INSTANCE;

    static EventRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EventRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EventRoomDatabase.class, "event_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}