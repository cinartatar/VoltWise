package com.voltwise.energy_monitor.reader;

import java.time.Instant;

public class ApplianceReading {
    private int homeId;
    private int applianceId;
    private int powerWatts;
    private Instant timestamp; //for date and clock

    public ApplianceReading(int homeId, int applianceId, int powerWatts, Instant timestamp) {
        this.homeId = homeId;
        this.applianceId = applianceId;
        this.powerWatts = powerWatts;
        this.timestamp = timestamp;
    }

    public ApplianceReading() {
    }

    public int getHomeId() {
        return homeId;
    }

    public int getApplianceId() {
        return applianceId;
    }

    public int getPowerWatts() {
        return powerWatts;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ApplianceReading{" +
                "homeId=" + homeId +
                ", applianceId=" + applianceId +
                ", powerWatts=" + powerWatts +
                ", timestamp=" + timestamp +
                '}';
    }
    //calculate elapsed energy
    //add energy to Homemetrics
    //add cost
    //update currentPowerWatts
    //calculate budgetPercentage
    //upsert Homemetrics
}
