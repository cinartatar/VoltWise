package com.voltwise.energy_monitor.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="recommendations")
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Integer homeId;
    private Integer applianceId;
    private String notificationType;
    @Column (columnDefinition = "TEXT")
    private String recommendationText;
    private LocalDateTime createdAt;

    public Recommendation(Integer homeId, Integer applianceId, String notificationType, String recommendationText) {
        this.homeId = homeId;
        this.applianceId = applianceId;
        this.notificationType = notificationType;
        this.recommendationText = recommendationText;
        this.createdAt = LocalDateTime.now();
    }

    protected Recommendation() {
    }

    public int getId() {
        return id;
    }

    public Integer getHomeId() {
        return homeId;
    }

    public Integer getApplianceId() {
        return applianceId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getRecommendationText() {
        return recommendationText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
