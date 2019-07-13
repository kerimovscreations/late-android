package com.kerimovscreations.lateandroid.models;

public class ReminderOption {

    private String title;
    private int value;
    private boolean isSelected;
    private boolean isPlaying;

    public ReminderOption(String title, int value) {
        this.title = title;
        this.value = value;
        this.isSelected = false;
        this.isPlaying = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
