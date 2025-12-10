package com.fastfood.service;

import com.fastfood.datastructures.*;
import com.fastfood.dto.EstadisticasResponse;
import com.fastfood.model.Pedido;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class PedidoService {
    private final SinglyLinkedList lista = new SinglyLinkedList();
    private final QueuePedidos cola = new QueuePedidos();
    private final StackHistorial historial = new StackHistorial();
    private final AtomicInteger idGen = new AtomicInteger(1);

    public Pedido crearPedido(String nombreCliente, String descripcion, double monto) {
        int id = idGen.getAndIncrement();
        Pedido p = new Pedido(id, nombreCliente, descripcion, monto, "REGISTRADO");
        lista.add(p);
        cola.enqueue(p);
        historial.push(new HistorialOperacion("CREAR", null, p));
        return p.clone();
    }

    public List<Pedido> listarPedidos() {
        return lista.toList();
    }

    public Pedido obtenerPorId(int id) {
        Pedido p = lista.findById(id);
        return p != null ? p.clone() : null;
    }

    public boolean cancelarPedido(int id) {
        Pedido p = lista.findById(id);
        if (p == null) return false;
        Pedido antes = p.clone();
        p.setEstado("CANCELADO");
        // intentar quitar de la cola
        cola.removeById(id);
        historial.push(new HistorialOperacion("CANCELAR", antes, p));
        return true;
    }

    public Pedido despacharSiguiente() {
        if (cola.isEmpty()) return null;
        Pedido p = cola.dequeue();
        if (p == null) return null;
        Pedido antes = p.clone();
        p.setEstado("DESPACHADO");
        historial.push(new HistorialOperacion("DESPACHAR", antes, p));
        return p.clone();
    }

    public EstadisticasResponse estadisticas() {
        List<Pedido> all = lista.toList();
        int totalPedidos = all.size();
        double totalMonto = lista.totalRecursivo();
        int reg = 0, desp = 0, canc = 0;
        for (Pedido p : all) {
            switch (p.getEstado()) {
                case "REGISTRADO" -> reg++;
                case "DESPACHADO" -> desp++;
                case "CANCELADO" -> canc++;
            }
        }
        return new EstadisticasResponse(totalPedidos, totalMonto, reg, desp, canc);
    }

    public double totalRecursivo() {
        return lista.totalRecursivo();
    }

    public HistorialOperacion realizarRollback() {
        HistorialOperacion op = historial.pop();
        if (op == null) return null;
        String tipo = op.getTipoOperacion();
        if ("CREAR".equals(tipo)) {
            // eliminar el pedido creado (si existe) de la lista y de la cola
            Pedido creado = op.getPedidoDespues();
            if (creado != null) {
                lista.removeById(creado.getId());
                cola.removeById(creado.getId());
            }
        } else if ("CANCELAR".equals(tipo)) {
            Pedido antes = op.getPedidoAntes();
            if (antes != null) {
                Pedido actual = lista.findById(antes.getId());
                if (actual != null) actual.setEstado(antes.getEstado());
                // si antes era REGISTRADO, asegurarse de que esté en la cola
                if ("REGISTRADO".equals(antes.getEstado())) {
                    // evitar duplicados simples: encolamos de nuevo
                    cola.enqueue(actual);
                }
            }
        } else if ("DESPACHAR".equals(tipo)) {
            Pedido antes = op.getPedidoAntes();
            if (antes != null) {
                Pedido actual = lista.findById(antes.getId());
                if (actual != null) actual.setEstado(antes.getEstado());
                // si el estado anterior era REGISTRADO/EN_PREPARACION, reencolar
                if ("REGISTRADO".equals(antes.getEstado()) || "EN_PREPARACION".equals(antes.getEstado())) {
                    cola.enqueue(actual);
                }
            }
        }
        return op;
    }
}