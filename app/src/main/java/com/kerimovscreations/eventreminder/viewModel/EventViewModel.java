package com.kerimovscreations.eventreminder.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.kerimovscreations.eventreminder.models.Event;
import com.kerimovscreations.eventreminder.room.EventRepository;

import java.util.List;

public class EventViewModel extends AndroidViewModel {

    private EventRepository mRepository;

    private LiveData<List<Event>> mAllEvents;

    public EventViewModel(Application application) {
        super(application);
        mRepository = new EventRepository(application);
        mAllEvents = mRepository.getAllEvents();
    }

    public LiveData<List<Event>> getAllEvents() {
        return mAllEvents;
    }

    public void insert(Event event) {
        mRepository.insert(event);
    }

    public void delete(Event event) {
        mRepository.delete(event);
    }

    public void delete(int index) {
        if (getAllEvents().getValue() != null) {
            Event deleteEvent = getAllEvents().getValue().get(index);
            if (deleteEvent != null)
                mRepository.delete(deleteEvent);
        }
    }
}