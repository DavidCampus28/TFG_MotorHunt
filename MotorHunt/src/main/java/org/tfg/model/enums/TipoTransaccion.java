package org.tfg.model.enums;

public enum TipoTransaccion {
    COMPRA("Compra"),
    VENTA("Venta"),
    INTERCAMBIO("Intercambio");

    private final String descripcion;

    TipoTransaccion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
