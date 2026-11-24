package com.stayease.backend.dto;

import java.math.BigDecimal;

public class MealPackageResponse {

    private Long packageId;
    private String packageName;

    private boolean includesBreakfast;
    private boolean includesLunch;
    private boolean includesDinner;

    private BigDecimal pricePerAdultPerNight;

    public MealPackageResponse(
            Long packageId,
            String packageName,
            boolean includesBreakfast,
            boolean includesLunch,
            boolean includesDinner,
            BigDecimal pricePerAdultPerNight) {

        this.packageId = packageId;
        this.packageName = packageName;
        this.includesBreakfast = includesBreakfast;
        this.includesLunch = includesLunch;
        this.includesDinner = includesDinner;
        this.pricePerAdultPerNight = pricePerAdultPerNight;
    }

    public Long getPackageId() {
        return packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isIncludesBreakfast() {
        return includesBreakfast;
    }

    public boolean isIncludesLunch() {
        return includesLunch;
    }

    public boolean isIncludesDinner() {
        return includesDinner;
    }

    public BigDecimal getPricePerAdultPerNight() {
        return pricePerAdultPerNight;
    }
}
