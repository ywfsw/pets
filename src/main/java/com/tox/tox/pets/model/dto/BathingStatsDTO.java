package com.tox.tox.pets.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class BathingStatsDTO {
    private long totalRecords;
    private int uniqueTypes;
    private String topType;
    private List<TypeStat> typeStats;

    @Data
    public static class TypeStat {
        private String serviceType;
        private int count;
    }
}
