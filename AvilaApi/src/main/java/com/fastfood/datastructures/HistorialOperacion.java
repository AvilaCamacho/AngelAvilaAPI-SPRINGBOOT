package com.fastfood.datastructures;

import com.fastfood.model.Pedido;

public class HistorialOperacion {
    private String tipoOperacion; // CREAR, CANCELAR, DESPACHAR
    private Pedido pedidoAntes; // puede ser null para CREAR
    private Pedido pedidoDespues;

    public HistorialOperacion(String tipoOperacion, Pedido pedidoAntes, Pedido pedidoDespues) {
        this.tipoOperacion = tipoOperacion;
        this.pedidoAntes = pedidoAntes != null ? pedidoAntes.clone() : null;
        this.pedidoDespues = pedidoDespues != null ? pedidoDespues.clone() : null;
    }

    public String getTipoOperacion() { return tipoOperacion; }
    public Pedido getPedidoAntes() { return pedidoAntes; }
    public Pedido getPedidoDespues() { return pedidoDespues; }
}