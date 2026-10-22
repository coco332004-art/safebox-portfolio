package com.proyecto.safebox.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.safebox.model.Usuarios;

// Repositorio Spring Data JPA para la entidad Usuarios. Hereda de JpaRepository, por lo que dispone de todas las operaciones CRUD básicas, 
// y define además consultas específicas por correo electrónico.

public interface UsuariosRepository extends JpaRepository<Usuarios, Long> { 

    boolean existsByEmail(String email);//devuelve true o false dependiendo de si ya existe un usuario con ese correo electrónico.
    
    boolean existsByEmailAndIdNot(String email, Long id); //Devuelve true si existe otro usuario distinto al indicado por id que ya utiliza ese correo electrónico. Es clave para validar duplicidad al actualizar usuarios.
    
    Optional<Usuarios> findByEmail(String email); // Busca un usuario por su correo electrónico y lo devuelve envuelto en Optional. Es el método utilizado por el servicio de autenticación de Spring Security.

}
