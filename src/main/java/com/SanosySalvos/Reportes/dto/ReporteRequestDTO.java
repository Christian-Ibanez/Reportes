package com.SanosySalvos.Reportes.dto;

import com.SanosySalvos.Reportes.model.TipoReporte;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReporteRequestDTO {

    // ¿Quién está haciendo el reporte? (Viene del microservicio de Usuarios)
    private Long usuarioId;

    // --- HU-1 ---
    // Si el usuario es dueño, enviará el ID de su mascota. 
    // Si es alguien que encontró un animal (HU-2), esto llegará como null.
    private Long mascotaId;

    // ¿Qué pasó? (PERDIDO o ENCONTRADO)
    private TipoReporte tipoReporte;

    private String titulo;
    private String descripcion;
    
    // --- HU-2 ---
    // Clave para cuando alguien encuentra un animal y le toma una foto
    private String urlImagen;

    // ¿Cuándo ocurrió el suceso?
    private LocalDateTime fechaIncidente;

    // Volver a exigir estos datos cuando el frontend tenga el mapa listo
    private Double latitud;
    private Double longitud;
}