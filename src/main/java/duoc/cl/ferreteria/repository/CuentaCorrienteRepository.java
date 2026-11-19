package duoc.cl.ferreteria.repository;

import duoc.cl.ferreteria.model.CuentaCorriente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CuentaCorrienteRepository extends JpaRepository<CuentaCorriente, Long> {
    Optional<CuentaCorriente> findByUsuarioId(Long usuarioId);
    Optional<CuentaCorriente> findByUsuarioEmail(String email);
}