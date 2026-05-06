package com.tox.tox.pets.model.dto;

import lombok.Data;
import java.util.List;

/**
 * 仪表盘概览数据DTO
 */
@Data
public class DashboardSummaryDTO {

    private int totalPets;
    private long totalPhotos;
    private long pendingEvents;
    private long totalWeightRecords;
    private long totalHealthEvents;
    private long totalFeedings;
    private long totalMedications;
    private long totalBathingRecords;
    private List<ActivityItem> recentActivities;
    private List<PetOverviewItem> petOverviews;
    private List<FeedingStatItem> feedingStats;

    @Data
    public static class ActivityItem {
        private String id;
        private String type;
        private String date;
        private String petName;
        private String title;
        private String icon;
    }

    @Data
    public static class PetOverviewItem {
        private Long id;
        private String name;
        private String gender;
        private String birthday;
        private String speciesName;
        private String breedName;
        private String latestWeight;
        private long pendingEventsCount;
        private String avatarUrl;
    }

    @Data
    public static class FeedingStatItem {
        private String foodType;
        private long count;
        private Integer avgAmount;
    }
}
