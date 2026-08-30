package com.voltwise.energy_monitor.repository;

import com.voltwise.energy_monitor.model.DailyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface DailyConsumptionRepository extends JpaRepository<DailyConsumption,Integer>{
    @Query("SELECT d FROM DailyConsumption d WHERE d.homeId= :homeId")
    List<DailyConsumption> findByHomeId(@Param("homeId") int homeId);

    Optional<DailyConsumption> findByHomeIdAndDate(@Param("homeId") int homeId, LocalDate date);

}
