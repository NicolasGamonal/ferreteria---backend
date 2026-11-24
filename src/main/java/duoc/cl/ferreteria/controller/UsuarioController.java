package duoc.cl.ferreteria.controller;

import duoc.cl.ferreteria.model.Usuario;
import duoc.cl.ferreteria.security.JwtService;
import duoc.cl.ferreteria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            return ResponseEntity.ok(usuarioService.registrar(usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String password = credenciales.get("password");

        return usuarioService.login(email, password)
                .<ResponseEntity<?>>map(usuario -> {
                    String token = jwtService.generarAccessToken(usuario.getEmail(), usuario.getRol());
                    String refreshToken = jwtService.generarRefreshToken(usuario.getEmail(), usuario.getRol());

                    return ResponseEntity.ok(Map.of(
                            "token", token,
                            "refreshToken", refreshToken,
                            "usuario", usuario
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(401)
                        .body(Map.of("mensaje", "Credenciales incorrectas o usuario inactivo")));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        if (refreshToken == null || !jwtService.esTokenValido(refreshToken, "REFRESH")) {
            return ResponseEntity.status(401)
                    .body(Map.of("mensaje", "Refresh token inválido o expirado"));
        }

        String email = jwtService.extraerEmail(refreshToken);
        String rol = jwtService.extraerRol(refreshToken);
        String nuevoAccessToken = jwtService.generarAccessToken(email, rol);

        return ResponseEntity.ok(Map.of("token", nuevoAccessToken));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            return ResponseEntity.ok(usuarioService.actualizarUsuario(id, usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        try {
            return ResponseEntity.ok(usuarioService.cambiarEstadoActivo(id, body.get("activo")));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}