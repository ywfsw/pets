package com.tox.tox.pets.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class FeedingStatsDTO {
    private List<DailyStat> dailyStats;
    private long totalRecords;
    private Integer avgDailyAmount;
    private String periodDays;

    @Data
    public static class DailyStat {
        private String date;
        private int count;
        private Integer totalAmount;
    }
}
