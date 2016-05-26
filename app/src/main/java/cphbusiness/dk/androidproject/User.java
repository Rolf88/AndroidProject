package cphbusiness.dk.androidproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by kalkun on 22-05-2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
    private String name;
    private String email;
    private String password;
    private double latitude;
    private double longitude;

    public User() {

    }

    public User(String name, String email, String password, double latitude, double longitude) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

