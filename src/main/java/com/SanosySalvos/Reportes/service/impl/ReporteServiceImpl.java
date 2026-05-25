package com.SanosySalvos.Reportes.service.impl;

import com.SanosySalvos.Reportes.dto.ReporteRequestDTO;
import com.SanosySalvos.Reportes.dto.ReporteResponseDTO;
import com.SanosySalvos.Reportes.model.EstadoReporte;
import com.SanosySalvos.Reportes.model.Reporte;
import com.SanosySalvos.Reportes.repository.ReporteRepository;
import com.SanosySalvos.Reportes.service.ReporteService;
import com.SanosySalvos.Reportes.service.UsuarioClient;

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

    @Autowired
    private UsuarioClient usuarioClient;
    
    @Override
    public ReporteResponseDTO crearReporte(ReporteRequestDTO requestDTO) {
        if (requestDTO.getUsuarioId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID del usuario es obligatorio para crear un reporte.");
        }

        boolean usuarioValido = usuarioClient.verificarUsuarioExterno(requestDTO.getUsuarioId());
        if (!usuarioValido) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario indicado no existe en el sistema.");
        }

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
        
        Reporte reporteGuardado = reporteRepository.save(reporte);

        return mapearAResponseDTO(reporteGuardado);
    }

    @Override
    public List<ReporteResponseDTO> obtenerReportesActivos() {
        
        List<Reporte> reportes = reporteRepository.findByEstado(EstadoReporte.ACTIVO);
        
        return reportes.stream()
                .map(this::mapearAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReporteResponseDTO marcarComoResuelto(Long reporteId, Long usuarioId) {
        Reporte reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El reporte no existe."));

        if (!reporte.getUsuarioId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado: Solo el creador del reporte puede cerrarlo.");
        }

        reporte.setEstado(EstadoReporte.RESUELTO);
        Reporte reporteActualizado = reporteRepository.save(reporte);

        return mapearAResponseDTO(reporteActualizado);
    }
    
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