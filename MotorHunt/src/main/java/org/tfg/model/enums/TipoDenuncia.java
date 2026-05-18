package org.tfg.model.enums;

public enum TipoDenuncia {
    FRAUDE("Fraude"),
    SPAM("Spam"),
    COCHE_INEXISTENTE("Coche inexistente"),
    PRECIO_FALSO("Precio falso"),
    COMPORTAMIENTO_SOSPECHOSO("Comportamiento sospechoso"),
    CONTENIDO_INAPROPIADO("Contenido inapropiado"),
    ABUSO("Abuso"),
    OTRO("Otro");

    private final String descripcion;

    TipoDenuncia(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

