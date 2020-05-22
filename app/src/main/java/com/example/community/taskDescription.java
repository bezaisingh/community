package com.example.community;

public class taskDescription {


    private String UID,Task,Date,Time,Address,Landmark,WorkValue, PostKey;

    public taskDescription() {}

    public taskDescription(String ID, String task, String date, String time, String address, String landmark, String workValue, String postKey) {
        this.UID = ID;
        Task = task;
        Date = date;
        Time = time;
        Address = address;
        Landmark = landmark;
        WorkValue = workValue;
        PostKey= postKey;

    }

    public String getID() {
        return UID;
    }

    public void setID(String ID) {
        this.UID = ID;
    }

    public String getTask() {
        return Task;
    }

    public void setTask(String task) {
        Task = task;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getLandmark() {
        return Landmark;
    }

    public void setLandmark(String landmark) {
        Landmark = landmark;
    }

    public String getWorkValue() {
        return WorkValue;
    }

    public void setWorkValue(String workValue) {
        WorkValue = workValue;
    }

    public String getPostKey() {
        return PostKey;
    }

    public void setPostKey(String postKey) {
        PostKey = postKey;
    }
}
