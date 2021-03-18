package com.madhu.deltachat.ModelDataClasses;

public class ChatUsers {

    private String name, image, status, Uid;


    private String thumbNail;

    public ChatUsers() {
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public ChatUsers(String name, String image, String status, String Uid, String thumbNailImage) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.Uid = Uid;
        this.thumbNail = thumbNailImage;

    }

    public String getThumbNail() {
        return thumbNail;
    }

    public void setThumbNail(String thumbNail) {
        this.thumbNail = thumbNail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
