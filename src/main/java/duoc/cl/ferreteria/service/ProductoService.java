package duoc.cl.ferreteria.service;

import duoc.cl.ferreteria.model.Producto;
import duoc.cl.ferreteria.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    // Alerta de reposición: productos cuyo stock llegó al mínimo
    public List<Producto> obtenerStockBajo() {
        return productoRepository.findAll().stream()
                .filter(p -> p.getStock() != null && p.getStock() <= (p.getStockMinimo() != null ? p.getStockMinimo() : 5))
                .toList();
    }

    public Producto guardarProducto(Producto producto) {
        if (producto.getStockMinimo() == null) {
            producto.setStockMinimo(5);
        }
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto productoDetalles) {
        return productoRepository.findById(id).map(producto -> {
            producto.setCodigo(productoDetalles.getCodigo());
            producto.setNombre(productoDetalles.getNombre());
            producto.setDescripcion(productoDetalles.getDescripcion());
            producto.setCategoria(productoDetalles.getCategoria());
            producto.setImagen(productoDetalles.getImagen());
            producto.setPrecio(productoDetalles.getPrecio());
            producto.setStock(productoDetalles.getStock());
            if (productoDetalles.getStockMinimo() != null) {
                producto.setStockMinimo(productoDetalles.getStockMinimo());
            }
            return productoRepository.save(producto);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }
}