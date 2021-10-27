package gui;

import ast.Date;

import javax.swing.*;
import java.awt.*;

public class Event extends JButton implements Comparable<Event> {
    private String eventName;
    private String type;
    private Date date;
    private String startTime;
    private String endTime;
    private String place;
    private Color colour;
    private boolean allowOverlap;
    private boolean isPriority;
    private boolean isRecurring;

    public Event(String eventName, String type, Date date, String startTime, String endTime, String place, Color colour, boolean isPriority) {
        this.eventName = eventName;
        this.type = type;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.place = place;
        this.colour = colour;
        this.allowOverlap = false; //default. Change this by using the set method

        //default. Change this by using the set method
        if (this.type.toLowerCase().equals("class")) {
            this.isRecurring = true;
        }
        else{
            this.isRecurring = false;
        }

        this.isPriority = isPriority;


        setText("<html>" + "Event: " + eventName + "<br>" + "Time: " + startTime + "-" + endTime + "<br>" + "Location: " + place + "</html>");
        setBackground(colour);
    }

    public void setAllowOverlap(boolean allowOverlap) {
        this.allowOverlap = allowOverlap;
    }

    public void setRecurring(boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public Date getDate() {
        return this.date;
    }

    public int getStartingTime() {
        int startTime = Integer.parseInt(this.startTime.substring(0, 2));
        if (startTime == 24) {
            startTime = 0; //For example, 24:00 should return 00:00 since this is the first hour and time of the day
        }
        return startTime;
    }

    @Override
    public int compareTo(Event event) {
        int startTime = event.getStartingTime();
        /* For Ascending order*/
        return this.getStartingTime() - startTime;
    }
}
