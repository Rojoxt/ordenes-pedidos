package com.example.orden_pedido.domain.model.order;

import java.util.List;

public class Orders {

    private List<SalesOrder> salesOrders;
    private List<FactoryOrder> factoryOrders;

    public Orders() {}

    public Orders(List<SalesOrder> salesOrders, List<FactoryOrder> factoryOrders) {
        this.salesOrders = salesOrders;
        this.factoryOrders = factoryOrders;
    }

    public List<SalesOrder> getSalesOrders() {
        return salesOrders;
    }

    public void setSalesOrders(List<SalesOrder> salesOrders) {
        this.salesOrders = salesOrders;
    }

    public List<FactoryOrder> getFactoryOrders() {
        return factoryOrders;
    }

    public void setFactoryOrders(List<FactoryOrder> factoryOrders) {
        this.factoryOrders = factoryOrders;
    }

    // ========================
    // Subclases
    // ========================
    public static class SalesOrder {
        private String status;
        private String message;
        private String purchaseOrder;
        private String yobPurchaseOrder;

        public SalesOrder() {}

        public SalesOrder(String status, String message, String purchaseOrder, String yobPurchaseOrder) {
            this.status = status;
            this.message = message;
            this.purchaseOrder = purchaseOrder;
            this.yobPurchaseOrder = yobPurchaseOrder;
        }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getPurchaseOrder() { return purchaseOrder; }
        public void setPurchaseOrder(String purchaseOrder) { this.purchaseOrder = purchaseOrder; }

        public String getYobPurchaseOrder() { return yobPurchaseOrder; }
        public void setYobPurchaseOrder(String yobPurchaseOrder) { this.yobPurchaseOrder = yobPurchaseOrder; }
    }

    public static class FactoryOrder {
        private String status;
        private String message;
        private String factoryOrder;
        private String yobFactoryOrder;

        public FactoryOrder() {}

        public FactoryOrder(String status, String message, String factoryOrder, String yobFactoryOrder) {
            this.status = status;
            this.message = message;
            this.factoryOrder = factoryOrder;
            this.yobFactoryOrder = yobFactoryOrder;
        }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getFactoryOrder() { return factoryOrder; }
        public void setFactoryOrder(String factoryOrder) { this.factoryOrder = factoryOrder; }

        public String getYobFactoryOrder() { return yobFactoryOrder; }
        public void setYobFactoryOrder(String yobFactoryOrder) { this.yobFactoryOrder = yobFactoryOrder; }
    }

    public void updateSalesOrder(String purchaseOrder, String status, String message, String yob) {
        this.salesOrders.stream()
                .filter(o -> o.getPurchaseOrder().equals(purchaseOrder))
                .forEach(o -> {
                    o.setStatus(status);
                    o.setMessage(message);
                    o.setYobPurchaseOrder(yob);
                });
    }

    public void updateFactoryOrder(String factoryOrder, String status, String message, String yob) {
        this.factoryOrders.stream()
                .filter(o -> o.getFactoryOrder().equals(factoryOrder))
                .forEach(o -> {
                    o.setStatus(status);
                    o.setMessage(message);
                    o.setYobFactoryOrder(yob);
                });
    }
}
