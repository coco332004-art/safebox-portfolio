package com.proyecto.safebox.service;

import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import com.proyecto.safebox.model.Mensajes;
import com.proyecto.safebox.model.Usuarios;
import com.proyecto.safebox.repository.MensajesRepository;
import com.proyecto.safebox.repository.UsuariosRepository;

import org.springframework.transaction.annotation.Transactional;

// Contrato de servicio (interfaz de negocio) para la gestión de mensajes. 
// Define las operaciones que debe soportar la capa de mensajería segura (listar, obtener, crear, actualizar y eliminar).

public class MensajesService {

    private final MensajesRepository repo;
    private final UsuariosRepository usuariosRepo;

    public MensajesService(MensajesRepository repo, UsuariosRepository usuariosRepo) {
        this.repo = repo;
        this.usuariosRepo = usuariosRepo;
    }

    // ===== Métodos "normales" =====

    public List<Mensajes> listar() {
        return repo.findAll();
    }

    public List<Mensajes> listarPorUsuario(Long usuarioId) {
        return repo.findByUsuarioId(usuarioId);
    }

    public Optional<Mensajes> obtener(Long id) {
        return repo.findById(id);
    }

    @Transactional
    public Mensajes crear(Mensajes m, Long usuarioId) {
        Usuarios u = usuariosRepo.findById(usuarioId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        m.setUsuario(u);
        return repo.save(m);
    }

    @Transactional
    public Mensajes actualizar(Long id, Mensajes m) {
        Mensajes actual = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Mensaje no encontrado"));
        actual.setTitulo(m.getTitulo());
        actual.setContenido(m.getContenido());
        return repo.save(actual);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    // ===== Nuevos métodos para ADMIN (sin descifrar) =====

    public List<Mensajes> listarSinDescifrar() {
        // Para admin: lista directa desde repo (sin aplicar lógica extra)
        return repo.findAll();
    }

    public List<Mensajes> listarPorUsuarioSinDescifrar(Long usuarioId) {
        return repo.findByUsuarioId(usuarioId);
    }
}
