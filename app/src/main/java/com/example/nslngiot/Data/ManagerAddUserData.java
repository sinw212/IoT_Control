package com.example.nslngiot.Data;


public class ManagerAddUserData {

    private String Number;
    private String Name;
    private String ID;

    public void setNumber(String number){
        this.Number = number;
    }

    public void setName(String name){
        this.Name = name;
    }

    public void setID(String id){
        this.ID = id;
    }

    public String getNumber(){
        return Number;
    }

    public String getName(){
        return Name;
    }

    public String getID(){
        return ID;
    }
}