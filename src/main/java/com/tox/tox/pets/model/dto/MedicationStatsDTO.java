package com.tox.tox.pets.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class MedicationStatsDTO {
    private long totalRecords;
    private long activeCount;
    private long completedCount;
    private String topType;
    private List<TypeStat> typeStats;

    @Data
    public static class TypeStat {
        private String medicationType;
        private int count;
    }
}
