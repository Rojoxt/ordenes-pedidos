package com.example.orden_pedido.infrastructure.interceptor;

public class MonitoringContextHolder {

    private static final ThreadLocal<MonitoringContext> holder = new ThreadLocal<>();

    public static void set(MonitoringContext context) {
        holder.set(context);
    }

    public static MonitoringContext get() {
        return holder.get();
    }

    public static void clear() {
        holder.remove();
    }
}
