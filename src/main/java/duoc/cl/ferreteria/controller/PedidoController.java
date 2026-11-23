package duoc.cl.ferreteria.controller;

import duoc.cl.ferreteria.model.Pedido;
import duoc.cl.ferreteria.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoService.obtenerTodos();
    }

    @GetMapping("/cliente/{email}")
    public List<Pedido> listarPedidosPorCliente(@PathVariable String email) {
        return pedidoService.obtenerPorEmail(email);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody Pedido pedido) {
        try {
            Pedido nuevoPedido = pedidoService.crearPedido(pedido);
            return ResponseEntity.ok(nuevoPedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String estado = body.get("estado");
            return ResponseEntity.ok(pedidoService.actualizarEstado(id, estado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}