package com.voltwise.energy_monitor.model;

import jakarta.persistence.*;

@Entity
@Table(name = "appliances")
public class Appliance {
    @Id
    private int id;
    private int homeId;
    private String name;
    private double powerThreshold;

    public Appliance(int id, int homeId, String name, double powerThreshold) {
        this.id = id;
        this.homeId = homeId;
        this.name = name;
        this.powerThreshold = powerThreshold;
    }

    protected Appliance() {
    }

    public int getId() {
        return id;
    }

    public int getHomeId() {
        return homeId;
    }

    public String getName() {
        return name;
    }

    public double getPowerThreshold() {
        return powerThreshold;
    }

    public void setId(int id) {

        this.id = id;
    }

    public void setHomeId(int homeId) {
        this.homeId = homeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPowerThreshold(double powerThreshold) {
        this.powerThreshold = powerThreshold;
    }
}
