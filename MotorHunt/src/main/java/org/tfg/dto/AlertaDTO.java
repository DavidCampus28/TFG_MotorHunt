package org.tfg.dto;

import java.time.LocalDateTime;

public class AlertaDTO {
    private Long id;
    private String tipo;
    private String titulo;
    private String descripcion;
    private int nivelRiesgo;
    private String usuario;
    private Long usuarioId;
    private String coche;
    private Long cocheId;
    private String ipSospechosa;
    private Boolean resuelta;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaResolucion;

    // Constructores
    public AlertaDTO() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getNivelRiesgo() { return nivelRiesgo; }
    public void setNivelRiesgo(int nivelRiesgo) { this.nivelRiesgo = nivelRiesgo; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getCoche() { return coche; }
    public void setCoche(String coche) { this.coche = coche; }

    public Long getCocheId() { return cocheId; }
    public void setCocheId(Long cocheId) { this.cocheId = cocheId; }

    public String getIpSospechosa() { return ipSospechosa; }
    public void setIpSospechosa(String ipSospechosa) { this.ipSospechosa = ipSospechosa; }

    public Boolean getResuelta() { return resuelta; }
    public void setResuelta(Boolean resuelta) { this.resuelta = resuelta; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }
}

