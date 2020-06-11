package com.example.trip2;

public class Feed {
    private String name;
    private String time;
    private String user_image;
    private String feed_image;
    private String desc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getFeed_image() {
        return feed_image;
    }

    public void setFeed_image(String pid_image) {
        this.feed_image = pid_image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }



    public Feed(){}
    public Feed(String name, String time, String user_image, String feed_image, String desc){
        this.name=name;
        this.time=time;
        this.user_image=user_image;
        this.feed_image=feed_image;
        this.desc=desc;
    }
}
