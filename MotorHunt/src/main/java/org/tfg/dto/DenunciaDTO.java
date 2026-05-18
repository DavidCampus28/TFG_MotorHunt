package org.tfg.dto;

import java.time.LocalDateTime;

public class DenunciaDTO {
    private Long id;
    private String tipo;
    private String estado;
    private String descripcion;
    private String denunciante;
    private Long denuncianteId;
    private String usuarioDenunciado;
    private Long usuarioDenunciadoId;
    private String coche;
    private Long cocheId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaResolucion;
    private String resolucion;

    // Constructores
    public DenunciaDTO() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDenunciante() { return denunciante; }
    public void setDenunciante(String denunciante) { this.denunciante = denunciante; }

    public Long getDenuncianteId() { return denuncianteId; }
    public void setDenuncianteId(Long denuncianteId) { this.denuncianteId = denuncianteId; }

    public String getUsuarioDenunciado() { return usuarioDenunciado; }
    public void setUsuarioDenunciado(String usuarioDenunciado) { this.usuarioDenunciado = usuarioDenunciado; }

    public Long getUsuarioDenunciadoId() { return usuarioDenunciadoId; }
    public void setUsuarioDenunciadoId(Long usuarioDenunciadoId) { this.usuarioDenunciadoId = usuarioDenunciadoId; }

    public String getCoche() { return coche; }
    public void setCoche(String coche) { this.coche = coche; }

    public Long getCocheId() { return cocheId; }
    public void setCocheId(Long cocheId) { this.cocheId = cocheId; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    public String getResolucion() { return resolucion; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }
}

