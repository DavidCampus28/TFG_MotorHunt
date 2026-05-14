package org.tfg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private String telefono;
    private String direccion;
    private String rol; // USUARIO o EMPRESA
}