package com.example.trip2;

public class Contacts {
    private String name;
    private String status;

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

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    private String user_image;

    public Contacts(){}
    public Contacts(String name, String status, String user_image) {
        this.name = name;
        this.status = status;
        this.user_image = user_image;
    }


}
