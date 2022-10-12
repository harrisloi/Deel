package com.example.harrisloi.deel.Model;

public class Food {
    private String name;
    private String description;
    private String price;
    private String image;
    private String owner;

    public Food() {
    }

    public Food(String name, String description, String price, String image, String owner) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
