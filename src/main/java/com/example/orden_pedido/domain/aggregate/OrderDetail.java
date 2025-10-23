package com.example.orden_pedido.domain.aggregate;

import java.math.BigDecimal;

public class OrderDetail {
    private final Integer lineNumber;
    private final String factoryOrder;
    private final String orderClass;
    private final String material;
    private final BigDecimal requestedQuantity;
    private final BigDecimal price;
    private final Integer basePrice;
    private final BigDecimal uniPrice;
    private final String currency;
    private String unitOfMeasure;
    private final Integer expirationDate; // formato AAAAMMDD
    private final Integer releaseDate;    // formato AAAAMMDD
    private final Integer deliveryDatePostOrder;    // formato AAAAMMDD
    private final String batchNumber;


    public OrderDetail(Integer lineNumber, String factoryOrder, String orderClass, String material, BigDecimal requestedQuantity, BigDecimal price, Integer basePrice, BigDecimal uniPrice, String currency, Integer expirationDate, Integer releaseDate, String batchNumber, String unitOfMeasure, Integer deliveryDatePostOrder) {
        this.lineNumber = lineNumber;
        this.factoryOrder = factoryOrder;
        this.orderClass = orderClass;
        this.material = material;
        this.requestedQuantity = requestedQuantity;
        this.price = price;
        this.basePrice = basePrice;
        this.uniPrice = uniPrice;
        this.currency = currency;
        this.expirationDate = expirationDate;
        this.releaseDate = releaseDate;
        this.batchNumber = batchNumber;
        this.unitOfMeasure = unitOfMeasure;
        this.deliveryDatePostOrder = deliveryDatePostOrder;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public String getFactoryOrder() {
        return factoryOrder;
    }

    public String getMaterial() {
        return material;
    }

    public BigDecimal getRequestedQuantity() {
        return requestedQuantity;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Integer getExpirationDate() {
        return expirationDate;
    }

    public Integer getReleaseDate() {
        return releaseDate;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getBasePrice() {
        return basePrice;
    }

    public BigDecimal getUnitPrice() {
        return uniPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public String getOrderClass() {
        return orderClass;
    }

    public Integer getDeliveryDatePostOrder() {
        return deliveryDatePostOrder;
    }
}
