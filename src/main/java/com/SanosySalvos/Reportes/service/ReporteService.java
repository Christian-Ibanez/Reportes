package com.SanosySalvos.Reportes.service;

import com.SanosySalvos.Reportes.dto.ReporteRequestDTO;
import com.SanosySalvos.Reportes.dto.ReporteResponseDTO;
import java.util.List;

public interface ReporteService {
    // Para HU-1 y HU-2: Crear un nuevo reporte
    ReporteResponseDTO crearReporte(ReporteRequestDTO requestDTO);

    // Para mostrar los pines en el mapa
    List<ReporteResponseDTO> obtenerReportesActivos();

    // Para HU-3: Marcar un caso como resuelto
    ReporteResponseDTO marcarComoResuelto(Long reporteId, Long usuarioId);
}