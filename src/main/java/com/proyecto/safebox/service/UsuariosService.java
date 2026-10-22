package com.proyecto.safebox.service;

import java.util.List;
import java.util.Optional;
import com.proyecto.safebox.model.Usuarios;
import com.proyecto.safebox.repository.UsuariosRepository;

// Servicio (o contrato) para la gestión básica de usuarios apoyado en UsuariosRepository. 
// En esta versión actúa como una capa sencilla de acceso a datos para crear, listar, obtener, actualizar y eliminar usuarios.

public class UsuariosService {

  private final UsuariosRepository repo;

    public UsuariosService(UsuariosRepository repo) { 
      this.repo = repo; }

    public List<Usuarios> listar() {
       return repo.findAll(); }

    public Optional<Usuarios> obtener(Long id) {
       return repo.findById(id); }

    public Usuarios crear(Usuarios u) {
       return repo.save(u); }

    public Usuarios actualizar(Long id, Usuarios u) {
        u.setId(id);
        return repo.save(u);
    }

    public void eliminar(Long id) {
       repo.deleteById(id); }
}
