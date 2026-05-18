package org.tfg.model.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracion_admin")
public class ConfiguracionAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String urlLogo;

    @Column(length = 255)
    private String urlBanner;

    @Column(columnDefinition = "TEXT")
    private String textoBienvenida;

    @Column(columnDefinition = "TEXT")
    private String textoDescripcion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Usuario adminModificador;

    @PrePersist
    protected void onCreate() {
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // Getters y Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUrlLogo() { return urlLogo; }
    public void setUrlLogo(String urlLogo) { this.urlLogo = urlLogo; }

    public String getUrlBanner() { return urlBanner; }
    public void setUrlBanner(String urlBanner) { this.urlBanner = urlBanner; }

    public String getTextoBienvenida() { return textoBienvenida; }
    public void setTextoBienvenida(String textoBienvenida) { this.textoBienvenida = textoBienvenida; }

    public String getTextoDescripcion() { return textoDescripcion; }
    public void setTextoDescripcion(String textoDescripcion) { this.textoDescripcion = textoDescripcion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public Usuario getAdminModificador() { return adminModificador; }
    public void setAdminModificador(Usuario adminModificador) { this.adminModificador = adminModificador; }
}

