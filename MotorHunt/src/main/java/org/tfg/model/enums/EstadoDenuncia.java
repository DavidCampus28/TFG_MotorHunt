package org.tfg.model.enums;

public enum EstadoDenuncia {
    PENDIENTE("Pendiente de revisar"),
    REVISANDO("En revisión"),
    RESUELTA("Resuelta"),
    RECHAZADA("Rechazada");

    private final String descripcion;

    EstadoDenuncia(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

