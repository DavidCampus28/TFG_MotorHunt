package org.tfg.model.entities;

import jakarta.persistence.*;
import org.tfg.model.enums.TipoDenuncia;
import org.tfg.model.enums.EstadoDenuncia;
import java.time.LocalDateTime;

@Entity
@Table(name = "denuncias")
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoDenuncia tipo; // FRAUDE, SPAM, COCHE_INEXISTENTE, PRECIO_FALSO, COMPORTAMIENTO_SOSPECHOSO

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoDenuncia estado = EstadoDenuncia.PENDIENTE; // PENDIENTE, REVISANDO, RESUELTA, RECHAZADA

    @ManyToOne
    @JoinColumn(name = "denunciante_id", nullable = false)
    private Usuario denunciante;

    @ManyToOne
    @JoinColumn(name = "usuario_denunciado_id")
    private Usuario usuarioDenunciado;

    @ManyToOne
    @JoinColumn(name = "coche_denunciado_id")
    private Coche cocheDenunciado;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column
    private LocalDateTime fechaResolucion;

    @Column
    private String resolucion;

    @ManyToOne
    @JoinColumn(name = "admin_revisor_id")
    private Usuario adminRevisor;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoDenuncia getTipo() { return tipo; }
    public void setTipo(TipoDenuncia tipo) { this.tipo = tipo; }

    public EstadoDenuncia getEstado() { return estado; }
    public void setEstado(EstadoDenuncia estado) { this.estado = estado; }

    public Usuario getDenunciante() { return denunciante; }
    public void setDenunciante(Usuario denunciante) { this.denunciante = denunciante; }

    public Usuario getUsuarioDenunciado() { return usuarioDenunciado; }
    public void setUsuarioDenunciado(Usuario usuarioDenunciado) { this.usuarioDenunciado = usuarioDenunciado; }

    public Coche getCocheDenunciado() { return cocheDenunciado; }
    public void setCocheDenunciado(Coche cocheDenunciado) { this.cocheDenunciado = cocheDenunciado; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    public String getResolucion() { return resolucion; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }

    public Usuario getAdminRevisor() { return adminRevisor; }
    public void setAdminRevisor(Usuario adminRevisor) { this.adminRevisor = adminRevisor; }
}

