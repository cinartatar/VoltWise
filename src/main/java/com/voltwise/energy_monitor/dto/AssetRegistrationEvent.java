package com.voltwise.energy_monitor.dto;

import com.voltwise.energy_monitor.model.Appliance;
import com.voltwise.energy_monitor.model.Home;

import java.util.List;

public class AssetRegistrationEvent {

    private Home home;
    private List<Appliance> appliances;

    public AssetRegistrationEvent(Home home, List<Appliance> appliances) {
        this.home = home;
        this.appliances = appliances;
    }

    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
    }

    public List<Appliance> getAppliances() {
        return appliances;
    }

    public void setAppliances(List<Appliance> appliances) {
        this.appliances = appliances;
    }
}
