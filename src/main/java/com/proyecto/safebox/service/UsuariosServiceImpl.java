
package com.proyecto.safebox.service;

import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.proyecto.safebox.model.Usuarios;
import com.proyecto.safebox.repository.UsuariosRepository;
import jakarta.persistence.EntityNotFoundException;

// Implementación más completa de la gestión de usuarios que incorpora reglas de negocio adicionales como la codificación de contraseñas con BCrypt 
// y la validación de correos duplicados. Opera siempre sobre la entidad Usuarios y se integra con la capa de seguridad.

@Service
@Transactional
public class UsuariosServiceImpl extends UsuariosService {

  private final UsuariosRepository repo;
  private final PasswordEncoder passwordEncoder;

  public UsuariosServiceImpl(UsuariosRepository repo, PasswordEncoder passwordEncoder) {
    super(repo);
    this.repo = repo;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Usuarios crear(Usuarios u) {
    if (repo.existsByEmail(u.getEmail())) {
      throw new IllegalArgumentException("El email ya está registrado.");
    }
    u.setPassword(passwordEncoder.encode(u.getPassword()));
    return repo.save(u);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Usuarios> listar() {
    return repo.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Usuarios> obtener(Long id) {
    return repo.findById(id);
  }

  @Override
  public Usuarios actualizar(Long id, Usuarios data) {
    
    Usuarios u = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        // 2. Validar email duplicado solo si pertenece a OTRO usuario
        if (repo.existsByEmailAndIdNot(data.getEmail(), id)) {
            throw new IllegalArgumentException("El correo ya está registrado por otro usuario.");
        }

    // Actualizar campos permitidos
    u.setNombreCompleto(data.getNombreCompleto());
    u.setEmail(data.getEmail());

    // Solo actualiza la contraseña si viene no vacía
    if (data.getPassword() != null && !data.getPassword().isBlank()) {
      u.setPassword(passwordEncoder.encode(data.getPassword()));
    }

     u.setRol(data.getRol());
     u.setEnabled(data.isEnabled());

    return repo.save(u);
  }

  @Override
  public void eliminar(Long id) {
    if (!repo.existsById(id)) {
      throw new EntityNotFoundException("Usuario no encontrado");
    }
    repo.deleteById(id);
  }
}
