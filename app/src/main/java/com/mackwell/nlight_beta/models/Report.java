package com.mackwell.nlight_beta.models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * Created by weiyuan zhu on 29/08/14.
 * Report class representing panel report
 */
public class Report implements Serializable{

    /**
     * Fields and properties
     */

    private int faults;
    private Calendar date;
    private boolean faulty = true;
    private List<List<Integer>> faultyDeviceList;
    private List<List<Integer>> Loop1GroupStatus;
    private List<List<Integer>> Loop2GroupStatus;





    /**
     * Getters and Setters

     *
     */

    public List<List<Integer>> getLoop2GroupStatus() {
        return Loop2GroupStatus;
    }

    public void setLoop2GroupStatus(List<List<Integer>> loop2GroupStatus) {
        Loop2GroupStatus = loop2GroupStatus;
    }




    public List<List<Integer>> getLoop1GroupStatus() {
        return Loop1GroupStatus;
    }

    public void setLoop1GroupStatus(List<List<Integer>> loop1GroupStatus) {
        this.Loop1GroupStatus = loop1GroupStatus;
    }


    public List<List<Integer>> getFaultyDeviceList() {
        return faultyDeviceList;
    }

    public void setFaultyDeviceList(List<List<Integer>> list) {

        this.faultyDeviceList = list;
    }


    public int getFaults() {
        return faults;
    }

    public void setFaults(int faults) {
        this.faults = faults;
    }

    public boolean isFaulty() {
        return faulty;
    }

    public void setFaulty(boolean faulty) {
        this.faulty = faulty;
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

    public Report(int faults, Calendar date, boolean faulty) {
        this.faults = faults;
        this.date = date;
        this.faulty = faulty;
    }


}
