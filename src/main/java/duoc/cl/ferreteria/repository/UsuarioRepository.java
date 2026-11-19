package duoc.cl.ferreteria.repository;

import duoc.cl.ferreteria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Método clave para la futura autenticación JWT
    Optional<Usuario> findByEmail(String email);
}