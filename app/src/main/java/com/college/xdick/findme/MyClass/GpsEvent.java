package com.college.xdick.findme.MyClass;

public class GpsEvent {
    private String[] message;

    public   GpsEvent(String[] message){
        this.message=message;
    }

    public String[] getMessage() {
        return message;
    }

    public void setMessage(String[] message) {
        this.message = message;
    }


}
