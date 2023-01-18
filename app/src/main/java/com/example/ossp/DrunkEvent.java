package com.example.ossp;

public class DrunkEvent {
    private int stHour, stMin, edHour, edMin;
    private float count;

    public DrunkEvent() {
    }

    public DrunkEvent(int stHour, int stMin, int edHour, int edMin, float count) {
        this.stHour = stHour;
        this.stMin = stMin;
        this.edHour = edHour;
        this.edMin = edMin;
        this.count = count;
    }

    public void setStart(int stHour, int stMin) {
        this.stHour = stHour;
        this.stMin = stMin;
    }
    public void setEnd(int edHour, int edMin) {
        this.edHour = edHour;
        this.edMin = edMin;
    }
    public void setCount(float count) {
        this.count = count;
    }

    public int getStHour() {
        return stHour;
    }

    public int getStMin() {
        return stMin;
    }

    public int getEdHour() {
        return edHour;
    }

    public int getEdMin() {
        return edMin;
    }

    public float getCount() {
        return count;
    }
}
