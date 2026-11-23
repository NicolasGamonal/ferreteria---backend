package duoc.cl.ferreteria.config;

import duoc.cl.ferreteria.model.CuentaCorriente;
import duoc.cl.ferreteria.model.Producto;
import duoc.cl.ferreteria.model.Usuario;
import duoc.cl.ferreteria.repository.CuentaCorrienteRepository;
import duoc.cl.ferreteria.repository.ProductoRepository;
import duoc.cl.ferreteria.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final CuentaCorrienteRepository cuentaCorrienteRepository;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           ProductoRepository productoRepository,
                           CuentaCorrienteRepository cuentaCorrienteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.cuentaCorrienteRepository = cuentaCorrienteRepository;
    }

    @Override
    public void run(String... args) {
        // 1. Usuarios de prueba (ADMIN, VENDEDOR, CLIENTE)
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNombre("Don Juan (Administrador)");
            admin.setEmail("admin@ferreteria.cl");
            admin.setPassword("admin123");
            admin.setRol("ADMIN");

            Usuario vendedor = new Usuario();
            vendedor.setNombre("Carlos Vendedor");
            vendedor.setEmail("vendedor@ferreteria.cl");
            vendedor.setPassword("vendedor123");
            vendedor.setRol("VENDEDOR");

            Usuario cliente = new Usuario();
            cliente.setNombre("Pedro Maestro Contratista");
            cliente.setEmail("cliente@ferreteria.cl");
            cliente.setPassword("cliente123");
            cliente.setRol("CLIENTE");

            usuarioRepository.saveAll(List.of(admin, vendedor, cliente));

            // 2. Cuenta corriente asociada al cliente contratista
            CuentaCorriente cc = new CuentaCorriente();
            cc.setUsuario(cliente);
            cc.setCreditoDisponible(500000.0);
            cc.setSaldoPendiente(125000.0);
            cc.setSaldoVencido(0.0);
            cuentaCorrienteRepository.save(cc);
        }

        // 3. Catálogo inicial con las imágenes de ferreteria-don-juan-react/public
        if (productoRepository.count() == 0) {
            productoRepository.saveAll(List.of(
                crearProducto("HER-001", "Caja de Destornilladores", "Set de destornilladores de precisión y fuerza", "Herramientas", "/caja_destornillador.jpg", 14990.0, 25),
                crearProducto("HER-002", "Cinta Métrica 5m", "Huincha de medir reforzada con freno", "Herramientas", "/cinta_metrica.jpg", 5990.0, 40),
                crearProducto("HER-003", "Llave Ajustable 10\"", "Llave francesa de acero cromado", "Herramientas", "/llave_ajustable.jpg", 9490.0, 18),
                crearProducto("HER-004", "Nivel de Burbuja 60cm", "Nivel de aluminio para obra gruesa y terminaciones", "Herramientas", "/nivel_de_burbuja.jpg", 11990.0, 12),
                crearProducto("HER-005", "Tijeras de Corte Lata", "Tijera hojalatera mango ergonómico", "Herramientas", "/tijeras_de_corte.jpg", 8990.0, 4),
                crearProducto("MAT-001", "Saco de Cemento 25kg", "Cemento especial de alta resistencia para construcción", "Materiales", "/saco_de_cemento_de.jpg", 4890.0, 120),
                crearProducto("MAT-002", "Saco de Arena Fina 25kg", "Arena fina tamizada para estuco y mortero", "Materiales", "/saco_de_arena_fina.jpg", 3290.0, 80),
                crearProducto("MAT-003", "Ladrillo de Arcilla Fiscal", "Ladrillo estructural de arcilla cocida", "Materiales", "/ladrillo_de_arcilla.jpg", 650.0, 500)
            ));
        }
    }

    private Producto crearProducto(String codigo, String nombre, String descripcion,
                                   String categoria, String imagen, Double precio, Integer stock) {
        Producto p = new Producto();
        p.setCodigo(codigo);
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setCategoria(categoria);
        p.setImagen(imagen);
        p.setPrecio(precio);
        p.setStock(stock);
        return p;
    }
}