package com.voltwise.energy_monitor.dto;

import com.voltwise.energy_monitor.model.Appliance;
import com.voltwise.energy_monitor.model.Home;

import java.util.List;

public class HomeRegistrationRequest {

    private Home home;
    private List<Appliance> appliances;

    public HomeRegistrationRequest() {
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
