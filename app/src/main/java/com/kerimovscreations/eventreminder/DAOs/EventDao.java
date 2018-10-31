package com.kerimovscreations.eventreminder.DAOs;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.kerimovscreations.eventreminder.models.Event;

import java.util.List;

@Dao
public interface EventDao {

    @Insert
    void insert(Event event);

    @Query("DELETE FROM event_table")
    void deleteAll();

    @Delete
    void delete(Event event);

    @Query("SELECT * from event_table ORDER BY date ASC")
    LiveData<List<Event>> getAllEvents();
}
