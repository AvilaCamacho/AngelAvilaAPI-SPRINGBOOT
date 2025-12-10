package com.fastfood.controller;

import com.fastfood.dto.*;
import com.fastfood.datastructures.HistorialOperacion;
import com.fastfood.model.Pedido;
import com.fastfood.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> crearPedido(@Valid @RequestBody PedidoRequest req) {
        if (req.getMonto() == null || req.getMonto() <= 0) {
            return ResponseEntity.badRequest().body("monto debe ser mayor a 0");
        }
        Pedido p = service.crearPedido(req.getNombreCliente(), req.getDescripcion(), req.getMonto());
        PedidoResponse res = new PedidoResponse(p.getId(), p.getNombreCliente(), p.getDescripcion(), p.getMonto(), p.getEstado());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listar() {
        List<PedidoResponse> out = service.listarPedidos().stream()
                .map(p -> new PedidoResponse(p.getId(), p.getNombreCliente(), p.getDescripcion(), p.getMonto(), p.getEstado()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        Pedido p = service.obtenerPorId(id);
        if (p == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado");
        PedidoResponse res = new PedidoResponse(p.getId(), p.getNombreCliente(), p.getDescripcion(), p.getMonto(), p.getEstado());
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelar(@PathVariable int id) {
        Pedido p = service.obtenerPorId(id);
        if (p == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado");
        boolean ok = service.cancelarPedido(id);
        if (!ok) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo cancelar");
        Pedido actual = service.obtenerPorId(id);
        return ResponseEntity.ok(java.util.Map.of("mensaje", "Pedido cancelado correctamente", "pedido",
                new PedidoResponse(actual.getId(), actual.getNombreCliente(), actual.getDescripcion(), actual.getMonto(), actual.getEstado())));
    }

    @PostMapping("/despachar")
    public ResponseEntity<?> despachar() {
        Pedido p = service.despacharSiguiente();
        if (p == null) return ResponseEntity.status(HttpStatus.CONFLICT).body("No hay pedidos por despachar");
        return ResponseEntity.ok(java.util.Map.of("mensaje", "Pedido despachado correctamente", "pedido",
                new PedidoResponse(p.getId(), p.getNombreCliente(), p.getDescripcion(), p.getMonto(), p.getEstado())));
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasResponse> estadisticas() {
        return ResponseEntity.ok(service.estadisticas());
    }

    @GetMapping("/total-recursivo")
    public ResponseEntity<?> totalRecursivo() {
        double total = service.totalRecursivo();
        return ResponseEntity.ok(java.util.Map.of("totalMontoRecursivo", total));
    }

    @PostMapping("/rollback")
    public ResponseEntity<?> rollback() {
        HistorialOperacion op = service.realizarRollback();
        if (op == null) return ResponseEntity.status(HttpStatus.CONFLICT).body("No hay operaciones para revertir");
        // devolver info del pedido resultante
        Pedido despues = op.getPedidoAntes() != null ? op.getPedidoAntes() : op.getPedidoDespues();
        return ResponseEntity.ok(java.util.Map.of("mensaje", "Rollback realizado correctamente", "operacionRevertida", op.getTipoOperacion(), "pedido", despues));
    }
}