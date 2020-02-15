package com.example.nslngiot.Data;

public class ManagerCalendarData {
    private String Number;
    private String Title;
    private String Detail;

    public void setNumber(String number){
        this.Number = number;
    }

    public void setTitle(String title){
        this.Title = title;
    }

    public void setDetail(String detail) {
        this.Detail = detail;
    }

    public String getNumber(){
        return Number;
    }

    public String getTitle(){
        return Title;
    }

    public String getDetail() {
        return Detail;
    }
}