package org.tfg.model.entities;

import jakarta.persistence.*;
import org.tfg.model.enums.Combustible;
import org.tfg.model.enums.TipoCambio;
import org.tfg.model.enums.EtiquetaAmbiental;
import org.tfg.model.enums.EstadoCoche;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "coches")
public class Coche {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String marca;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Column(nullable = false, length = 100)
    private String motor;

    @Column(length = 50)
    private String color;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoCambio tipoCambio;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Combustible combustible;

    @Column(nullable = false)
    private int numeroPuertas;

    @Column(nullable = false, length = 255)
    private String ubicacion;

    @Column(nullable = false)
    private int caballosPotencia;

    @Column(nullable = false)
    private int kilometros;

    @Column(nullable = false)
    private double precio;

    @Column(nullable = false)
    private int numeroPlazas;

    @Column(nullable = false)
    private int centimetrosCubicos;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EtiquetaAmbiental etiquetaAmbiental;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoCoche estado = EstadoCoche.EN_VENTA;

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private int ano;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column
    private LocalDateTime fechaActualizacion;

    @Column
    private LocalDateTime fechaVenta;

    // Relación con el usuario (propietario)
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relación inversa con transacciones
    @OneToMany(mappedBy = "coche", cascade = CascadeType.ALL)
    private List<Transaccion> transacciones;

    // Relación inversa con usuarios que lo tienen como favorito
    @ManyToMany(mappedBy = "cochesFavoritos")
    private List<Usuario> usuariosFavorito;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getMotor() { return motor; }
    public void setMotor(String motor) { this.motor = motor; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public TipoCambio getTipoCambio() { return tipoCambio; }
    public void setTipoCambio(TipoCambio tipoCambio) { this.tipoCambio = tipoCambio; }

    public Combustible getCombustible() { return combustible; }
    public void setCombustible(Combustible combustible) { this.combustible = combustible; }

    public int getNumeroPuertas() { return numeroPuertas; }
    public void setNumeroPuertas(int numeroPuertas) { this.numeroPuertas = numeroPuertas; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public int getCaballosPotencia() { return caballosPotencia; }
    public void setCaballosPotencia(int caballosPotencia) { this.caballosPotencia = caballosPotencia; }

    public int getKilometros() { return kilometros; }
    public void setKilometros(int kilometros) { this.kilometros = kilometros; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getNumeroPlazas() { return numeroPlazas; }
    public void setNumeroPlazas(int numeroPlazas) { this.numeroPlazas = numeroPlazas; }

    public int getCentimetrosCubicos() { return centimetrosCubicos; }
    public void setCentimetrosCubicos(int centimetrosCubicos) { this.centimetrosCubicos = centimetrosCubicos; }

    public EtiquetaAmbiental getEtiquetaAmbiental() { return etiquetaAmbiental; }
    public void setEtiquetaAmbiental(EtiquetaAmbiental etiquetaAmbiental) { this.etiquetaAmbiental = etiquetaAmbiental; }

    public EstadoCoche getEstado() { return estado; }
    public void setEstado(EstadoCoche estado) { this.estado = estado; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<Transaccion> getTransacciones() { return transacciones; }
    public void setTransacciones(List<Transaccion> transacciones) { this.transacciones = transacciones; }

    public List<Usuario> getUsuariosFavorito() { return usuariosFavorito; }
    public void setUsuariosFavorito(List<Usuario> usuariosFavorito) { this.usuariosFavorito = usuariosFavorito; }
}