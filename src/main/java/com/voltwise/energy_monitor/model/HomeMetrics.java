package com.voltwise.energy_monitor.model;


public class HomeMetrics {
    private int homeId;
    private int currentPowerWatts;
    private double accumulatedEnergyKWh;
    private double accumulatedCost;
    private double budgetPercentage;
    private double tariffRate;
    private TariffState tariffState;
    private BudgetState budgetState;

    public HomeMetrics(int homeId, int currentPowerWatts, double accumulatedEnergyKWh, double accumulatedCost, double budgetPercentage, double tariffRate, TariffState tariffState, BudgetState budgetState) {
        this.homeId = homeId;
        this.currentPowerWatts = currentPowerWatts;
        this.accumulatedEnergyKWh = accumulatedEnergyKWh;
        this.accumulatedCost = accumulatedCost;
        this.budgetPercentage = budgetPercentage;
        this.tariffRate = tariffRate;
        this.tariffState = tariffState;
        this.budgetState = budgetState;
    }

    public int getHomeId() {
        return homeId;
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

    public double getBudgetPercentage() {
        return budgetPercentage;
    }

    public double getTariffRate() {
        return tariffRate;
    }

    public TariffState getTariffState() {
        return tariffState;
    }

    public BudgetState getBudgetState() {
        return budgetState;
    }

    public void setHomeId(int homeId) {
        this.homeId = homeId;
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

    public void setBudgetPercentage(double budgetPercentage) {
        this.budgetPercentage = budgetPercentage;
    }

    public void setTariffRate(double tariffRate) {
        this.tariffRate = tariffRate;
    }

    public void setTariffState(TariffState tariffState) {
        this.tariffState = tariffState;
    }

    public void setBudgetState(BudgetState budgetState) {
        this.budgetState = budgetState;
    }
}
