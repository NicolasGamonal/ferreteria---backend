package duoc.cl.ferreteria.service;

import duoc.cl.ferreteria.model.CuentaCorriente;
import duoc.cl.ferreteria.model.DetallePedido;
import duoc.cl.ferreteria.model.Pedido;
import duoc.cl.ferreteria.model.Producto;
import duoc.cl.ferreteria.repository.CuentaCorrienteRepository;
import duoc.cl.ferreteria.repository.PedidoRepository;
import duoc.cl.ferreteria.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CuentaCorrienteRepository cuentaCorrienteRepository;

    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> obtenerPorEmail(String email) {
        return pedidoRepository.findByEmail(email);
    }

    @Transactional
    public Pedido crearPedido(Pedido pedido) {
        double totalCalculado = 0.0;

        // 1. Validar y descontar stock de cada ítem
        if (pedido.getItems() != null) {
            for (DetallePedido detalle : pedido.getItems()) {
                detalle.setPedido(pedido);

                if (detalle.getProducto() != null && detalle.getProducto().getId() != null) {
                    Producto prodDB = productoRepository.findById(detalle.getProducto().getId())
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + detalle.getProducto().getId()));

                    if (prodDB.getStock() < detalle.getCantidad()) {
                        throw new RuntimeException("Stock insuficiente para: " + prodDB.getNombre());
                    }

                    prodDB.setStock(prodDB.getStock() - detalle.getCantidad());
                    productoRepository.save(prodDB);

                    detalle.setProducto(prodDB);
                    detalle.setPrecioUnitario(prodDB.getPrecio());
                    totalCalculado += prodDB.getPrecio() * detalle.getCantidad();
                }
            }
        }

        if (pedido.getTotal() == null || pedido.getTotal() == 0) {
            pedido.setTotal(totalCalculado);
        }

        // 2. Lógica de Cuenta Corriente ("Pago pendiente a fin de mes")
        boolean usaCuentaCorriente = (pedido.getMontoCredito() != null && pedido.getMontoCredito() > 0)
                || (pedido.getFormaPago() != null && pedido.getFormaPago().toLowerCase().contains("cuenta"));

        if (usaCuentaCorriente && pedido.getEmail() != null) {
            double montoACargar = (pedido.getMontoCredito() != null && pedido.getMontoCredito() > 0)
                    ? pedido.getMontoCredito()
                    : pedido.getTotal();

            CuentaCorriente cc = cuentaCorrienteRepository.findByUsuarioEmail(pedido.getEmail())
                    .orElseThrow(() -> new RuntimeException("El cliente no posee cuenta corriente activa"));

            if (cc.getCreditoDisponible() < montoACargar) {
                throw new RuntimeException("Crédito disponible insuficiente en la cuenta corriente");
            }

            cc.setCreditoDisponible(cc.getCreditoDisponible() - montoACargar);
            cc.setSaldoPendiente(cc.getSaldoPendiente() + montoACargar);
            cuentaCorrienteRepository.save(cc);

            pedido.setMontoCredito(montoACargar);
            pedido.setEstado("Pago pendiente a fin de mes");
        } else if (pedido.getEstado() == null || pedido.getEstado().isEmpty()) {
            pedido.setEstado("Pendiente");
        }

        return pedidoRepository.save(pedido);
    }

    public Pedido actualizarEstado(Long id, String nuevoEstado) {
        return pedidoRepository.findById(id).map(pedido -> {
            pedido.setEstado(nuevoEstado);
            return pedidoRepository.save(pedido);
        }).orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    public void eliminarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }
}