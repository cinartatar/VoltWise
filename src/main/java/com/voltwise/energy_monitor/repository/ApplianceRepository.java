package com.voltwise.energy_monitor.repository;

import com.voltwise.energy_monitor.model.Appliance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplianceRepository extends JpaRepository<Appliance,Integer> {
    @Query("SELECT d FROM Appliance d WHERE d.homeId= :homeId")
    List<Appliance> findByHomeId(@Param("homeId") int homeId);
}
