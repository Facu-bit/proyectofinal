package com.barclub.controller;

import com.barclub.dto.ReservaRequestDTO;
import com.barclub.dto.ReservaResponseDTO;
import com.barclub.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReservaController {

    private final ReservaService reservaService;

    // GET /api/reservas
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    // GET /api/reservas/hoy
    @GetMapping("/hoy")
    public ResponseEntity<List<ReservaResponseDTO>> listarDeHoy() {
        return ResponseEntity.ok(reservaService.listarDeHoy());
    }

    // GET /api/reservas/fecha?fecha=2025-04-10
    @GetMapping("/fecha")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(reservaService.listarPorFecha(fecha));
    }

    // GET /api/reservas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    // POST /api/reservas
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(
            @Valid @RequestBody ReservaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(dto));
    }

    // PUT /api/reservas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReservaRequestDTO dto) {
        return ResponseEntity.ok(reservaService.actualizar(id, dto));
    }

    // PATCH /api/reservas/{id}/cancelar
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelar(id));
    }

    // PATCH /api/reservas/{id}/completar
    @PatchMapping("/{id}/completar")
    public ResponseEntity<ReservaResponseDTO> completar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.completar(id));
    }
}
