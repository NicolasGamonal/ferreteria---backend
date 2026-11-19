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

    // Lectura (Todos)
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    // Lectura (Por ID)
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    // Creación
    public Producto guardarProducto(Producto producto) {
        // Aquí podrías agregar validaciones de negocio (ej. stock mayor a 0)
        return productoRepository.save(producto);
    }

    // Actualización
    public Producto actualizarProducto(Long id, Producto productoDetalles) {
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(productoDetalles.getNombre());
            producto.setDescripcion(productoDetalles.getDescripcion());
            producto.setPrecio(productoDetalles.getPrecio());
            producto.setStock(productoDetalles.getStock());
            return productoRepository.save(producto);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    // Eliminación
    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }
}
