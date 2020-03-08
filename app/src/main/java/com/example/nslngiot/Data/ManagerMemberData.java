package com.example.nslngiot.Data;

public class ManagerMemberData {
    // manager와 member 어댑터가 같은 data 공유
    private String Number;
    private String Name;
    private String Phone;
    private String Group;
    private String Course;

    public void setNumber(String number){
        this.Number = number;
    }

    public void setName(String name){
        this.Name = name;
    }

    public void setPhone(String phone){
        this.Phone = phone;
    }

    public void setGroup(String group){
        this.Group = group;
    }

    public void setCourse(String course){
        this.Course = course;
    }


    public String getNumber(){
        return Number;
    }

    public String getName(){
        return Name;
    }

    public String getPhone(){
        return Phone;
    }

    public String getGroup(){
        return Group;
    }

    public String getCourse(){
        return Course;
    }
}