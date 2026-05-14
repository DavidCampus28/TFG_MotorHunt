package org.tfg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CocheDetalleResponse {
    private Long id;
    private String marca;
    private String modelo;
    private String motor;
    private String color;
    private int caballosPotencia;
    private int kilometros;
    private double precio;
    private int ano;
    private String combustible;
    private String tipoCambio;
    private int numeroPuertas;
    private int numeroPlazas;
    private int centimetrosCubicos;
    private String etiquetaAmbiental;
    private String estado;
    private String descripcion;
    private String ubicacion;

    // Info del vendedor
    private Long vendedorId;
    private String vendedorNombre;
    private String vendedorEmail;
    private String vendedorTelefono;
    private String vendedorDireccion;
    private String vendedorRol;

    // Me gusta info
    private boolean tieneMemGusta;
    private long totalMeGustas;

    // Fotos
    private List<CocheFotoResponse> fotos;
    private String fotoPortadaUrl;
}

