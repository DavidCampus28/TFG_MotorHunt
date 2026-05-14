package org.tfg.model.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coche_fotos")
public class CocheFoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] contenido;

    @Column(nullable = false, length = 255)
    private String nombreArchivo;

    @Column(length = 100)
    private String contentType;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean portada = false;

    @Column(nullable = false)
    private Integer orden = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaSubida;

    @ManyToOne
    @JoinColumn(name = "coche_id", nullable = false)
    private Coche coche;

    @PrePersist
    protected void onCreate() {
        fechaSubida = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public byte[] getContenido() { return contenido; }
    public void setContenido(byte[] contenido) { this.contenido = contenido; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Boolean getPortada() { return portada; }
    public void setPortada(Boolean portada) { this.portada = portada; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }

    public LocalDateTime getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(LocalDateTime fechaSubida) { this.fechaSubida = fechaSubida; }

    public Coche getCoche() { return coche; }
    public void setCoche(Coche coche) { this.coche = coche; }
}

