package duoc.cl.ferreteria.service;

import duoc.cl.ferreteria.model.CuentaCorriente;
import duoc.cl.ferreteria.model.Usuario;
import duoc.cl.ferreteria.repository.CuentaCorrienteRepository;
import duoc.cl.ferreteria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CuentaCorrienteRepository cuentaCorrienteRepository;

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("Cliente");
        }
        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }

        Usuario guardado = usuarioRepository.save(usuario);

        // Crea automáticamente la cuenta corriente para cada cliente nuevo ($300.000 iniciales)
        if ("Cliente".equalsIgnoreCase(guardado.getRol())) {
            CuentaCorriente cc = new CuentaCorriente();
            cc.setUsuario(guardado);
            cc.setCreditoDisponible(300000.0);
            cc.setSaldoPendiente(0.0);
            cc.setSaldoVencido(0.0);
            cuentaCorrienteRepository.save(cc);
        }

        return guardado;
    }

    public Optional<Usuario> login(String email, String password) {
        return usuarioRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password))
                .filter(u -> u.getActivo() == null || u.getActivo());
    }

    public Usuario actualizarUsuario(Long id, Usuario detalles) {
        return usuarioRepository.findById(id).map(u -> {
            u.setNombre(detalles.getNombre());
            u.setEmail(detalles.getEmail());
            if (detalles.getPassword() != null && !detalles.getPassword().isEmpty()) {
                u.setPassword(detalles.getPassword());
            }
            if (detalles.getRol() != null) {
                u.setRol(detalles.getRol());
            }
            if (detalles.getActivo() != null) {
                u.setActivo(detalles.getActivo());
            }
            return usuarioRepository.save(u);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario cambiarEstadoActivo(Long id, Boolean activo) {
        return usuarioRepository.findById(id).map(u -> {
            u.setActivo(activo);
            return usuarioRepository.save(u);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.findById(id).ifPresent(u ->
            cuentaCorrienteRepository.findByUsuarioEmail(u.getEmail())
                .ifPresent(cc -> cuentaCorrienteRepository.delete(cc))
        );
        usuarioRepository.deleteById(id);
    }
}