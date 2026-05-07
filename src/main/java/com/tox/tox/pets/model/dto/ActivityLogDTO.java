package com.tox.tox.pets.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class ActivityLogDTO {

    private List<ActivityLogItem> activities;
    private long total;
    private ActivityStats stats;

    @Data
    public static class ActivityLogItem {
        private String id;
        private String type;
        private Long petId;
        private String petName;
        private String title;
        private String detail;
        private String date;
        private String icon;
        private String color;
    }

    @Data
    public static class ActivityStats {
        private long totalActivities;
        private long weightCount;
        private long healthCount;
        private long feedingCount;
        private long photoCount;
        private long medicationCount;
        private long bathingCount;
    }
}
