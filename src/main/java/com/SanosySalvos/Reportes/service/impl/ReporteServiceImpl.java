package com.SanosySalvos.Reportes.service.impl;

import com.SanosySalvos.Reportes.dto.ReporteRequestDTO;
import com.SanosySalvos.Reportes.dto.ReporteResponseDTO;
import com.SanosySalvos.Reportes.model.EstadoReporte;
import com.SanosySalvos.Reportes.model.Reporte;
import com.SanosySalvos.Reportes.repository.ReporteRepository;
import com.SanosySalvos.Reportes.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    @Override
    public ReporteResponseDTO crearReporte(ReporteRequestDTO requestDTO) {
        // 1. Validaciones de seguridad básicas
        if (requestDTO.getUsuarioId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID del usuario es obligatorio para crear un reporte.");
        }

        // 2. Mapear los datos de entrada (DTO) hacia la Entidad que entiende la base de datos
        Reporte reporte = new Reporte();
        reporte.setUsuarioId(requestDTO.getUsuarioId());
        reporte.setMascotaId(requestDTO.getMascotaId());
        reporte.setTipoReporte(requestDTO.getTipoReporte());
        reporte.setTitulo(requestDTO.getTitulo());
        reporte.setDescripcion(requestDTO.getDescripcion());
        reporte.setUrlImagen(requestDTO.getUrlImagen());
        reporte.setFechaIncidente(requestDTO.getFechaIncidente());
        reporte.setLatitud(requestDTO.getLatitud());
        reporte.setLongitud(requestDTO.getLongitud());
        
        // Nota: No seteamos el estado ni la fecha de creación porque el @PrePersist de la entidad lo hará solo.

        // 3. Guardar en PostgreSQL
        Reporte reporteGuardado = reporteRepository.save(reporte);

        // 4. Devolver la respuesta formateada
        return mapearAResponseDTO(reporteGuardado);
    }

    @Override
    public List<ReporteResponseDTO> obtenerReportesActivos() {
        // Buscamos solo los que tienen estado ACTIVO
        List<Reporte> reportes = reporteRepository.findByEstado(EstadoReporte.ACTIVO);
        
        // Convertimos la lista de Entidades a una lista de DTOs usando programación funcional
        return reportes.stream()
                .map(this::mapearAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReporteResponseDTO marcarComoResuelto(Long reporteId, Long usuarioId) {
        // 1. Buscamos el reporte, si no existe lanzamos un 404 NOT FOUND
        Reporte reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El reporte no existe."));

        // 2. Validación de seguridad vital: ¿El que intenta cerrar el reporte es el dueño original?
        if (!reporte.getUsuarioId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado: Solo el creador del reporte puede cerrarlo.");
        }

        // 3. Cambiamos el estado
        reporte.setEstado(EstadoReporte.RESUELTO);
        
        // 4. Guardamos los cambios
        Reporte reporteActualizado = reporteRepository.save(reporte);

        return mapearAResponseDTO(reporteActualizado);
    }

    // --- MÉTODO AUXILIAR PRIVADO ---
    // Lo usamos para no repetir código cada vez que necesitamos transformar un Reporte a DTO
    private ReporteResponseDTO mapearAResponseDTO(Reporte reporte) {
        ReporteResponseDTO dto = new ReporteResponseDTO();
        dto.setId(reporte.getId());
        dto.setUsuarioId(reporte.getUsuarioId());
        dto.setMascotaId(reporte.getMascotaId());
        dto.setTipoReporte(reporte.getTipoReporte());
        dto.setEstado(reporte.getEstado());
        dto.setTitulo(reporte.getTitulo());
        dto.setDescripcion(reporte.getDescripcion());
        dto.setUrlImagen(reporte.getUrlImagen());
        dto.setLatitud(reporte.getLatitud());
        dto.setLongitud(reporte.getLongitud());
        dto.setFechaIncidente(reporte.getFechaIncidente());
        dto.setFechaCreacion(reporte.getFechaCreacion());
        return dto;
    }
}