package com.voltwise.energy_monitor.model;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.Instant;

public class ApplianceMetrics {
    private int applianceId;
    private int currentPowerWatts;
    private double accumulatedEnergyKWh;
    private double accumulatedCost;
    private int breachCount;
    private boolean anomalous;
    private Instant timestamp;

    public ApplianceMetrics(int applianceId, int currentPowerWatts, double accumulatedEnergyKWh, double accumulatedCost, int breachCount, boolean anomalous, Instant timestamp) {
        this.applianceId = applianceId;
        this.currentPowerWatts = currentPowerWatts;
        this.accumulatedEnergyKWh = accumulatedEnergyKWh;
        this.accumulatedCost = accumulatedCost;
        this.breachCount = breachCount;
        this.anomalous = anomalous;
        this.timestamp = timestamp;
    }

    public int getApplianceId() {
        return applianceId;
    }

    public int getCurrentPowerWatts() {
        return currentPowerWatts;
    }

    public double getAccumulatedEnergyKWh() {
        return accumulatedEnergyKWh;
    }

    public double getAccumulatedCost() {
        return accumulatedCost;
    }

    public int getBreachCount() {
        return breachCount;
    }

    public boolean isAnomalous() {
        return anomalous;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setApplianceId(int applianceId) {
        this.applianceId = applianceId;
    }

    public void setCurrentPowerWatts(int currentPowerWatts) {
        this.currentPowerWatts = currentPowerWatts;
    }

    public void setAccumulatedEnergyKWh(double accumulatedEnergyKWh) {
        this.accumulatedEnergyKWh = accumulatedEnergyKWh;
    }

    public void setAccumulatedCost(double accumulatedCost) {
        this.accumulatedCost = accumulatedCost;
    }

    public void setBreachCount(int breachCount) {
        this.breachCount = breachCount;
    }

    public void setAnomalous(boolean anomalous) {
        this.anomalous = anomalous;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
