package com.tox.tox.pets.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class HealthEventsStatsDTO {
    private long totalCount;
    private long pendingCount;
    private long completedCount;
    private long overdueCount;
    private List<TypeBreakdown> typeBreakdown;

    @Data
    public static class TypeBreakdown {
        private Long eventTypeId;
        private String eventTypeName;
        private long count;
    }
}
