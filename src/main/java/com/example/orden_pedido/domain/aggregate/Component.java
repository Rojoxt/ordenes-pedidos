package com.example.orden_pedido.domain.aggregate;

public class Component {
    private final String component;
    private final Double requiredQuantity;
    private String unitOfMeasure;
    private final Double conversionFactor;
    private final Integer validityStartDate; // Formato AAAAMMDD
    private final Integer validityEndDate;   // Formato AAAAMMDD
    private final Double wasteFactor;

    public Component(String component, Double requiredQuantity, String unitOfMeasure, Double conversionFactor, Integer validityStartDate, Integer validityEndDate, Double wasteFactor) {
        this.component = component;
        this.requiredQuantity = requiredQuantity;
        this.unitOfMeasure = unitOfMeasure;
        this.conversionFactor = conversionFactor;
        this.validityStartDate = validityStartDate;
        this.validityEndDate = validityEndDate;
        this.wasteFactor = wasteFactor;
    }

    public String getComponent() {
        return component;
    }

    public Double getRequiredQuantity() {
        return requiredQuantity;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Double getConversionFactor() {
        return conversionFactor;
    }

    public Integer getValidityStartDate() {
        return validityStartDate;
    }

    public Integer getValidityEndDate() {
        return validityEndDate;
    }

    public Double getWasteFactor() {
        return wasteFactor;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
}
