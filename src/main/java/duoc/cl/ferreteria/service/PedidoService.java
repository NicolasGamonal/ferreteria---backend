package duoc.cl.ferreteria.service;

import duoc.cl.ferreteria.model.DetallePedido;
import duoc.cl.ferreteria.model.Pedido;
import duoc.cl.ferreteria.model.Producto;
import duoc.cl.ferreteria.repository.PedidoRepository;
import duoc.cl.ferreteria.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> obtenerPorEmail(String email) {
        return pedidoRepository.findByEmail(email);
    }

    @Transactional
    public Pedido crearPedido(Pedido pedido) {
        if (pedido.getEstado() == null || pedido.getEstado().isEmpty()) {
            pedido.setEstado("Pendiente");
        }

        // Recorrer cada detalle del pedido para vincularlo y descontar stock
        if (pedido.getItems() != null) {
            for (DetallePedido detalle : pedido.getItems()) {
                detalle.setPedido(pedido);

                if (detalle.getProducto() != null && detalle.getProducto().getId() != null) {
                    Producto prodDB = productoRepository.findById(detalle.getProducto().getId())
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + detalle.getProducto().getId()));

                    if (prodDB.getStock() < detalle.getCantidad()) {
                        throw new RuntimeException("Stock insuficiente para: " + prodDB.getNombre());
                    }

                    // Descontar stock
                    prodDB.setStock(prodDB.getStock() - detalle.getCantidad());
                    productoRepository.save(prodDB);

                    // Asegurar el precio unitario real desde la BD
                    detalle.setProducto(prodDB);
                    detalle.setPrecioUnitario(prodDB.getPrecio());
                }
            }
        }

        return pedidoRepository.save(pedido);
    }

    public Pedido actualizarEstado(Long id, String nuevoEstado) {
        return pedidoRepository.findById(id).map(pedido -> {
            pedido.setEstado(nuevoEstado);
            return pedidoRepository.save(pedido);
        }).orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }
}