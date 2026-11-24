package com.stayease.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "meal_packages")
public class MealPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Long packageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "package_name", nullable = false, length = 100)
    private String packageName;

    @Column(name = "includes_breakfast", nullable = false)
    private boolean includesBreakfast;

    @Column(name = "includes_lunch", nullable = false)
    private boolean includesLunch;

    @Column(name = "includes_dinner", nullable = false)
    private boolean includesDinner;

    @Column(name = "price_per_adult_per_night",
            nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerAdultPerNight;

    @Column(name = "is_available", nullable = false)
    private boolean available;

    public MealPackage() {
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isIncludesBreakfast() {
        return includesBreakfast;
    }

    public void setIncludesBreakfast(boolean includesBreakfast) {
        this.includesBreakfast = includesBreakfast;
    }

    public boolean isIncludesLunch() {
        return includesLunch;
    }

    public void setIncludesLunch(boolean includesLunch) {
        this.includesLunch = includesLunch;
    }

    public boolean isIncludesDinner() {
        return includesDinner;
    }

    public void setIncludesDinner(boolean includesDinner) {
        this.includesDinner = includesDinner;
    }

    public BigDecimal getPricePerAdultPerNight() {
        return pricePerAdultPerNight;
    }

    public void setPricePerAdultPerNight(BigDecimal pricePerAdultPerNight) {
        this.pricePerAdultPerNight = pricePerAdultPerNight;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
