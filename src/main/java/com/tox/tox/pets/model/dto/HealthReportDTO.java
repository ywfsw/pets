package com.tox.tox.pets.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class HealthReportDTO {

    private Long petId;
    private String petName;
    private String speciesLabel;
    private String breedLabel;
    private int months;
    private List<MonthlyData> monthlyData;

    @Getter
    @Setter
    public static class MonthlyData {
        private String month;
        private BigDecimal weightFirst;
        private BigDecimal weightLast;
        private BigDecimal weightMin;
        private BigDecimal weightMax;
        private int weightRecords;
        private int healthEventsTotal;
        private int healthEventsCompleted;
        private int feedingRecords;
        private int bathingRecords;
        private int medicationRecords;
    }
}
