package com.voltwise.energy_monitor.repository;

import com.voltwise.energy_monitor.model.DailyConsumption;
import com.voltwise.energy_monitor.model.Home;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeRepository extends JpaRepository<Home,Integer> {


}
