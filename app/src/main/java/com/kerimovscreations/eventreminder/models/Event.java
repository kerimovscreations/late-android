package com.kerimovscreations.eventreminder.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "event_table")
public class Event {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title, date;
    @NonNull
    private int duration_mins;

    public Event(String title, String date, int duration_mins) {
        this.title = title;
        this.date = date;
        this.duration_mins = duration_mins;
    }

    public int getDuration_mins() {
        return duration_mins;
    }

    public void setDuration_mins(int duration_mins) {
        this.duration_mins = duration_mins;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public String getDateConverted() {
        Date dateObj = new Date();

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        try {
            dateObj = format.parse(this.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat out = new SimpleDateFormat("HH:mm, dd MMM yyyy", Locale.getDefault());

        return out.format(dateObj);
    }

    public Date getDateObj() {
        Date dateObj = new Date();

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        try {
            dateObj = format.parse(this.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.e("ERR33", this.date);
        return dateObj;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
