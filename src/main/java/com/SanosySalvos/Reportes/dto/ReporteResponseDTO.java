package com.SanosySalvos.Reportes.dto;

import com.SanosySalvos.Reportes.model.EstadoReporte;
import com.SanosySalvos.Reportes.model.TipoReporte;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReporteResponseDTO {

    // El ID real en la base de datos (vital para la HU-3)
    private Long id; 

    private Long usuarioId;
    private Long mascotaId;
    
    private TipoReporte tipoReporte;
    
    // Para que el frontend sepa de qué color pintar el pin en el mapa
    private EstadoReporte estado; 

    private String titulo;
    private String descripcion;
    private String urlImagen;
    
    private Double latitud;
    private Double longitud;
    
    private LocalDateTime fechaIncidente;
    
    // Útil para que la interfaz muestre textos como: "Publicado el 24 de Mayo"
    private LocalDateTime fechaCreacion; 
}