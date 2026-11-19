package duoc.cl.ferreteria.repository;

import duoc.cl.ferreteria.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Permite consultar el historial de pedidos de un cliente por su correo
    List<Pedido> findByEmail(String email);
}