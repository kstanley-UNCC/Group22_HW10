// Homework Assignment 10
// Group22_HW10
// Ken Stanley & Stephanie Karp

package com.example.group22_hw10;


import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.UUID;

public class Trip implements Serializable {
    private String trip_id;
    private String user_id;
    private String trip_name;
    private Timestamp created_at;
    private Timestamp completed_at;
    private double start_latitude;
    private double start_longitude;
    private double end_latitude = 0;
    private double end_longitude = 0;
    private double total_miles;
    private String status;

    public Trip() {
    }

    public Trip(String trip_id, String user_id, String trip_name, Timestamp created_at, Timestamp completed_at, double start_latitude, double start_longitude, double end_latitude, double end_longitude, double total_miles, String status) {
        this.trip_id = trip_id;
        this.user_id = user_id;
        this.trip_name = trip_name;
        this.created_at = created_at;
        this.completed_at = completed_at;
        this.start_latitude = start_latitude;
        this.start_longitude = start_longitude;
        this.end_latitude = end_latitude;
        this.end_longitude = end_longitude;
        this.total_miles = total_miles;
        this.status = status;
    }

    public Trip(String user_id, String trip_name, double start_latitude, double start_longitude) {
        this.trip_id = UUID.randomUUID().toString();
        this.user_id = user_id;
        this.trip_name = trip_name;
        this.start_latitude = start_latitude;
        this.start_longitude = start_longitude;
        this.created_at = Timestamp.now();
    }

    public String getTrip_id() {
        return trip_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getTrip_name() {
        return trip_name;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public Timestamp getCompleted_at() {
        return completed_at;
    }

    public double getTotal_miles() {
        return total_miles;
    }

    public String getStatus() {
        if (end_latitude != 0 && end_longitude !=0) {
            status = "Completed";
        } else {
            status = "On Going";
        }
        return status;
    }

    public double getStart_latitude() {
        return start_latitude;
    }

    public double getStart_longitude() {
        return start_longitude;
    }

    public double getEnd_latitude() {
        return end_latitude;
    }

    public double getEnd_longitude() {
        return end_longitude;
    }

    public Trip setStart_latitude(double start_latitude) {
        this.start_latitude = start_latitude;
        return this;
    }

    public Trip setStart_longitude(double start_longitude) {
        this.start_longitude = start_longitude;
        return this;
    }

    public Trip setEnd_latitude(double end_latitude) {
        this.end_latitude = end_latitude;
        return this;
    }

    public Trip setEnd_longitude(double end_longitude) {
        this.end_longitude = end_longitude;
        return this;
    }

    public Trip setTrip_id(String trip_id) {
        this.trip_id = trip_id;
        return this;
    }

    public Trip setUser_id(String user_id) {
        this.user_id = user_id;
        return this;
    }

    public Trip setTrip_name(String trip_name) {
        this.trip_name = trip_name;
        return this;
    }

    public Trip setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
        return this;
    }

    public Trip setCompleted_at(Timestamp completed_at) {
        this.completed_at = completed_at;
        return this;
    }

    public Trip setTotal_miles(double total_miles) {
        this.total_miles = total_miles;
        return this;
    }

    public Trip setStatus(String status) {
        this.status = status;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "Trip{" +
                "trip_id='" + trip_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", trip_name='" + trip_name + '\'' +
                ", created_at=" + created_at +
                ", completed_at=" + completed_at +
                ", start_latitude=" + start_latitude +
                ", start_longitude=" + start_longitude +
                ", end_latitude=" + end_latitude +
                ", end_longitutde=" + end_longitude +
                ", total_miles=" + total_miles +
                ", status='" + status + '\'' +
                '}';
    }
}
