package duoc.cl.ferreteria.controller;

import duoc.cl.ferreteria.model.SolicitudCredito;
import duoc.cl.ferreteria.service.SolicitudCreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/solicitudes-credito")
@CrossOrigin(origins = "*")
public class SolicitudCreditoController {

    @Autowired
    private SolicitudCreditoService solicitudService;

    // Lista todas las solicitudes (panel del administrador)
    @GetMapping
    public List<SolicitudCredito> listar() {
        return solicitudService.obtenerTodas();
    }

    // Solicitudes de un cliente
    @GetMapping("/usuario/{usuarioId}")
    public List<SolicitudCredito> listarPorUsuario(@PathVariable Long usuarioId) {
        return solicitudService.obtenerPorUsuario(usuarioId);
    }

    // El cliente crea una solicitud de aumento
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody SolicitudCredito solicitud) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(solicitudService.crear(solicitud));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // El administrador responde: body { "accion": "Aprobada" } o { "accion": "Rechazada" }
    @PutMapping("/{id}/responder")
    public ResponseEntity<?> responder(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(solicitudService.responder(id, body.get("accion")));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}