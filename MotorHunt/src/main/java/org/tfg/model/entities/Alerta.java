package org.tfg.model.entities;

import jakarta.persistence.*;
import org.tfg.model.enums.TipoAlerta;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoAlerta tipo; // MULTIPLES_CUENTAS, IP_SOSPECHOSA, ANUNCIOS_REPETIDOS, FRAUDE_POTENCIAL, COCHE_ROBADO, PHISHING

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private int nivelRiesgo; // 1: bajo, 2: medio, 3: alto, 4: crítico

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "coche_id")
    private Coche coche;

    @Column
    private String ipSospechosa;

    @Column(nullable = false)
    private Boolean resuelta = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column
    private LocalDateTime fechaResolucion;

    @Column
    private String detallesResolucion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoAlerta getTipo() { return tipo; }
    public void setTipo(TipoAlerta tipo) { this.tipo = tipo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getNivelRiesgo() { return nivelRiesgo; }
    public void setNivelRiesgo(int nivelRiesgo) { this.nivelRiesgo = nivelRiesgo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Coche getCoche() { return coche; }
    public void setCoche(Coche coche) { this.coche = coche; }

    public String getIpSospechosa() { return ipSospechosa; }
    public void setIpSospechosa(String ipSospechosa) { this.ipSospechosa = ipSospechosa; }

    public Boolean getResuelta() { return resuelta; }
    public void setResuelta(Boolean resuelta) { this.resuelta = resuelta; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    public String getDetallesResolucion() { return detallesResolucion; }
    public void setDetallesResolucion(String detallesResolucion) { this.detallesResolucion = detallesResolucion; }
}

