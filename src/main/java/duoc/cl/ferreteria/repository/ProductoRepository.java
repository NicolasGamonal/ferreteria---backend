package duoc.cl.ferreteria.repository;

import duoc.cl.ferreteria.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}