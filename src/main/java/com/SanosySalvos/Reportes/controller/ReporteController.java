package com.SanosySalvos.Reportes.controller;

import com.SanosySalvos.Reportes.dto.ReporteRequestDTO;
import com.SanosySalvos.Reportes.dto.ReporteResponseDTO;
import com.SanosySalvos.Reportes.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    // --- HU-1 y HU-2: Crear un nuevo reporte ---
    // Método POST: http://localhost:8081/api/reportes
    @PostMapping
    public ResponseEntity<ReporteResponseDTO> crearReporte(@RequestBody ReporteRequestDTO requestDTO) {
        ReporteResponseDTO nuevoReporte = reporteService.crearReporte(requestDTO);
        return new ResponseEntity<>(nuevoReporte, HttpStatus.CREATED);
    }

    // --- Mostrar pines en el mapa ---
    // Método GET: http://localhost:8081/api/reportes/activos
    @GetMapping("/activos")
    public ResponseEntity<List<ReporteResponseDTO>> obtenerReportesActivos() {
        List<ReporteResponseDTO> reportes = reporteService.obtenerReportesActivos();
        return ResponseEntity.ok(reportes);
    }

    // --- HU-3: Marcar un caso como resuelto ---
    // Método PUT: http://localhost:8081/api/reportes/1/resolver?usuarioId=5
    @PutMapping("/{id}/resolver")
    public ResponseEntity<ReporteResponseDTO> marcarComoResuelto(
            @PathVariable("id") Long reporteId,
            @RequestParam("usuarioId") Long usuarioId) {
        
        ReporteResponseDTO reporteActualizado = reporteService.marcarComoResuelto(reporteId, usuarioId);
        return ResponseEntity.ok(reporteActualizado);
    }
}