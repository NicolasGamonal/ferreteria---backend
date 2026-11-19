package duoc.cl.ferreteria.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cliente;
    private String rut;
    private String email;
    private String tipoEntrega;
    private String direccion;
    private String fechaEntrega;
    private String formaPago;
    private Double montoCredito;
    private Double total;
    private String estado;
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> items = new ArrayList<>();

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTipoEntrega() { return tipoEntrega; }
    public void setTipoEntrega(String tipoEntrega) { this.tipoEntrega = tipoEntrega; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(String fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }

    public Double getMontoCredito() { return montoCredito; }
    public void setMontoCredito(Double montoCredito) { this.montoCredito = montoCredito; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<DetallePedido> getItems() { return items; }
    public void setItems(List<DetallePedido> items) { this.items = items; }
}