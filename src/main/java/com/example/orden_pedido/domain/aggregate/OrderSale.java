package com.example.orden_pedido.domain.aggregate;


import java.util.List;

public class OrderSale {
    private final String organizationCode;
    private final String purchaseOrder;
    private final Integer requestDate; // AAAAMMDD
    private final Integer totalLines;
    private String codeClient;
    private String warehouse;
    private final List<OrderDetail> orderDetailList;


    public OrderSale(String organizationCode, String purchaseOrder, Integer requestDate, Integer totalLines, String codeClient, String warehouse, List<OrderDetail> orderDetailList) {
        this.organizationCode = organizationCode;
        this.purchaseOrder = purchaseOrder;
        this.requestDate = requestDate;
        this.totalLines = totalLines;
        this.codeClient = codeClient;
        this.warehouse = warehouse;
        this.orderDetailList = orderDetailList;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public Integer getRequestDate() {
        return requestDate;
    }

    public Integer getTotalLines() {
        return totalLines;
    }

    public List<OrderDetail> getOrderDetailList() {
        return orderDetailList;
    }

    public String getCodeClient() {
        return codeClient;
    }

    public String getWarehouse() {
        return warehouse;
    }


    public void updateVariables(String codeClient, String warehouse) {
        this.codeClient = codeClient;
        this.warehouse = warehouse;
    }
}
