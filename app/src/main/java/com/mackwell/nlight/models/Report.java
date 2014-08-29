package com.mackwell.nlight.models;

import java.util.Calendar;

/**
 * Created by weiyuan zhu on 29/08/14.
 * Report class representing panel report
 */
public class Report {

    /**
     * Fields and properties
     */

    private int faults;
    private Calendar date;
    private boolean status = true;

    /**
     * Getters and Setters
     *
     */
    public int getFaults() {
        return faults;
    }

    public void setFaults(int faults) {
        this.faults = faults;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    /**
     *  Empty Constructor
     */

    public Report(){

    }

    public Report(int faults, Calendar date, boolean status) {
        this.faults = faults;
        this.date = date;
        this.status = status;
    }


}
