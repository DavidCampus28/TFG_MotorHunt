package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tfg.model.entities.*;
import org.tfg.model.enums.EstadoDenuncia;
import org.tfg.repository.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/admin/advanced")
@CrossOrigin(origins = "*")
public class AdminAdvancedController {

    @Autowired
    private DenunciaRepository denunciaRepository;

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private ConfiguracionAdminRepository configuracionAdminRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CocheRepository cocheRepository;

    // ===== DENUNCIAS =====

    @GetMapping("/denuncias")
    public ResponseEntity<?> listarDenuncias(@RequestParam(required = false) String estado,
                                              @RequestParam(required = false) String tipo) {
        try {
            List<Denuncia> denuncias = denunciaRepository.findAll();

            if (estado != null && !estado.isEmpty()) {
                EstadoDenuncia estadoEnum = EstadoDenuncia.valueOf(estado.trim().toUpperCase());
                denuncias = denuncias.stream()
                        .filter(d -> d.getEstado() == estadoEnum)
                        .collect(Collectors.toList());
            }

            if (tipo != null && !tipo.isEmpty()) {
                denuncias = denuncias.stream()
                        .filter(d -> d.getTipo() != null && d.getTipo().name().equals(tipo.trim().toUpperCase()))
                        .collect(Collectors.toList());
            }

            denuncias.sort(Comparator.comparing(Denuncia::getFechaCreacion,
                    Comparator.nullsFirst(Comparator.naturalOrder())).reversed());

            var resultado = denuncias.stream().map(this::mapearDenuncia).collect(Collectors.toList());
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Filtro de denuncias no valido"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudieron cargar las denuncias: " + e.getMessage()));
        }
    }

    @GetMapping("/denuncias/{id}")
    public ResponseEntity<?> obtenerDenuncia(@PathVariable Long id) {
        try {
            Optional<Denuncia> denuncia = denunciaRepository.findById(id);
            if (denuncia.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(mapearDenuncia(denuncia.get()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo cargar la denuncia: " + e.getMessage()));
        }
    }

    @PutMapping("/denuncias/{id}/resolver")
    public ResponseEntity<?> resolverDenuncia(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<Denuncia> denunciaOpt = denunciaRepository.findById(id);
            if (denunciaOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Denuncia denuncia = denunciaOpt.get();
            String estado = (String) request.get("estado");
            EstadoDenuncia estadoEnum = EstadoDenuncia.valueOf(estado.trim().toUpperCase());
            denuncia.setEstado(estadoEnum);
            denuncia.setResolucion((String) request.get("resolucion"));
            denuncia.setFechaResolucion(LocalDateTime.now());

            Object adminId = request.get("adminId");
            if (adminId instanceof Number) {
                Optional<Usuario> admin = usuarioRepository.findById(((Number) adminId).longValue());
                admin.ifPresent(denuncia::setAdminRevisor);
            }

            // Si se resuelve como RESUELTA y hay una acción
            String accion = (String) request.get("accion");
            if (estadoEnum == EstadoDenuncia.RESUELTA && accion != null) {

                // Bloquear usuario denunciado
                if ("bloquear_usuario".equals(accion) && denuncia.getUsuarioDenunciado() != null) {
                    Usuario usuario = denuncia.getUsuarioDenunciado();
                    usuario.setBloqueado(true);
                    usuario.setMotivoBloqueado((String) request.get("motivo"));
                    usuario.setNumeroDenuncias((usuario.getNumeroDenuncias() == null ? 0 : usuario.getNumeroDenuncias()) + 1);
                    usuarioRepository.save(usuario);
                }

                // Bloquear coche
                else if ("bloquear_coche".equals(accion) && denuncia.getCocheDenunciado() != null) {
                    Coche coche = denuncia.getCocheDenunciado();
                    coche.setBloqueado(true);
                    coche.setMotivoBloqueado((String) request.get("motivo"));
                    coche.setNumeroDenuncias((coche.getNumeroDenuncias() == null ? 0 : coche.getNumeroDenuncias()) + 1);
                    cocheRepository.save(coche);
                }

                // Incrementar contador sin bloquear
                else if ("solo_contador_usuario".equals(accion) && denuncia.getUsuarioDenunciado() != null) {
                    Usuario usuario = denuncia.getUsuarioDenunciado();
                    usuario.setNumeroDenuncias((usuario.getNumeroDenuncias() == null ? 0 : usuario.getNumeroDenuncias()) + 1);
                    usuarioRepository.save(usuario);
                }

                // Incrementar contador en coche sin bloquear
                else if ("solo_contador_coche".equals(accion) && denuncia.getCocheDenunciado() != null) {
                    Coche coche = denuncia.getCocheDenunciado();
                    coche.setNumeroDenuncias((coche.getNumeroDenuncias() == null ? 0 : coche.getNumeroDenuncias()) + 1);
                    cocheRepository.save(coche);
                }
            }

            denunciaRepository.save(denuncia);
            return ResponseEntity.ok(Map.of("mensaje", "Denuncia resuelta"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Estado de denuncia no valido"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo resolver la denuncia: " + e.getMessage()));
        }
    }

    @PostMapping("/denuncias")
    public ResponseEntity<?> crearDenuncia(@RequestBody Map<String, Object> request) {
        try {
            Denuncia denuncia = new Denuncia();
            denuncia.setTipo(org.tfg.model.enums.TipoDenuncia.valueOf((String) request.get("tipo")));
            denuncia.setDescripcion((String) request.get("descripcion"));

            Object denuncianteId = request.get("denuncianteId");
            if (denuncianteId instanceof Number) {
                Optional<Usuario> denunciante = usuarioRepository.findById(((Number) denuncianteId).longValue());
                denunciante.ifPresent(denuncia::setDenunciante);
            } else {
                return ResponseEntity.badRequest().body("Denunciante requerido");
            }

            Object usuarioDenunciadoId = request.get("usuarioDenunciadoId");
            if (usuarioDenunciadoId instanceof Number) {
                Optional<Usuario> usuarioDenunciado = usuarioRepository.findById(((Number) usuarioDenunciadoId).longValue());
                usuarioDenunciado.ifPresent(denuncia::setUsuarioDenunciado);
            }

            Object cocheDenunciadoId = request.get("cocheDenunciadoId");
            if (cocheDenunciadoId instanceof Number) {
                Optional<Coche> cocheDenunciado = cocheRepository.findById(((Number) cocheDenunciadoId).longValue());
                cocheDenunciado.ifPresent(denuncia::setCocheDenunciado);
            }

            Denuncia guardada = denunciaRepository.save(denuncia);
            return ResponseEntity.ok(Map.of("id", guardada.getId(), "mensaje", "Denuncia creada"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // ===== ALERTAS =====

    @GetMapping("/alertas")
    public ResponseEntity<?> listarAlertas(@RequestParam(required = false) Boolean resueltas) {
        try {
            List<Alerta> alertas;
            if (resueltas != null) {
                alertas = alertaRepository.findAll().stream()
                        .filter(a -> a.getResuelta() == resueltas)
                        .collect(Collectors.toList());
            } else {
                alertas = alertaRepository.findAll();
            }

            alertas.sort((a, b) -> b.getFechaCreacion().compareTo(a.getFechaCreacion()));

            var resultado = alertas.stream().map(this::mapearAlerta).collect(Collectors.toList());
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/alertas/criticas")
    public ResponseEntity<?> obtenerAlertasCriticas() {
        try {
            List<Alerta> alertas = alertaRepository.findByNivelRiesgoGreaterThanEqual(3).stream()
                    .filter(a -> !a.getResuelta())
                    .collect(Collectors.toList());

            var resultado = alertas.stream().map(this::mapearAlerta).collect(Collectors.toList());
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/alertas/{id}/resolver")
    public ResponseEntity<?> resolverAlerta(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<Alerta> alertaOpt = alertaRepository.findById(id);
            if (alertaOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Alerta alerta = alertaOpt.get();
            alerta.setResuelta(true);
            alerta.setFechaResolucion(LocalDateTime.now());
            alerta.setDetallesResolucion((String) request.get("detalles"));

            alertaRepository.save(alerta);
            return ResponseEntity.ok(Map.of("mensaje", "Alerta resuelta"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // ===== ANALYTICS =====

    @GetMapping("/analytics/dashboard")
    public ResponseEntity<?> obtenerDashboard(@RequestParam(required = false) String mes) {
        try {
            Map<String, Object> analytics = new HashMap<>();

            // Alertas críticas no resueltas
            long alertasCriticas = alertaRepository.findByNivelRiesgoGreaterThanEqual(3).stream()
                    .filter(a -> !a.getResuelta())
                    .count();

            // Denuncias pendientes
            long denunciasPendientes = denunciaRepository.findByEstado(EstadoDenuncia.PENDIENTE).size();

            // Total usuarios
            long totalUsuarios = usuarioRepository.count();

            // Total coches
            long totalCoches = cocheRepository.count();

            analytics.put("alertasCriticas", alertasCriticas);
            analytics.put("denunciasPendientes", denunciasPendientes);
            analytics.put("totalUsuarios", totalUsuarios);
            analytics.put("totalCoches", totalCoches);

            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/analytics/coches-buscados")
    public ResponseEntity<?> cochesMasBuscados() {
        try {
            List<Coche> coches = cocheRepository.findAll();

            Map<String, Long> conteo = coches.stream()
                    .collect(Collectors.groupingBy(
                            c -> c.getMarca() + " " + c.getModelo(),
                            Collectors.counting()
                    ));

            var resultado = conteo.entrySet().stream()
                    .map(e -> Map.of("modelo", e.getKey(), "cantidad", e.getValue()))
                    .sorted((a, b) -> Long.compare((Long) b.get("cantidad"), (Long) a.get("cantidad")))
                    .limit(10)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/analytics/marcas-populares")
    public ResponseEntity<?> marcasPopulares() {
        try {
            Map<String, Long> conteo = cocheRepository.findAll().stream()
                    .collect(Collectors.groupingBy(Coche::getMarca, Collectors.counting()));

            var resultado = conteo.entrySet().stream()
                    .map(e -> Map.of("marca", e.getKey(), "cantidad", e.getValue()))
                    .sorted((a, b) -> Long.compare((Long) b.get("cantidad"), (Long) a.get("cantidad")))
                    .limit(10)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/analytics/exportar-csv")
    public ResponseEntity<?> exportarAnalyticsCSV() {
        try {
            StringBuilder csv = new StringBuilder();
            csv.append("Tipo,Valor\n");
            csv.append("Total Usuarios,").append(usuarioRepository.count()).append("\n");
            csv.append("Total Coches,").append(cocheRepository.count()).append("\n");
            csv.append("Denuncias Pendientes,").append(denunciaRepository.findByEstado(EstadoDenuncia.PENDIENTE).size()).append("\n");
            csv.append("Alertas Críticas,").append(alertaRepository.findByNivelRiesgoGreaterThanEqual(3).size()).append("\n");

            return ResponseEntity.ok(Map.of("csv", csv.toString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // ===== CONFIGURACIÓN =====

    @GetMapping("/configuracion")
    public ResponseEntity<?> obtenerConfiguracion() {
        try {
            Optional<ConfiguracionAdmin> config = configuracionAdminRepository.findFirstByOrderByIdDesc();
            if (config.isEmpty()) {
                Map<String, String> defaultConfig = new HashMap<>();
                defaultConfig.put("urlLogo", "/img/LogoMotorHunt.png");
                defaultConfig.put("urlBanner", "");
                defaultConfig.put("textoBienvenida", "Bienvenido a MotorHunt");
                defaultConfig.put("textoDescripcion", "");
                return ResponseEntity.ok(defaultConfig);
            }

            ConfiguracionAdmin c = config.get();
            return ResponseEntity.ok(Map.of(
                    "id", c.getId(),
                    "urlLogo", c.getUrlLogo() != null ? c.getUrlLogo() : "/img/LogoMotorHunt.png",
                    "urlBanner", c.getUrlBanner() != null ? c.getUrlBanner() : "",
                    "textoBienvenida", c.getTextoBienvenida() != null ? c.getTextoBienvenida() : "",
                    "textoDescripcion", c.getTextoDescripcion() != null ? c.getTextoDescripcion() : "",
                    "fechaActualizacion", c.getFechaActualizacion()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/configuracion")
    public ResponseEntity<?> guardarConfiguracion(@RequestBody Map<String, Object> request) {
        try {
            ConfiguracionAdmin config = new ConfiguracionAdmin();

            if (request.containsKey("id") && request.get("id") != null) {
                Long id = ((Number) request.get("id")).longValue();
                Optional<ConfiguracionAdmin> existente = configuracionAdminRepository.findById(id);
                if (existente.isPresent()) {
                    config = existente.get();
                }
            }

            config.setUrlLogo((String) request.getOrDefault("urlLogo", "/img/LogoMotorHunt.png"));
            config.setUrlBanner((String) request.getOrDefault("urlBanner", ""));
            config.setTextoBienvenida((String) request.getOrDefault("textoBienvenida", ""));
            config.setTextoDescripcion((String) request.getOrDefault("textoDescripcion", ""));

            configuracionAdminRepository.save(config);
            return ResponseEntity.ok(Map.of("mensaje", "Configuración actualizada"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping(value = "/configuracion/logo", consumes = {"multipart/form-data"})
    public ResponseEntity<?> subirLogo(@RequestParam("file") MultipartFile file,
                                      @RequestParam(required = false) Long adminId) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Archivo vacío"));
            }

            Optional<ConfiguracionAdmin> opt = configuracionAdminRepository.findFirstByOrderByIdDesc();
            ConfiguracionAdmin config = opt.orElse(new ConfiguracionAdmin());

            config.setLogoData(file.getBytes());
            config.setLogoContentType(file.getContentType());
            // Exponer una URL que sirve el logo desde este controlador
            config.setUrlLogo("/api/admin/advanced/configuracion/logo");

            if (adminId != null) {
                Optional<Usuario> admin = usuarioRepository.findById(adminId);
                admin.ifPresent(config::setAdminModificador);
            }

            configuracionAdminRepository.save(config);
            return ResponseEntity.ok(Map.of("urlLogo", config.getUrlLogo()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/configuracion/logo")
    public ResponseEntity<?> obtenerLogo() {
        try {
            Optional<ConfiguracionAdmin> opt = configuracionAdminRepository.findFirstByOrderByIdDesc();
            if (opt.isEmpty() || opt.get().getLogoData() == null) {
                // Redirigir al logo por defecto estático
                return ResponseEntity.status(HttpStatus.FOUND).header("Location", "/img/LogoMotorHunt.png").build();
            }

            ConfiguracionAdmin c = opt.get();
            MediaType mt = MediaType.APPLICATION_OCTET_STREAM;
            try { mt = MediaType.parseMediaType(c.getLogoContentType()); } catch (Exception ignored) {}

            return ResponseEntity.ok()
                    .contentType(mt)
                    .body(c.getLogoData());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping(value = "/configuracion/banner", consumes = {"multipart/form-data"})
    public ResponseEntity<?> subirBanner(@RequestParam("file") MultipartFile file,
                                        @RequestParam(required = false) Long adminId) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Archivo vacío"));
            }

            Optional<ConfiguracionAdmin> opt = configuracionAdminRepository.findFirstByOrderByIdDesc();
            ConfiguracionAdmin config = opt.orElse(new ConfiguracionAdmin());

            config.setBannerData(file.getBytes());
            config.setBannerContentType(file.getContentType());
            config.setUrlBanner("/api/admin/advanced/configuracion/banner");

            if (adminId != null) {
                Optional<Usuario> admin = usuarioRepository.findById(adminId);
                admin.ifPresent(config::setAdminModificador);
            }

            configuracionAdminRepository.save(config);
            return ResponseEntity.ok(Map.of("urlBanner", config.getUrlBanner()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/configuracion/banner")
    public ResponseEntity<?> obtenerBanner() {
        try {
            Optional<ConfiguracionAdmin> opt = configuracionAdminRepository.findFirstByOrderByIdDesc();
            if (opt.isEmpty() || opt.get().getBannerData() == null) {
                return ResponseEntity.notFound().build();
            }

            ConfiguracionAdmin c = opt.get();
            MediaType mt = MediaType.APPLICATION_OCTET_STREAM;
            try { mt = MediaType.parseMediaType(c.getBannerContentType()); } catch (Exception ignored) {}

            return ResponseEntity.ok()
                    .contentType(mt)
                    .body(c.getBannerData());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // ===== MÉTODOS AUXILIARES =====

    private Map<String, Object> mapearDenuncia(Denuncia d) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", d.getId());
        map.put("tipo", d.getTipo() != null ? d.getTipo().name() : "OTRO");
        map.put("tipoTexto", d.getTipo() != null ? d.getTipo().getDescripcion() : "Otro");
        map.put("estado", d.getEstado() != null ? d.getEstado().name() : "PENDIENTE");
        map.put("estadoTexto", d.getEstado() != null ? d.getEstado().getDescripcion() : "Pendiente de revisar");
        map.put("descripcion", d.getDescripcion());
        map.put("denunciante", d.getDenunciante() != null ? d.getDenunciante().getNombre() : "Sistema");
        map.put("denuncianteId", d.getDenunciante() != null ? d.getDenunciante().getId() : null);
        map.put("usuarioDenunciado", d.getUsuarioDenunciado() != null ? d.getUsuarioDenunciado().getNombre() : "-");
        map.put("usuarioDenunciadoId", d.getUsuarioDenunciado() != null ? d.getUsuarioDenunciado().getId() : null);
        map.put("coche", d.getCocheDenunciado() != null ?
                d.getCocheDenunciado().getMarca() + " " + d.getCocheDenunciado().getModelo() : "-");
        map.put("cocheId", d.getCocheDenunciado() != null ? d.getCocheDenunciado().getId() : null);
        map.put("objetivo", d.getUsuarioDenunciado() != null ? "USUARIO" : (d.getCocheDenunciado() != null ? "COCHE" : "DESCONOCIDO"));
        map.put("fechaCreacion", d.getFechaCreacion());
        map.put("fechaResolucion", d.getFechaResolucion());
        map.put("resolucion", d.getResolucion());
        return map;
    }

    private Map<String, Object> mapearAlerta(Alerta a) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", a.getId());
        map.put("tipo", a.getTipo().toString());
        map.put("titulo", a.getTitulo());
        map.put("descripcion", a.getDescripcion());
        map.put("nivelRiesgo", a.getNivelRiesgo());
        map.put("usuario", a.getUsuario() != null ? a.getUsuario().getNombre() : "-");
        map.put("usuarioId", a.getUsuario() != null ? a.getUsuario().getId() : null);
        map.put("coche", a.getCoche() != null ? a.getCoche().getMarca() + " " + a.getCoche().getModelo() : "-");
        map.put("cocheId", a.getCoche() != null ? a.getCoche().getId() : null);
        map.put("ipSospechosa", a.getIpSospechosa());
        map.put("resuelta", a.getResuelta());
        map.put("fechaCreacion", a.getFechaCreacion());
        map.put("fechaResolucion", a.getFechaResolucion());
        return map;
    }
}

