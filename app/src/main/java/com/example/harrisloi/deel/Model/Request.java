package com.example.harrisloi.deel.Model;

import java.util.List;

public class Request {
    private String phone;
    private String address;
    private String total;
    private String username;
    private String fullName;
    private String status;
    private String latLang;
    private List<Order> foods; // list of foods
    private String owner;

    public Request() {
    }

    public Request(String phone, String address, String total, String username, String fullName, String status, String latLang, List<Order> foods, String owner) {
        this.phone = phone;
        this.address = address;
        this.total = total;
        this.username = username;
        this.fullName = fullName;
        this.status = status;
        this.latLang = latLang;
        this.foods = foods;
        this.owner = owner;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLatLang() {
        return latLang;
    }

    public void setLatLang(String latLang) {
        this.latLang = latLang;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
