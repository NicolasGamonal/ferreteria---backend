package duoc.cl.ferreteria.service;

import duoc.cl.ferreteria.model.CuentaCorriente;
import duoc.cl.ferreteria.model.SolicitudCredito;
import duoc.cl.ferreteria.repository.CuentaCorrienteRepository;
import duoc.cl.ferreteria.repository.SolicitudCreditoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SolicitudCreditoService {

    public static final String PENDIENTE = "Pendiente";
    public static final String APROBADA = "Aprobada";
    public static final String RECHAZADA = "Rechazada";

    @Autowired
    private SolicitudCreditoRepository solicitudRepository;

    @Autowired
    private CuentaCorrienteRepository cuentaCorrienteRepository;

    public List<SolicitudCredito> obtenerTodas() {
        return solicitudRepository.findAll();
    }

    public List<SolicitudCredito> obtenerPorUsuario(Long usuarioId) {
        return solicitudRepository.findByUsuarioIdOrderByIdDesc(usuarioId);
    }

    /**
     * Límite de crédito de una cuenta = crédito disponible + saldo pendiente.
     * (La cuenta no guarda el límite directamente; se deduce de estos dos valores.)
     */
    private double limiteDe(CuentaCorriente cuenta) {
        double disponible = cuenta.getCreditoDisponible() != null ? cuenta.getCreditoDisponible() : 0;
        double pendiente = cuenta.getSaldoPendiente() != null ? cuenta.getSaldoPendiente() : 0;
        return disponible + pendiente;
    }

    /** El cliente crea una solicitud. Solo se acepta si tiene cuenta y pide más que su límite actual. */
    public SolicitudCredito crear(SolicitudCredito solicitud) {
        if (solicitud.getUsuarioId() == null || solicitud.getMontoSolicitado() == null) {
            throw new IllegalArgumentException("Faltan datos de la solicitud.");
        }
        CuentaCorriente cuenta = cuentaCorrienteRepository.findByUsuarioId(solicitud.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("El cliente no tiene cuenta corriente."));

        double limiteActual = limiteDe(cuenta);
        if (solicitud.getMontoSolicitado() <= limiteActual) {
            throw new IllegalArgumentException("El monto solicitado debe ser mayor a tu límite actual.");
        }

        solicitud.setId(null);
        solicitud.setMontoActual(limiteActual);   // se toma del servidor, no del cliente
        solicitud.setEstado(PENDIENTE);
        solicitud.setFecha(LocalDate.now());
        return solicitudRepository.save(solicitud);
    }

    /**
     * El administrador aprueba o rechaza una solicitud pendiente.
     * Si la aprueba, sube el crédito disponible de la cuenta para que el nuevo límite
     * (disponible + pendiente) quede igual al monto solicitado.
     */
    @Transactional
    public SolicitudCredito responder(Long id, String accion) {
        if (!APROBADA.equals(accion) && !RECHAZADA.equals(accion)) {
            throw new IllegalArgumentException("Acción inválida. Usa 'Aprobada' o 'Rechazada'.");
        }
        SolicitudCredito solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada."));
        if (!PENDIENTE.equals(solicitud.getEstado())) {
            throw new IllegalStateException("La solicitud ya fue respondida.");
        }

        if (APROBADA.equals(accion)) {
            CuentaCorriente cuenta = cuentaCorrienteRepository.findByUsuarioId(solicitud.getUsuarioId())
                    .orElseThrow(() -> new IllegalArgumentException("El cliente no tiene cuenta corriente."));
            double limiteActual = limiteDe(cuenta);
            double diferencia = solicitud.getMontoSolicitado() - limiteActual;
            if (diferencia > 0) {
                cuenta.setCreditoDisponible(cuenta.getCreditoDisponible() + diferencia);
                cuentaCorrienteRepository.save(cuenta);
            }
        }

        solicitud.setEstado(accion);
        return solicitudRepository.save(solicitud);
    }
}