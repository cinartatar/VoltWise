package com.voltwise.energy_monitor.repository;

import com.voltwise.energy_monitor.model.ApplianceMetrics;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.table.RecordView;
import org.apache.ignite.table.Tuple;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
public class ApplianceMetricsRepository {

    private final IgniteClient igniteClient;

    public ApplianceMetricsRepository(@Lazy IgniteClient igniteClient) {
        this.igniteClient = igniteClient;
    }
    public ApplianceMetrics getByApplianceId(int applianceId) {
        var table=igniteClient.tables().table("APPLIANCE_METRICS");
        if (table==null) return null;
        RecordView<Tuple> recordView = table.recordView();
        Tuple key=Tuple.create().set("APPLIANCE_ID",applianceId);
        Tuple row = recordView.get(null,key);
        if (row==null) return null;
        return new ApplianceMetrics(
                row.intValue("APPLIANCE_ID"),
                row.intValue("CURRENT_POWER_WATTS"),
                row.doubleValue("ACCUMULATED_ENERGY_KWH"),
                row.doubleValue("ACCUMULATED_COST"),
                row.intValue("BREACH_COUNT"),
                row.booleanValue("ANOMALOUS"),
                row.value("LAST_READING_TIMESTAMP")
                );
    }

    public void upsert(ApplianceMetrics metrics){
        var table=igniteClient.tables().table("APPLIANCE_METRICS");
        if (table==null) return;
        RecordView<Tuple> recordView = table.recordView();

        Tuple row = Tuple.create()
                .set("APPLIANCE_ID",metrics.getApplianceId())
                .set("CURRENT_POWER_WATTS", metrics.getCurrentPowerWatts())
                .set("ACCUMULATED_ENERGY_KWH", metrics.getAccumulatedEnergyKWh())
                .set("ACCUMULATED_COST", metrics.getAccumulatedCost())
                .set("BREACH_COUNT",metrics.getBreachCount())
                .set("ANOMALOUS",metrics.isAnomalous())
                .set("LAST_READING_TIMESTAMP",metrics.getTimestamp());

        recordView.upsert(null,row);

    }
}
