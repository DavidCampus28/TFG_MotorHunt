package org.tfg.model.enums;

public enum TipoAlerta {
    MULTIPLES_CUENTAS("Múltiples cuentas"),
    IP_SOSPECHOSA("IP sospechosa"),
    ANUNCIOS_REPETIDOS("Anuncios repetidos"),
    FRAUDE_POTENCIAL("Fraude potencial"),
    COCHE_ROBADO("Coche robado"),
    PHISHING("Phishing/Scam"),
    PRECIO_ANOMALO("Precio anómalo"),
    KILOMETRAJE_SOSPECHOSO("Kilometraje sospechoso"),
    MATRICULA_INVALIDA("Matrícula inválida");

    private final String descripcion;

    TipoAlerta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

