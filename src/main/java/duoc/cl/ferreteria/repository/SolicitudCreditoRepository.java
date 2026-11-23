package duoc.cl.ferreteria.repository;

import duoc.cl.ferreteria.model.SolicitudCredito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudCreditoRepository extends JpaRepository<SolicitudCredito, Long> {
    List<SolicitudCredito> findByUsuarioIdOrderByIdDesc(Long usuarioId);
}