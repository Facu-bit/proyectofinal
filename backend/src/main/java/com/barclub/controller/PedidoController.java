package com.barclub.controller;

import com.barclub.dto.DetallePedidoRequestDTO;
import com.barclub.dto.PedidoRequestDTO;
import com.barclub.dto.PedidoResponseDTO;
import com.barclub.entity.EstadoPedido;
import com.barclub.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;

    // GET /api/pedidos
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    // GET /api/pedidos/activos  (para panel de cocina)
    @GetMapping("/activos")
    public ResponseEntity<List<PedidoResponseDTO>> listarActivos() {
        return ResponseEntity.ok(pedidoService.listarActivos());
    }

    // GET /api/pedidos/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorEstado(
            @PathVariable EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.listarPorEstado(estado));
    }

    // GET /api/pedidos/fecha?fecha=2025-04-10
    @GetMapping("/fecha")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(pedidoService.listarPorFecha(fecha));
    }

    // GET /api/pedidos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    // POST /api/pedidos
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crear(
            @Valid @RequestBody PedidoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(dto));
    }

    // PATCH /api/pedidos/{id}/estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, estado));
    }

    // PATCH /api/pedidos/{id}/cancelar
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelar(id));
    }

    // POST /api/pedidos/{id}/detalles  (agregar producto a un pedido)
    @PostMapping("/{id}/detalles")
    public ResponseEntity<PedidoResponseDTO> agregarDetalle(
            @PathVariable Long id,
            @Valid @RequestBody DetallePedidoRequestDTO detalleDTO) {
        return ResponseEntity.ok(pedidoService.agregarDetalle(id, detalleDTO));
    }

    // DELETE /api/pedidos/{pedidoId}/detalles/{detalleId}
    @DeleteMapping("/{pedidoId}/detalles/{detalleId}")
    public ResponseEntity<PedidoResponseDTO> eliminarDetalle(
            @PathVariable Long pedidoId,
            @PathVariable Long detalleId) {
        return ResponseEntity.ok(pedidoService.eliminarDetalle(pedidoId, detalleId));
    }
}
