package duoc.cl.ferreteria.service;

import duoc.cl.ferreteria.model.Usuario;
import duoc.cl.ferreteria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("Cliente");
        }
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> login(String email, String password) {
        return usuarioRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password));
    }
}