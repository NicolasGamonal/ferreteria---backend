package duoc.cl.ferreteria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cuentas_corrientes")
public class CuentaCorriente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double creditoDisponible;
    private Double saldoPendiente;
    private Double saldoVencido;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getCreditoDisponible() { return creditoDisponible; }
    public void setCreditoDisponible(Double creditoDisponible) { this.creditoDisponible = creditoDisponible; }

    public Double getSaldoPendiente() { return saldoPendiente; }
    public void setSaldoPendiente(Double saldoPendiente) { this.saldoPendiente = saldoPendiente; }

    public Double getSaldoVencido() { return saldoVencido; }
    public void setSaldoVencido(Double saldoVencido) { this.saldoVencido = saldoVencido; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}