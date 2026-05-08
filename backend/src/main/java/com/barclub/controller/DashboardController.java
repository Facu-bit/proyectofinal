package com.barclub.controller;

import com.barclub.dto.ResumenDiaDTO;
import com.barclub.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    // GET /api/dashboard/resumen  (resumen de hoy)
    @GetMapping("/resumen")
    public ResponseEntity<ResumenDiaDTO> resumenHoy() {
        return ResponseEntity.ok(dashboardService.resumenDelDia(LocalDate.now()));
    }

    // GET /api/dashboard/resumen?fecha=2025-04-10
    @GetMapping("/resumen/fecha")
    public ResponseEntity<ResumenDiaDTO> resumenPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(dashboardService.resumenDelDia(fecha));
    }
}
