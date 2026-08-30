package com.voltwise.energy_monitor.repository;


import com.voltwise.energy_monitor.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<Recommendation,Integer> {


}
