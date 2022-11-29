package com.example.group22_hw10;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.UUID;

public class Trip implements Serializable {

    private String trip_id = UUID.randomUUID().toString();
    private String user_id;
    private String trip_name;
    private Timestamp created_at;
    private Timestamp completed_at;
    private double total_miles;
    private String status;

    public Trip() {}

    public Trip(String user_id, String trip_name, Timestamp created_at) {
        this.user_id = user_id;
        this.trip_name = trip_name;
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
        return status;
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

    @Override
    public String toString() {
        return "Trip{" +
                "trip_id='" + trip_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", trip_name='" + trip_name + '\'' +
                ", created_at=" + created_at +
                ", completed_at=" + completed_at +
                ", total_miles=" + total_miles +
                ", status='" + status + '\'' +
                '}';
    }
}
