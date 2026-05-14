package org.tfg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CocheFotoResponse {
    private Long id;
    private String nombreArchivo;
    private boolean portada;
    private Integer orden;
    private String url;
}

