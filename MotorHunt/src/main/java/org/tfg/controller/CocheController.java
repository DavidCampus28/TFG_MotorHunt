package org.tfg.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tfg.model.entities.Coche;
import org.tfg.model.entities.CocheFoto;
import org.tfg.model.entities.Usuario;
import org.tfg.model.enums.*;
import org.tfg.repository.CocheRepository;
import org.tfg.repository.CocheFotoRepository;
import org.tfg.repository.UsuarioRepository;
import org.tfg.repository.MeGustaRepository;
import org.tfg.dto.CocheDetalleResponse;
import org.tfg.dto.CocheFotoResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/coches")
@CrossOrigin(origins = "*")
@Slf4j
public class CocheController {

    @Autowired
    private CocheRepository cocheRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MeGustaRepository meGustaRepository;

    @Autowired
    private CocheFotoRepository cocheFotoRepository;

    @GetMapping
    public List<Coche> getAllCoches() {
        // NO mostrar coches eliminados (FUERA_SERVICIO) en el listado público
        return cocheRepository.findAll().stream()
                .filter(coche -> coche.getEstado() != EstadoCoche.FUERA_SERVICIO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coche> getCocheById(@PathVariable Long id) {
        return cocheRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/detalle/{id}")
    public ResponseEntity<?> getCocheDetalle(@PathVariable Long id, @RequestParam(required = false) Long usuarioId) {
        try {
            Optional<Coche> cocheOpt = cocheRepository.findById(id);
            if (cocheOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Coche coche = cocheOpt.get();
            Usuario vendedor = coche.getUsuario();

            CocheDetalleResponse response = new CocheDetalleResponse();
            response.setId(coche.getId());
            response.setMarca(coche.getMarca());
            response.setModelo(coche.getModelo());
            response.setMotor(coche.getMotor());
            response.setColor(coche.getColor());
            response.setCaballosPotencia(coche.getCaballosPotencia());
            response.setKilometros(coche.getKilometros());
            response.setPrecio(coche.getPrecio());
            response.setAno(coche.getAno());
            response.setCombustible(coche.getCombustible().toString());
            response.setTipoCambio(coche.getTipoCambio().toString());
            response.setNumeroPuertas(coche.getNumeroPuertas());
            response.setNumeroPlazas(coche.getNumeroPlazas());
            response.setCentimetrosCubicos(coche.getCentimetrosCubicos());
            response.setEtiquetaAmbiental(coche.getEtiquetaAmbiental().toString());
            response.setEstado(coche.getEstado().toString());
            response.setDescripcion(coche.getDescripcion());
            response.setUbicacion(coche.getUbicacion());

            // Información del vendedor
            response.setVendedorId(vendedor.getId());
            response.setVendedorNombre(vendedor.getNombre());
            response.setVendedorEmail(vendedor.getEmail());
            response.setVendedorTelefono(vendedor.getTelefono());
            response.setVendedorDireccion(vendedor.getDireccion());
            response.setVendedorRol(vendedor.getRol().toString());
            response.setVendedorTipoVendedor(vendedor.getTipoVendedorLegible());
            response.setVendedorNumeroDenuncias(vendedor.getNumeroDenuncias() == null ? 0 : vendedor.getNumeroDenuncias());
            response.setVendedorBloqueado(Boolean.TRUE.equals(vendedor.getBloqueado()));
            response.setVendedorMotivoBloqueado(vendedor.getMotivoBloqueado());

            response.setNumeroDenuncias(coche.getNumeroDenuncias() == null ? 0 : coche.getNumeroDenuncias());
            response.setBloqueado(Boolean.TRUE.equals(coche.getBloqueado()));
            response.setMotivoBloqueado(coche.getMotivoBloqueado());

            // Información de me gusta
            if (usuarioId != null) {
                Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
                if (usuarioOpt.isPresent()) {
                    response.setTieneMemGusta(meGustaRepository.existsByUsuarioAndCoche(usuarioOpt.get(), coche));
                }
            }

            response.setTotalMeGustas(meGustaRepository.countByCoche(coche));

            List<CocheFoto> fotos = cocheFotoRepository.findByCocheOrderByOrdenAsc(coche);
            response.setFotos(fotos.stream().map(foto -> new CocheFotoResponse(
                    foto.getId(),
                    foto.getNombreArchivo(),
                    Boolean.TRUE.equals(foto.getPortada()),
                    foto.getOrden(),
                    "/coches/fotos/" + foto.getId()
            )).collect(Collectors.toList()));

            String portadaUrl = cocheFotoRepository.findByCocheAndPortadaTrue(coche)
                    .or(() -> cocheFotoRepository.findFirstByCocheOrderByOrdenAsc(coche))
                    .map(foto -> "/coches/fotos/" + foto.getId())
                    .orElse(null);
            response.setFotoPortadaUrl(portadaUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Map<String, Object>> getCochesByUsuario(@PathVariable Long usuarioId) {
        return cocheRepository.findByUsuarioId(usuarioId).stream().map(coche -> {
            String portadaUrl = cocheFotoRepository.findByCocheAndPortadaTrue(coche)
                    .or(() -> cocheFotoRepository.findFirstByCocheOrderByOrdenAsc(coche))
                    .map(foto -> "/coches/fotos/" + foto.getId())
                    .orElse(null);

            Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", coche.getId());
            item.put("marca", coche.getMarca());
            item.put("modelo", coche.getModelo());
            item.put("motor", coche.getMotor());
            item.put("color", coche.getColor());
            item.put("tipoCambio", coche.getTipoCambio().toString());
            item.put("combustible", coche.getCombustible().toString());
            item.put("numeroPuertas", coche.getNumeroPuertas());
            item.put("ubicacion", coche.getUbicacion());
            item.put("caballosPotencia", coche.getCaballosPotencia());
            item.put("ano", coche.getAno());
            item.put("kilometros", coche.getKilometros());
            item.put("precio", coche.getPrecio());
            item.put("numeroPlazas", coche.getNumeroPlazas());
            item.put("centimetrosCubicos", coche.getCentimetrosCubicos());
            item.put("etiquetaAmbiental", coche.getEtiquetaAmbiental().toString());
            item.put("descripcion", coche.getDescripcion());
            item.put("estado", coche.getEstado().toString());
            item.put("portadaUrl", portadaUrl);
            return item;
        }).toList();
    }

    @PostMapping
    public Coche createCoche(@RequestBody Coche coche) {
        return cocheRepository.save(coche);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coche> updateCoche(@PathVariable Long id, @RequestBody Coche cocheDetails) {
        return cocheRepository.findById(id).map(coche -> {
            coche.setMarca(cocheDetails.getMarca());
            coche.setModelo(cocheDetails.getModelo());
            coche.setMotor(cocheDetails.getMotor());
            coche.setColor(cocheDetails.getColor());
            coche.setTipoCambio(cocheDetails.getTipoCambio());
            coche.setCombustible(cocheDetails.getCombustible());
            coche.setNumeroPuertas(cocheDetails.getNumeroPuertas());
            coche.setUbicacion(cocheDetails.getUbicacion());
            coche.setCaballosPotencia(cocheDetails.getCaballosPotencia());
            coche.setKilometros(cocheDetails.getKilometros());
            coche.setPrecio(cocheDetails.getPrecio());
            coche.setNumeroPlazas(cocheDetails.getNumeroPlazas());
            coche.setCentimetrosCubicos(cocheDetails.getCentimetrosCubicos());
            coche.setEtiquetaAmbiental(cocheDetails.getEtiquetaAmbiental());
            coche.setUsuario(cocheDetails.getUsuario());
            if (cocheDetails.getEstado() != null) {
                coche.setEstado(cocheDetails.getEstado());
                if (cocheDetails.getEstado() == EstadoCoche.VENDIDO) {
                    coche.setFechaVenta(LocalDateTime.now());
                }
            }
            return ResponseEntity.ok(cocheRepository.save(coche));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        try {
            Optional<Coche> cocheOpt = cocheRepository.findById(id);
            if (cocheOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Coche coche = cocheOpt.get();
            EstadoCoche nuevoEstado = EstadoCoche.valueOf(estado.trim().toUpperCase());
            coche.setEstado(nuevoEstado);
            if (nuevoEstado == EstadoCoche.VENDIDO) {
                coche.setFechaVenta(LocalDateTime.now());
            } else {
                coche.setFechaVenta(null);
            }
            cocheRepository.save(coche);
            return ResponseEntity.ok(Map.of("mensaje", "Estado actualizado", "estado", coche.getEstado().toString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/editar")
    public ResponseEntity<?> editarCoche(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<Coche> cocheOpt = cocheRepository.findById(id);
            if (cocheOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Coche coche = cocheOpt.get();
            coche.setMarca((String) request.get("marca"));
            coche.setModelo((String) request.get("modelo"));
            coche.setMotor((String) request.get("motor"));
            coche.setColor((String) request.get("color"));
            coche.setTipoCambio(TipoCambio.valueOf(request.get("tipoCambio").toString()));
            coche.setCombustible(Combustible.valueOf(request.get("combustible").toString()));
            coche.setUbicacion((String) request.get("ubicacion"));
            coche.setEtiquetaAmbiental(EtiquetaAmbiental.valueOf(request.get("etiquetaAmbiental").toString()));
            coche.setDescripcion((String) request.get("descripcion"));

            if (request.get("numeroPuertas") != null) {
                coche.setNumeroPuertas(((Number) request.get("numeroPuertas")).intValue());
            }
            if (request.get("caballosPotencia") != null) {
                coche.setCaballosPotencia(((Number) request.get("caballosPotencia")).intValue());
            }
            if (request.get("km") != null) {
                coche.setKilometros(((Number) request.get("km")).intValue());
            }
            if (request.get("precio") != null) {
                coche.setPrecio(((Number) request.get("precio")).doubleValue());
            }
            if (request.get("numeroPlazas") != null) {
                coche.setNumeroPlazas(((Number) request.get("numeroPlazas")).intValue());
            }
            if (request.get("centimetrosCubicos") != null) {
                coche.setCentimetrosCubicos(((Number) request.get("centimetrosCubicos")).intValue());
            }
            if (request.get("ano") != null) {
                coche.setAno(((Number) request.get("ano")).intValue());
            }
            if (request.get("estado") != null) {
                EstadoCoche nuevoEstado = EstadoCoche.valueOf(request.get("estado").toString());
                coche.setEstado(nuevoEstado);
                if (nuevoEstado == EstadoCoche.VENDIDO) {
                    coche.setFechaVenta(LocalDateTime.now());
                } else {
                    coche.setFechaVenta(null);
                }
            }

            cocheRepository.save(coche);
            return ResponseEntity.ok(Map.of("mensaje", "Coche actualizado", "id", coche.getId()));
        } catch (Exception e) {
            log.error("Error editando coche", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage(), "tipo", e.getClass().getSimpleName()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCoche(@PathVariable Long id) {
        return cocheRepository.findById(id).map(coche -> {
            // Soft delete: marcar como FUERA_SERVICIO en lugar de borrar físicamente
            coche.setEstado(EstadoCoche.FUERA_SERVICIO);
            coche.setFechaActualizacion(LocalDateTime.now());
            cocheRepository.save(coche);
            return ResponseEntity.ok(Map.of("mensaje", "Anuncio eliminado correctamente"));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearCoche(@RequestBody Map<String, Object> request) {
        try {
            Coche coche = new Coche();
            coche.setMarca((String) request.get("marca"));
            coche.setModelo((String) request.get("modelo"));
            coche.setMotor((String) request.get("motor"));
            coche.setColor((String) request.get("color"));
            coche.setTipoCambio(TipoCambio.valueOf(request.get("tipoCambio").toString()));
            coche.setCombustible(Combustible.valueOf(request.get("combustible").toString()));
            
            if (request.get("numeroPuertas") != null) {
                coche.setNumeroPuertas(((Number) request.get("numeroPuertas")).intValue());
            }
            coche.setUbicacion((String) request.get("ubicacion"));
            if (request.get("caballosPotencia") != null) {
                coche.setCaballosPotencia(((Number) request.get("caballosPotencia")).intValue());
            }
            if (request.get("km") != null) {
                coche.setKilometros(((Number) request.get("km")).intValue());
            }
            if (request.get("precio") != null) {
                coche.setPrecio(((Number) request.get("precio")).doubleValue());
            }
            if (request.get("numeroPlazas") != null) {
                coche.setNumeroPlazas(((Number) request.get("numeroPlazas")).intValue());
            }
            if (request.get("centimetrosCubicos") != null) {
                coche.setCentimetrosCubicos(((Number) request.get("centimetrosCubicos")).intValue());
            }
            coche.setEtiquetaAmbiental(EtiquetaAmbiental.valueOf(request.get("etiquetaAmbiental").toString()));
            coche.setEstado(EstadoCoche.EN_VENTA);
            coche.setDescripcion((String) request.get("descripcion"));
            if (request.get("ano") != null) {
                coche.setAno(((Number) request.get("ano")).intValue());
            }

            Long usuarioId = Long.parseLong(request.get("usuarioId").toString());
            Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
            if (usuario.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Usuario no encontrado"));
            }

            coche.setUsuario(usuario.get());
            coche.setFechaCreacion(LocalDateTime.now());

            cocheRepository.save(coche);
            return ResponseEntity.ok(Map.of("mensaje", "Coche creado", "id", coche.getId()));
        } catch (Exception e) {
            log.error("Error creando coche", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage(), "tipo", e.getClass().getSimpleName()));
        }
    }

    @PostMapping(value = "/{cocheId}/fotos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirFotos(
            @PathVariable Long cocheId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "portadaIndex", required = false, defaultValue = "0") Integer portadaIndex) {
        try {
            Optional<Coche> cocheOpt = cocheRepository.findById(cocheId);
            if (cocheOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Coche no encontrado"));
            }

            Coche coche = cocheOpt.get();
            if (files == null || files.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No hay fotos"));
            }

            int ordenBase = cocheFotoRepository.findByCocheOrderByOrdenAsc(coche).size();
            int fotosGuardadas = 0;
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                if (file.isEmpty()) continue;

                CocheFoto foto = new CocheFoto();
                foto.setCoche(coche);
                foto.setNombreArchivo(file.getOriginalFilename() != null ? file.getOriginalFilename() : "foto-" + i);
                foto.setContentType(file.getContentType());
                foto.setContenido(file.getBytes());
                foto.setOrden(ordenBase + fotosGuardadas);
                foto.setPortada(i == portadaIndex);

                if (Boolean.TRUE.equals(foto.getPortada())) {
                    cocheFotoRepository.findByCocheOrderByOrdenAsc(coche).forEach(existing -> {
                        if (Boolean.TRUE.equals(existing.getPortada())) {
                            existing.setPortada(false);
                            cocheFotoRepository.save(existing);
                        }
                    });
                }

                cocheFotoRepository.save(foto);
                fotosGuardadas++;
            }

            normalizarPortada(coche);
            return ResponseEntity.ok(Map.of("mensaje", "Fotos subidas"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{cocheId}/fotos")
    public ResponseEntity<?> listarFotos(@PathVariable Long cocheId) {
        Optional<Coche> cocheOpt = cocheRepository.findById(cocheId);
        if (cocheOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<CocheFotoResponse> fotos = cocheFotoRepository.findByCocheOrderByOrdenAsc(cocheOpt.get()).stream()
                .map(foto -> new CocheFotoResponse(
                        foto.getId(),
                        foto.getNombreArchivo(),
                        Boolean.TRUE.equals(foto.getPortada()),
                        foto.getOrden(),
                        "/coches/fotos/" + foto.getId()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(fotos);
    }

    @PutMapping("/fotos/{fotoId}/portada")
    public ResponseEntity<?> marcarPortada(@PathVariable Long fotoId) {
        Optional<CocheFoto> fotoOpt = cocheFotoRepository.findById(fotoId);
        if (fotoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CocheFoto nuevaPortada = fotoOpt.get();
        cocheFotoRepository.findByCocheOrderByOrdenAsc(nuevaPortada.getCoche()).forEach(foto -> {
            foto.setPortada(foto.getId().equals(fotoId));
            cocheFotoRepository.save(foto);
        });

        return ResponseEntity.ok(Map.of("mensaje", "Portada actualizada"));
    }

    @PutMapping("/{cocheId}/fotos/orden")
    public ResponseEntity<?> actualizarOrdenFotos(@PathVariable Long cocheId, @RequestBody Map<String, Object> request) {
        Optional<Coche> cocheOpt = cocheRepository.findById(cocheId);
        if (cocheOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Object ordenObj = request.get("orden");
        if (!(ordenObj instanceof List<?> ordenIds)) {
            return ResponseEntity.badRequest().body(Map.of("error", "El campo orden debe ser una lista de IDs"));
        }

        Coche coche = cocheOpt.get();
        List<CocheFoto> fotos = cocheFotoRepository.findByCocheOrderByOrdenAsc(coche);
        Map<Long, CocheFoto> fotosPorId = fotos.stream().collect(Collectors.toMap(CocheFoto::getId, foto -> foto));

        for (int i = 0; i < ordenIds.size(); i++) {
            Long fotoId = Long.parseLong(ordenIds.get(i).toString());
            CocheFoto foto = fotosPorId.get(fotoId);
            if (foto == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "La foto " + fotoId + " no pertenece al coche"));
            }
            foto.setOrden(i);
            cocheFotoRepository.save(foto);
        }

        normalizarPortada(coche);
        return ResponseEntity.ok(Map.of("mensaje", "Orden actualizado"));
    }

    @DeleteMapping("/fotos/{fotoId}")
    public ResponseEntity<?> eliminarFoto(@PathVariable Long fotoId) {
        Optional<CocheFoto> fotoOpt = cocheFotoRepository.findById(fotoId);
        if (fotoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CocheFoto foto = fotoOpt.get();
        Coche coche = foto.getCoche();
        boolean eraPortada = Boolean.TRUE.equals(foto.getPortada());
        cocheFotoRepository.delete(foto);

        List<CocheFoto> restantes = cocheFotoRepository.findByCocheOrderByOrdenAsc(coche).stream()
                .filter(restante -> !restante.getId().equals(fotoId))
                .collect(Collectors.toList());
        for (int i = 0; i < restantes.size(); i++) {
            CocheFoto restante = restantes.get(i);
            restante.setOrden(i);
            if (eraPortada) {
                restante.setPortada(i == 0);
            }
            cocheFotoRepository.save(restante);
        }

        return ResponseEntity.ok(Map.of("mensaje", "Foto eliminada"));
    }

    @GetMapping("/fotos/{fotoId}")
    public ResponseEntity<byte[]> getFoto(@PathVariable Long fotoId) {
        Optional<CocheFoto> fotoOpt = cocheFotoRepository.findById(fotoId);
        if (fotoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CocheFoto foto = fotoOpt.get();
        MediaType mediaType = (foto.getContentType() != null && !foto.getContentType().isBlank())
                ? MediaType.parseMediaType(foto.getContentType())
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(foto.getContenido());
    }

    private void normalizarPortada(Coche coche) {
        List<CocheFoto> fotos = cocheFotoRepository.findByCocheOrderByOrdenAsc(coche);
        if (fotos.isEmpty() || fotos.stream().anyMatch(foto -> Boolean.TRUE.equals(foto.getPortada()))) {
            return;
        }

        CocheFoto primera = fotos.get(0);
        primera.setPortada(true);
        cocheFotoRepository.save(primera);
    }
}
