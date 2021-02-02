package com.example.desk.locprovider;

public class Gps {
    private  int id;
    private  String lat;
    private  String lon;
    private  String speed;
    private  String filesize;
    private  String activty;
    private  String car;

    public String getActivty() { return activty; }

    public String getSpeed() { return speed; }

    public String getFilesize() { return filesize; }

    public int getId() {
        return id;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() { return lon; }

    public String geCar() { return car; }

    public Gps(String lat, String lon,String spped,String filesize, String car,String activty) {
        this.lat = lat;
        this.lon = lon;
        this.activty = activty;
        this.speed = spped;
        this.filesize = filesize;
        this.car = car;
    }
}
