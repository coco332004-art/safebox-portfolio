package com.proyecto.safebox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Esta clase Configura Spring Security para toda la aplicación. Define qué rutas requieren autenticación, qué rutas son públicas y qué roles pueden acceder a cada recurso.
// También desactiva CSRF para simplificar pruebas locales y permite el acceso al H2 Console.
// CSRF (Cross-Site Request Forgery) es una vulnerabilidad de seguridad que permite a un atacante inducir a un usuario autenticado a realizar acciones no deseadas en una aplicación web. 
// En Spring Boot, se utiliza para proteger las aplicaciones contra este tipo de ataques.

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean //Bean es un objeto que es creado, configurado y gestionado por el contenedor de Spring.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { // reglas de authentificación y autorización
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/css/**", "/index", "/h2-console/**", "/api/health").permitAll()
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/perfil").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/mensajes/**").hasAnyRole("ADMIN", "USER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )

            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            );

        http.csrf(csrf -> csrf.disable());
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean //Declara un bean PasswordEncoder que utiliza BCrypt para cifrar las contraseñas de los usuarios antes de almacenarlas en la base de datos.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
