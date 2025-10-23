package com.example.orden_pedido.domain.aggregate;

import java.util.List;

public class Material {
    private final String parentCode;
    private final Integer orderLine;
    private final Integer manufacturingBaseQuantity;
    private String baseQuantityUnit;
    private final List<Component> components;

    public Material(String parentCode, Integer orderLine, Integer manufacturingBaseQuantity, String baseQuantityUnit, List<Component> components) {
        this.parentCode = parentCode;
        this.orderLine = orderLine;
        this.manufacturingBaseQuantity = manufacturingBaseQuantity;
        this.baseQuantityUnit = baseQuantityUnit;
        this.components = components;
    }

    public String getParentCode() {
        return parentCode;
    }

    public Integer getManufacturingBaseQuantity() {
        return manufacturingBaseQuantity;
    }

    public String getBaseQuantityUnit() {
        return baseQuantityUnit;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setBaseQuantityUnit(String baseQuantityUnit) {
        this.baseQuantityUnit = baseQuantityUnit;
    }

    public Integer getOrderLine() {
        return orderLine;
    }
}
