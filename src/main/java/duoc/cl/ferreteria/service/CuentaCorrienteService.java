package duoc.cl.ferreteria.service;

import duoc.cl.ferreteria.model.CuentaCorriente;
import duoc.cl.ferreteria.repository.CuentaCorrienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CuentaCorrienteService {

    @Autowired
    private CuentaCorrienteRepository cuentaCorrienteRepository;

    public List<CuentaCorriente> obtenerTodas() {
        return cuentaCorrienteRepository.findAll();
    }

    public Optional<CuentaCorriente> obtenerPorEmail(String email) {
        return cuentaCorrienteRepository.findByUsuarioEmail(email);
    }

    public CuentaCorriente guardar(CuentaCorriente cuenta) {
        return cuentaCorrienteRepository.save(cuenta);
    }

    public CuentaCorriente registrarAbono(Long id, Double montoAbono) {
        return cuentaCorrienteRepository.findById(id).map(cuenta -> {
            double nuevoSaldo = Math.max(0, cuenta.getSaldoPendiente() - montoAbono);
            cuenta.setSaldoPendiente(nuevoSaldo);
            cuenta.setCreditoDisponible(cuenta.getCreditoDisponible() + montoAbono);
            return cuentaCorrienteRepository.save(cuenta);
        }).orElseThrow(() -> new RuntimeException("Cuenta corriente no encontrada"));
    }
}