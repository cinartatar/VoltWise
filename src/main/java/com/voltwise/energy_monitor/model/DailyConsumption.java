package com.voltwise.energy_monitor.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="daily_consumption")
public class DailyConsumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //if null then don't persist
    private int homeId;
    private LocalDate date;
    @Column(name = "total_energy_kwh")
    private double totalEnergyKWh;
    private double totalCost;
    @Column(name = "start_energy_kwh")
    private double startEnergyKWh;
    private double startCost;

    public DailyConsumption(int homeId, LocalDate date, double totalEnergyKWh, double totalCost, double startEnergyKWh, double startCost) {
        this.homeId = homeId;
        this.date = date;
        this.totalEnergyKWh = totalEnergyKWh;
        this.totalCost = totalCost;
        this.startEnergyKWh = startEnergyKWh;
        this.startCost = startCost;
    }

    public DailyConsumption() {
    }

    public int getId() {
        return id;
    }

    public int getHomeId() {
        return homeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getTotalEnergyKWh() {
        return totalEnergyKWh;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getStartEnergyKWh() {
        return startEnergyKWh;
    }

    public double getStartCost() {
        return startCost;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setHomeId(int homeId) {
        this.homeId = homeId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTotalEnergyKWh(double totalEnergyKWh) {
        this.totalEnergyKWh = totalEnergyKWh;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public void setStartEnergyKWh(double startEnergyKWh) {
        this.startEnergyKWh = startEnergyKWh;
    }

    public void setStartCost(double startCost) {
        this.startCost = startCost;
    }
}
