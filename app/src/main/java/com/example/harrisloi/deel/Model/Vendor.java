package com.example.harrisloi.deel.Model;

public class Vendor {
    private String name;
    private String image;
    private String type;

    public Vendor() {
    }

    public Vendor(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public Vendor(String name, String image, String type) {
        this.name = name;
        this.image = image;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
