package com.voltwise.energy_monitor.repository;

import com.voltwise.energy_monitor.model.BudgetState;
import com.voltwise.energy_monitor.model.HomeMetrics;
import com.voltwise.energy_monitor.model.TariffState;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.table.RecordView;
import org.apache.ignite.table.Tuple;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
public class HomeMetricsRepository {

    private final IgniteClient igniteClient;

    public HomeMetricsRepository(@Lazy IgniteClient igniteClient) {
        this.igniteClient = igniteClient;
    }
    public HomeMetrics getByHomeId(int homeId) {
        var table=igniteClient.tables().table("HOME_METRICS");
        if (table==null) return null;
        RecordView<Tuple> recordView = table.recordView();
        Tuple key=Tuple.create().set("HOME_ID",homeId);
        Tuple row = recordView.get(null,key);
        if (row==null) return null;
        return new HomeMetrics(
                row.intValue("HOME_ID"),
                row.intValue("CURRENT_POWER_WATTS"),
                row.doubleValue("ACCUMULATED_ENERGY_KWH"),
                row.doubleValue("ACCUMULATED_COST"),
                row.doubleValue("BUDGET_PERCENTAGE"),
                row.doubleValue("TARIFF_RATE"),
                TariffState.valueOf(row.stringValue("TARIFF_STATE")),
                BudgetState.valueOf(row.stringValue("BUDGET_STATE"))
                );
    }

    public void upsert(HomeMetrics metrics){
        var table=igniteClient.tables().table("HOME_METRICS");
        if (table==null) return;
        RecordView<Tuple> recordView = table.recordView();

        Tuple row = Tuple.create()
                .set("HOME_ID",metrics.getHomeId())
                .set("CURRENT_POWER_WATTS", metrics.getCurrentPowerWatts())
                .set("ACCUMULATED_ENERGY_KWH", metrics.getAccumulatedEnergyKWh())
                .set("ACCUMULATED_COST", metrics.getAccumulatedCost())
                .set("BUDGET_PERCENTAGE", metrics.getBudgetPercentage())
                .set("TARIFF_RATE", metrics.getTariffRate())
                .set("TARIFF_STATE", metrics.getTariffState().name())
                .set("BUDGET_STATE",metrics.getBudgetState().name());

        recordView.upsert(null,row);

    }
}
