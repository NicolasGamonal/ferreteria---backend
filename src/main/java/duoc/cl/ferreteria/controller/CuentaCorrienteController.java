package duoc.cl.ferreteria.controller;

import duoc.cl.ferreteria.model.CuentaCorriente;
import duoc.cl.ferreteria.service.CuentaCorrienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuentas-corrientes")
@CrossOrigin(origins = "*")
public class CuentaCorrienteController {

    @Autowired
    private CuentaCorrienteService cuentaCorrienteService;

    @GetMapping
    public List<CuentaCorriente> listarCuentas() {
        return cuentaCorrienteService.obtenerTodas();
    }

    @GetMapping("/cliente/{email}")
    public ResponseEntity<CuentaCorriente> obtenerPorEmail(@PathVariable String email) {
        return cuentaCorrienteService.obtenerPorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CuentaCorriente crearOActualizar(@RequestBody CuentaCorriente cuenta) {
        return cuentaCorrienteService.guardar(cuenta);
    }

    @PutMapping("/{id}/abonar")
    public ResponseEntity<?> abonarSaldo(@PathVariable Long id, @RequestBody Map<String, Double> body) {
        try {
            Double monto = body.get("monto");
            return ResponseEntity.ok(cuentaCorrienteService.registrarAbono(id, monto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}