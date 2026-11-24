package duoc.cl.ferreteria.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Permite H2 Console
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Rutas públicas (Auth, Swagger, H2 y consultas de catálogo)
                        .requestMatchers(
                                "/api/v1/usuarios/login",
                                "/api/v1/usuarios/registro",
                                "/api/v1/usuarios/refresh",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/h2-console/**"
                        ).permitAll()

                        // 2. Productos: alerta de stock bajo solo Admin/Vendedor; lectura pública; escritura Admin/Vendedor
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/stock-bajo").hasAnyRole("ADMINISTRADOR", "VENDEDOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()
                        .requestMatchers("/api/v1/productos/**").hasAnyRole("ADMINISTRADOR", "VENDEDOR")

                        // 3. Pedidos: crear y ver propios (todos los roles autenticados); listar todos/actualizar estado/eliminar (Admin y Vendedor)
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos").hasAnyRole("ADMINISTRADOR", "VENDEDOR", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/cliente/**").hasAnyRole("ADMINISTRADOR", "VENDEDOR", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/**").hasAnyRole("ADMINISTRADOR", "VENDEDOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/pedidos/**").hasAnyRole("ADMINISTRADOR", "VENDEDOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/pedidos/**").hasAnyRole("ADMINISTRADOR", "VENDEDOR")

                        // 4. Cuentas corrientes: consultar por cliente y abonar (Admin y Cliente); listar todas y crear (Admin)
                        .requestMatchers(HttpMethod.GET, "/api/v1/cuentas-corrientes/cliente/**").hasAnyRole("ADMINISTRADOR", "CLIENTE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/cuentas-corrientes/*/abonar").hasAnyRole("ADMINISTRADOR", "CLIENTE")
                        .requestMatchers("/api/v1/cuentas-corrientes/**").hasRole("ADMINISTRADOR")

                        // 5. Gestión de usuarios: exclusiva de Administrador
                        .requestMatchers("/api/v1/usuarios/**").hasRole("ADMINISTRADOR")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}