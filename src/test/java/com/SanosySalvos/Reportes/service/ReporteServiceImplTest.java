package com.SanosySalvos.Reportes.service;

import com.SanosySalvos.Reportes.dto.ReporteRequestDTO;
import com.SanosySalvos.Reportes.dto.ReporteResponseDTO;
import com.SanosySalvos.Reportes.model.EstadoReporte;
import com.SanosySalvos.Reportes.model.Reporte;
import com.SanosySalvos.Reportes.model.TipoReporte;
import com.SanosySalvos.Reportes.repository.ReporteRepository;
import com.SanosySalvos.Reportes.service.impl.ReporteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {

    @Mock
    private ReporteRepository reporteRepository;

    @InjectMocks
    private ReporteServiceImpl reporteService;

    private Reporte reporteActivo;
    private ReporteRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        // Preparamos datos de prueba (Arrange) que se reinician antes de cada test
        reporteActivo = new Reporte();
        reporteActivo.setId(1L);
        reporteActivo.setUsuarioId(10L);
        reporteActivo.setMascotaId(5L);
        reporteActivo.setTitulo("Perrito perdido");
        reporteActivo.setEstado(EstadoReporte.ACTIVO);
        reporteActivo.setTipoReporte(TipoReporte.PERDIDO);
        reporteActivo.setFechaCreacion(LocalDateTime.now());

        requestDTO = new ReporteRequestDTO();
        requestDTO.setUsuarioId(10L);
        requestDTO.setMascotaId(5L);
        requestDTO.setTitulo("Perrito perdido");
        requestDTO.setTipoReporte(TipoReporte.PERDIDO);
    }

    // --- TESTS PARA CREAR REPORTE ---

    @Test
    void crearReporte_Exito() {
        // Arrange: Simulamos que al guardar cualquier reporte, el repositorio devuelve nuestro reporte de prueba
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteActivo);

        // Act: Ejecutamos el método
        ReporteResponseDTO response = reporteService.crearReporte(requestDTO);

        // Assert: Verificamos los resultados
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Perrito perdido", response.getTitulo());
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    @Test
    void crearReporte_LanzaExcepcion_SiUsuarioEsNulo() {
        // Arrange
        requestDTO.setUsuarioId(null);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            reporteService.crearReporte(requestDTO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(reporteRepository, never()).save(any(Reporte.class));
    }

    // --- TESTS PARA OBTENER ACTIVOS ---

    @Test
    void obtenerReportesActivos_Exito() {
        // Arrange
        when(reporteRepository.findByEstado(EstadoReporte.ACTIVO)).thenReturn(List.of(reporteActivo));

        // Act
        List<ReporteResponseDTO> response = reporteService.obtenerReportesActivos();

        // Assert
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertEquals(EstadoReporte.ACTIVO, response.get(0).getEstado());
        verify(reporteRepository, times(1)).findByEstado(EstadoReporte.ACTIVO);
    }

    // --- TESTS PARA MARCAR COMO RESUELTO ---

    @Test
    void marcarComoResuelto_Exito() {
        // Arrange: El usuario 10L es el dueño del reporte 1L
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteActivo));
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteActivo); // Guardará el cambio de estado

        // Act
        ReporteResponseDTO response = reporteService.marcarComoResuelto(1L, 10L);

        // Assert
        assertNotNull(response);
        assertEquals(EstadoReporte.RESUELTO, reporteActivo.getEstado()); // Verificamos que mutó a resuelto
        verify(reporteRepository, times(1)).save(reporteActivo);
    }

    @Test
    void marcarComoResuelto_LanzaExcepcion_SiNoEsDueno() {
        // Arrange: Intentamos cerrarlo con el usuario 99L (un intruso)
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteActivo));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            reporteService.marcarComoResuelto(1L, 99L);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Solo el creador del reporte puede cerrarlo"));
        verify(reporteRepository, never()).save(any(Reporte.class));
    }

    @Test
    void marcarComoResuelto_LanzaExcepcion_SiReporteNoExiste() {
        // Arrange: El repositorio no encuentra nada
        when(reporteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            reporteService.marcarComoResuelto(999L, 10L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(reporteRepository, never()).save(any(Reporte.class));
    }
}