package org.tfg.model.entities;

import jakarta.persistence.*;
import org.tfg.model.enums.Rol;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 255)
    private String telefono;

    @Column(length = 500)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean activo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column
    private LocalDateTime ultimaActividad;

    // Relación con coches que este usuario posee
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coche> coches;

    // Relación con coches favoritos
    @ManyToMany
    @JoinTable(
        name = "usuario_coches_favoritos",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "coche_id")
    )
    private List<Coche> cochesFavoritos;

    // Relación con compras realizadas
    @OneToMany(mappedBy = "comprador", cascade = CascadeType.ALL)
    private List<Transaccion> compras;

    // Relación con ventas realizadas
    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL)
    private List<Transaccion> ventas;

    // Relación inversa con mensajes enviados
    @OneToMany(mappedBy = "remitente", cascade = CascadeType.ALL)
    private List<Mensaje> mensajesEnviados;

    // Relación inversa con mensajes recibidos
    @OneToMany(mappedBy = "destinatario", cascade = CascadeType.ALL)
    private List<Mensaje> mensajesRecibidos;

    // Relación con me gustas
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeGusta> meGustas;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        ultimaActividad = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ultimaActividad = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDateTime getUltimaActividad() { return ultimaActividad; }
    public void setUltimaActividad(LocalDateTime ultimaActividad) { this.ultimaActividad = ultimaActividad; }

    public List<Coche> getCoches() { return coches; }
    public void setCoches(List<Coche> coches) { this.coches = coches; }

    public List<Coche> getCochesFavoritos() { return cochesFavoritos; }
    public void setCochesFavoritos(List<Coche> cochesFavoritos) { this.cochesFavoritos = cochesFavoritos; }

    public List<Transaccion> getCompras() { return compras; }
    public void setCompras(List<Transaccion> compras) { this.compras = compras; }

    public List<Transaccion> getVentas() { return ventas; }
    public void setVentas(List<Transaccion> ventas) { this.ventas = ventas; }

    public List<Mensaje> getMensajesEnviados() { return mensajesEnviados; }
    public void setMensajesEnviados(List<Mensaje> mensajesEnviados) { this.mensajesEnviados = mensajesEnviados; }

    public List<Mensaje> getMensajesRecibidos() { return mensajesRecibidos; }
    public void setMensajesRecibidos(List<Mensaje> mensajesRecibidos) { this.mensajesRecibidos = mensajesRecibidos; }

    public List<MeGusta> getMeGustas() { return meGustas; }
    public void setMeGustas(List<MeGusta> meGustas) { this.meGustas = meGustas; }
}
