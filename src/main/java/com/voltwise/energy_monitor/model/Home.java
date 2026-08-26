package com.voltwise.energy_monitor.model;

import jakarta.persistence.*;

@Entity
@Table(name="homes")
public class Home {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String contactEmail;
    private double monthlyBudgetLimit;


    public Home(String contactEmail, double monthlyBudgetLimit) {
        this.contactEmail = contactEmail;
        this.monthlyBudgetLimit = monthlyBudgetLimit;

    }

    protected Home() {
    }

    public int getId() {
        return id;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public double getMonthlyBudgetLimit() {
        return monthlyBudgetLimit;
    }



    public void setId(Integer id) {
        this.id = id;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setMonthlyBudgetLimit(double monthlyBudgetLimit) {
        this.monthlyBudgetLimit = monthlyBudgetLimit;
    }


}
