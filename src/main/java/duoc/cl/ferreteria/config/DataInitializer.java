package duoc.cl.ferreteria.config;

import duoc.cl.ferreteria.model.CuentaCorriente;
import duoc.cl.ferreteria.model.Producto;
import duoc.cl.ferreteria.model.Usuario;
import duoc.cl.ferreteria.repository.CuentaCorrienteRepository;
import duoc.cl.ferreteria.repository.ProductoRepository;
import duoc.cl.ferreteria.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final CuentaCorrienteRepository cuentaCorrienteRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           ProductoRepository productoRepository,
                           CuentaCorrienteRepository cuentaCorrienteRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.cuentaCorrienteRepository = cuentaCorrienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNombre("Admin Principal");
            admin.setEmail("admin@losmaestros.cl");
            admin.setPassword(passwordEncoder.encode("123"));
            admin.setRol("Administrador");
            admin.setActivo(true);

            Usuario vendedor = new Usuario();
            vendedor.setNombre("Carlos Vendedor");
            vendedor.setEmail("ventas@losmaestros.cl");
            vendedor.setPassword(passwordEncoder.encode("123"));
            vendedor.setRol("Vendedor");
            vendedor.setActivo(true);

            Usuario cliente = new Usuario();
            cliente.setNombre("Juan Pérez");
            cliente.setEmail("cliente@gmail.com");
            cliente.setPassword(passwordEncoder.encode("123"));
            cliente.setRol("Cliente");
            cliente.setActivo(true);

            usuarioRepository.saveAll(List.of(admin, vendedor, cliente));

            // Cuenta corriente del cliente con el límite por defecto ($300.000)
            CuentaCorriente cc = new CuentaCorriente();
            cc.setUsuario(cliente);
            cc.setCreditoDisponible(300000.0);
            cc.setSaldoPendiente(0.0);
            cc.setSaldoVencido(0.0);
            cuentaCorrienteRepository.save(cc);
        }

        if (productoRepository.count() == 0) {
            productoRepository.saveAll(List.of(
                crearProducto("LLA-001", "Llave Ajustable", "Llave ajustable de acero.", "Herramientas Manuales", "/llave_ajustable.jpg", 8590.0, 19),
                crearProducto("CIN-001", "Cinta Métrica", "Cinta métrica de 5 metros.", "Herramientas Manuales", "/cinta_metrica.jpg", 4990.0, 15),
                crearProducto("NIV-001", "Nivel de Burbuja", "Nivel de burbuja de aluminio.", "Herramientas Manuales", "/nivel_de_burbuja.jpg", 6990.0, 12),
                crearProducto("DES-001", "Set de Destornilladores", "Set de 6 destornilladores.", "Herramientas Manuales", "/caja_destornillador.jpg", 12990.0, 8),
                crearProducto("TIJ-001", "Tijeras de Corte", "Tijeras para hojalata.", "Herramientas Manuales", "/tijeras_de_corte.jpg", 8990.0, 6),
                crearProducto("MAT-001", "Cemento 25kg", "Saco de cemento de 25 kg.", "Materiales de Construcción", "/saco_de_cemento_de.jpg", 7990.0, 49),
                crearProducto("MAT-002", "Arena Fina (saco)", "Saco de arena fina.", "Materiales de Construcción", "/saco_de_arena_fina.jpg", 4990.0, 30),
                crearProducto("MAT-003", "Ladrillo de arcilla", "Ladrillo de arcilla.", "Materiales de Construcción", "/ladrillo_de_arcilla.jpg", 350.0, 500)
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
        p.setStockMinimo(5);
        return p;
    }
}