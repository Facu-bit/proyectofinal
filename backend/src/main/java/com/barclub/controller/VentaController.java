package com.barclub.controller;

import com.barclub.dto.VentaRequestDTO;
import com.barclub.dto.VentaResponseDTO;
import com.barclub.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VentaController {

    private final VentaService ventaService;

    // GET /api/ventas
    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(ventaService.listarTodas());
    }

    // GET /api/ventas/hoy
    @GetMapping("/hoy")
    public ResponseEntity<List<VentaResponseDTO>> listarDeHoy() {
        return ResponseEntity.ok(ventaService.listarPorFecha(LocalDate.now()));
    }

    // GET /api/ventas/fecha?fecha=2025-04-10
    @GetMapping("/fecha")
    public ResponseEntity<List<VentaResponseDTO>> listarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(ventaService.listarPorFecha(fecha));
    }

    // GET /api/ventas/total?fecha=2025-04-10
    @GetMapping("/total")
    public ResponseEntity<Double> totalDelDia(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
        return ResponseEntity.ok(ventaService.totalDelDia(fechaConsulta));
    }

    // GET /api/ventas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.obtenerPorId(id));
    }

    // POST /api/ventas  (registrar cobro de un pedido)
    @PostMapping
    public ResponseEntity<VentaResponseDTO> registrar(
            @Valid @RequestBody VentaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.registrar(dto));
    }
}
