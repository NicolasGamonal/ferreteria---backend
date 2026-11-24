package duoc.cl.ferreteria.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    // Clave secreta >= 32 caracteres para firmar con HMAC-SHA256
    @Value("${jwt.secret:ClaveSecretaSuperSeguraFerreteriaDonJuan2026!!}")
    private String secret;

    @Value("${jwt.expiration:3600000}") // 1 hora (en ms)
    private long accessExpiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7 días (en ms)
    private long refreshExpiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generarAccessToken(String email, String rol) {
        return construirToken(email, rol, "ACCESS", accessExpiration);
    }

    public String generarRefreshToken(String email, String rol) {
        return construirToken(email, rol, "REFRESH", refreshExpiration);
    }

    private String construirToken(String email, String rol, String tipo, long duracionMs) {
        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .claim("tipo", tipo)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + duracionMs))
                .signWith(getKey())
                .compact();
    }

    public Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extraerEmail(String token) {
        return extraerClaims(token).getSubject();
    }

    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    public boolean esTokenValido(String token, String tipoEsperado) {
        try {
            Claims claims = extraerClaims(token);
            boolean noExpirado = claims.getExpiration().after(new Date());
            boolean tipoCorrecto = tipoEsperado.equals(claims.get("tipo", String.class));
            return noExpirado && tipoCorrecto;
        } catch (Exception e) {
            return false;
        }
    }
}