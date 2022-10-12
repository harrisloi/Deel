package com.example.harrisloi.deel.Model;

public class ShippingInformation {
    private String orderId, shipperName;
    private Double lat, lng;

    public ShippingInformation() {
    }

    public ShippingInformation(String orderId, String shipperName, Double lat, Double lng) {
        this.orderId = orderId;
        this.shipperName = shipperName;
        this.lat = lat;
        this.lng = lng;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
