package com.example.trip2.ui.list;

public class Contacts {

    public Contacts(){}
    public Contacts(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String name, status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
