package com.proyecto.safebox;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.proyecto.safebox.model.Usuarios;
import com.proyecto.safebox.repository.UsuariosRepository;

// Clase principal de la aplicación Spring Boot. Configura y arranca la aplicación.

@SpringBootApplication
public class SafeboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafeboxApplication.class, args); 
    }

    @Bean
    CommandLineRunner initUsers(UsuariosRepository usuariosRepository,
                                PasswordEncoder passwordEncoder,
                                @Value("${safebox.admin.email:}") String adminEmail,
                                @Value("${safebox.admin.password:}") String adminPassword) {
        return args -> {
            if (usuariosRepository.count() == 0) {
                if (adminEmail.isBlank() || adminPassword.isBlank()) {
                    throw new IllegalStateException(
                        "Configure SAFEBOX_ADMIN_EMAIL and SAFEBOX_ADMIN_PASSWORD to create the initial administrator."
                    );
                }

                Usuarios admin = new Usuarios();
                admin.setNombreCompleto("Admin SafeBox");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRol(Usuarios.Role.ADMIN);
                admin.setEnabled(true);

                usuariosRepository.save(admin);

                System.out.println("Usuario administrador inicial creado para: " + adminEmail);
            }
        };
    }
}
