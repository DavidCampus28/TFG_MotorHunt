package org.tfg.model.enums;

public enum EstadoCoche {
    EN_VENTA("En venta"),
    VENDIDO("Vendido"),
    RESERVADO("Reservado"),
    FUERA_SERVICIO("Fuera de servicio");

    private final String descripcion;

    EstadoCoche(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
