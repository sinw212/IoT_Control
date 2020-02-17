package com.example.nslngiot.Data;

public class ManagerCalendarData {
    // manager와 member 어댑터가 같은 data 공유
    private String Number;
    private String Date;
    private String Title;
    private String Detail;

    public void setNumber(String number){
        this.Number = number;
    }

    public void setDate(String date) {
        this.Date = date;
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

    public String getDate() {
        return Date;
    }

    public String getTitle(){
        return Title;
    }

    public String getDetail() {
        return Detail;
    }
}