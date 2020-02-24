package com.example.nslngiot.Data;

public class ManagerCalendarData {
    // manager와 member 어댑터가 같은 data 공유
    private String Number;
    private String Title;

    public void setNumber(String number){
        this.Number = number;
    }
    public void setTitle(String title){
        this.Title = title;
    }
    public String getNumber(){
        return Number;
    }
    public String getTitle(){
        return Title;
    }
}