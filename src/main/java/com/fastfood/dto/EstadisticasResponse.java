package com.fastfood.dto;

public class EstadisticasResponse {
    private int totalPedidos;
    private double totalMonto;
    private int totalRegistrados;
    private int totalDespachados;
    private int totalCancelados;

    public EstadisticasResponse(int totalPedidos, double totalMonto, int totalRegistrados, int totalDespachados, int totalCancelados) {
        this.totalPedidos = totalPedidos;
        this.totalMonto = totalMonto;
        this.totalRegistrados = totalRegistrados;
        this.totalDespachados = totalDespachados;
        this.totalCancelados = totalCancelados;
    }

    // getters
    public int getTotalPedidos() { return totalPedidos; }
    public double getTotalMonto() { return totalMonto; }
    public int getTotalRegistrados() { return totalRegistrados; }
    public int getTotalDespachados() { return totalDespachados; }
    public int getTotalCancelados() { return totalCancelados; }
}