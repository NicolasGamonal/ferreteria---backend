package duoc.cl.ferreteria.model;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Solicitud de aumento del límite de crédito de un cliente.
 * Estados posibles: "Pendiente" -> "Aprobada" o "Rechazada".
 */
@Entity
@Table(name = "solicitudes_credito")
public class SolicitudCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Id del usuario (cliente) que hace la solicitud
    private Long usuarioId;

    private String nombre;

    // Límite que tenía el cliente al momento de solicitar
    private Double montoActual;

    // Nuevo límite que desea el cliente
    private Double montoSolicitado;

    private String estado;

    private LocalDate fecha;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getMontoActual() { return montoActual; }
    public void setMontoActual(Double montoActual) { this.montoActual = montoActual; }

    public Double getMontoSolicitado() { return montoSolicitado; }
    public void setMontoSolicitado(Double montoSolicitado) { this.montoSolicitado = montoSolicitado; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}