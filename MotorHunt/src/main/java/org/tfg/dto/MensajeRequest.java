package org.tfg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeRequest {
    private Long remitenteId;
    private Long destinatarioId;
    private String contenido;
    private Long cocheId; // Opcional
}

